# Documentation package manifest

This package contains the first Travel DNA documentation foundation.

## Root

- `README.md`: public project entry point.
- `AGENTS.md`: mandatory instructions for humans and coding agents.
- `CONTRIBUTING.md`: short contributor entry point.
- `docs/project/MANIFEST.md`: package contents and purpose.
- `docs/project/FOUNDATION-REPORT.md`: coverage, decisions and validation summary.

## Stable documentation

- `docs/it/`: Italian teaching and architecture documentation.
- `docs/adr/`: architecture decision records.
- `docs/commenting-style.md`: language-aware code comment policy.
- `docs/commenting-status.md`: current comment-review status.

## GitHub process

- `.github/pull_request_template.md`
- `.github/ISSUE_TEMPLATE/task.md`
- `.github/ISSUE_TEMPLATE/architecture.md`
- `.github/ISSUE_TEMPLATE/bug.md`
- `.github/ISSUE_TEMPLATE/field-audit.md`

## Teaching artifacts

- `docs/it/lab/scenarios/`: structured scenarios connecting user actions,
  internal stages, data, code paths, tests, performance and safety properties.
- `fixtures/`: future deterministic replay inputs; no real personal data.
- `tools/`: target common developer entry point and tooling rules.

This is a documentation-first foundation. Empty implementation directories are
not included until the corresponding milestone defines their contracts.
