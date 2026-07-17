#!/usr/bin/env python3
"""Validate local Markdown links and balanced fenced code blocks.

The checker intentionally avoids network access. It validates repository-local
paths and treats URL/anchor links as external contracts checked elsewhere.
"""

from __future__ import annotations

import re
import sys
from pathlib import Path
from urllib.parse import unquote

LINK_PATTERN = re.compile(r"(?<!!)\[[^\]]*\]\(([^)]+)\)")
FENCE_PATTERN = re.compile(r"^\s*(```+|~~~+)")
IGNORED_PREFIXES = ("http://", "https://", "mailto:", "tel:", "#")


def iter_markdown(root: Path):
    for path in sorted(root.rglob("*.md")):
        if ".git" not in path.parts and "build" not in path.parts:
            yield path


def normalize_target(raw: str) -> str:
    target = raw.strip()
    if target.startswith("<") and ">" in target:
        target = target[1 : target.index(">")]
    elif " " in target:
        target = target.split(" ", 1)[0]
    return unquote(target.split("#", 1)[0])


def check_file(root: Path, path: Path) -> list[str]:
    errors: list[str] = []
    text = path.read_text(encoding="utf-8")
    open_fence: str | None = None

    for number, line in enumerate(text.splitlines(), start=1):
        fence = FENCE_PATTERN.match(line)
        if fence:
            marker = fence.group(1)[0]
            if open_fence is None:
                open_fence = marker
            elif open_fence == marker:
                open_fence = None

        for match in LINK_PATTERN.finditer(line):
            target = match.group(1).strip()
            if not target or target.startswith(IGNORED_PREFIXES):
                continue
            local = normalize_target(target)
            if not local:
                continue
            resolved = (path.parent / local).resolve()
            try:
                resolved.relative_to(root.resolve())
            except ValueError:
                errors.append(f"{path.relative_to(root)}:{number}: link escapes repository: {target}")
                continue
            if not resolved.exists():
                errors.append(f"{path.relative_to(root)}:{number}: missing link target: {target}")

    if open_fence is not None:
        errors.append(f"{path.relative_to(root)}: unclosed fenced code block")
    return errors


def main(argv: list[str]) -> int:
    if len(argv) != 2:
        print("usage: check_docs.py REPOSITORY_ROOT", file=sys.stderr)
        return 2
    root = Path(argv[1]).resolve()
    errors: list[str] = []
    files = list(iter_markdown(root))
    for path in files:
        errors.extend(check_file(root, path))
    if errors:
        print("Documentation validation failed:", file=sys.stderr)
        for error in errors:
            print(f"- {error}", file=sys.stderr)
        return 1
    print(f"PASS documentation: {len(files)} Markdown files")
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv))
