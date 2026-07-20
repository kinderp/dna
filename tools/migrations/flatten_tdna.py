#!/usr/bin/env python3
"""Flatten the transitional TDNA submodule into the DNA monorepo.

This script is intentionally executed by a one-shot GitHub Actions workflow on
`agent/flatten-travel-history`. It performs real Git operations so the TDNA
commit graph remains reachable from the resulting DNA history.
"""

from __future__ import annotations

import os
import shutil
import subprocess
import textwrap
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
TRAVEL = ROOT / "domains" / "travel"
EXPECTED_TDNA_SHA = "85c73ab78dd56506c5595673098adf514765de9c"
TDNA_URL = "https://github.com/kinderp/tdna.git"


def run(*args: str, cwd: Path = ROOT, capture: bool = False) -> str:
    print("+", " ".join(args), flush=True)
    result = subprocess.run(
        args,
        cwd=cwd,
        check=True,
        text=True,
        stdout=subprocess.PIPE if capture else None,
        stderr=subprocess.STDOUT if capture else None,
    )
    return result.stdout.strip() if capture else ""


def write(path: str, content: str) -> None:
    target = ROOT / path
    target.parent.mkdir(parents=True, exist_ok=True)
    target.write_text(textwrap.dedent(content).lstrip(), encoding="utf-8")


def prepend_once(path: Path, marker: str, block: str) -> None:
    content = path.read_text(encoding="utf-8")
    if marker in content:
        return
    path.write_text(textwrap.dedent(block).lstrip() + "\n" + content, encoding="utf-8")


def verify_phase_a() -> None:
    if not (ROOT / ".gitmodules").is_file():
        raise RuntimeError(".gitmodules is missing; Phase A is not present")
    if not (TRAVEL / "tools" / "tdna").is_file():
        raise RuntimeError("Travel submodule is not initialized")

    checked_out = run("git", "rev-parse", "HEAD", cwd=TRAVEL, capture=True)
    if checked_out != EXPECTED_TDNA_SHA:
        raise RuntimeError(
            f"Travel checkout mismatch: expected {EXPECTED_TDNA_SHA}, got {checked_out}"
        )

    local_changes = run("git", "status", "--porcelain", cwd=TRAVEL, capture=True)
    if local_changes:
        raise RuntimeError(f"Travel submodule has local changes:\n{local_changes}")

    gitlink = run(
        "git", "ls-files", "--stage", "--", "domains/travel", capture=True
    )
    expected_record = f"160000 {EXPECTED_TDNA_SHA} 0\tdomains/travel"
    if gitlink != expected_record:
        raise RuntimeError(f"Unexpected Travel gitlink:\n{gitlink}")


def remove_submodule_bridge() -> None:
    run("git", "submodule", "deinit", "-f", "--", "domains/travel")
    run("git", "rm", "-f", "domains/travel")
    run("git", "rm", ".gitmodules")
    shutil.rmtree(ROOT / ".git" / "modules" / "domains" / "travel", ignore_errors=True)
    run("git", "commit", "-m", "Remove transitional Travel submodule")


def import_tdna_history() -> None:
    remotes = run("git", "remote", capture=True).splitlines()
    if "tdna" in remotes:
        run("git", "remote", "remove", "tdna")
    run("git", "remote", "add", "tdna", TDNA_URL)
    run("git", "fetch", "tdna", "main")

    fetched = run("git", "rev-parse", "tdna/main", capture=True)
    if fetched != EXPECTED_TDNA_SHA:
        raise RuntimeError(
            "TDNA main moved after Phase A. Refusing to import an unreviewed revision: "
            f"expected {EXPECTED_TDNA_SHA}, got {fetched}"
        )

    run(
        "git",
        "subtree",
        "add",
        "--prefix=domains/travel",
        "tdna",
        "main",
        "-m",
        "Import TDNA history under domains/travel",
    )

    run("git", "cat-file", "-e", f"{EXPECTED_TDNA_SHA}^{{commit}}")
    run("git", "merge-base", "--is-ancestor", EXPECTED_TDNA_SHA, "HEAD")


def update_root_tooling() -> None:
    write(
        "tools/dna",
        f'''\
        #!/bin/sh
        set -eu

        ROOT=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
        COMMAND=${{1:-help}}
        IMPORTED_TDNA_SHA={EXPECTED_TDNA_SHA}

        require_travel() {{
          if [ ! -f "$ROOT/domains/travel/tools/tdna" ]; then
            echo "Travel domain is missing from domains/travel" >&2
            exit 2
          fi
        }}

        check_travel_history() {{
          require_travel

          if [ -e "$ROOT/.gitmodules" ]; then
            echo "Unexpected .gitmodules: the transitional submodule should be gone" >&2
            exit 1
          fi

          if git -C "$ROOT" ls-files --stage -- domains/travel | grep -q '^160000 '; then
            echo "domains/travel is still recorded as a gitlink" >&2
            exit 1
          fi

          if [ -e "$ROOT/domains/travel/.git" ]; then
            echo "domains/travel still contains nested Git metadata" >&2
            exit 1
          fi

          git -C "$ROOT" cat-file -e "$IMPORTED_TDNA_SHA^{{commit}}"
          git -C "$ROOT" merge-base --is-ancestor "$IMPORTED_TDNA_SHA" HEAD
          echo "PASS Travel history: $IMPORTED_TDNA_SHA is reachable and domains/travel is flattened"
        }}

        case "$COMMAND" in
          help)
            cat <<'EOF'
        usage: sh tools/dna COMMAND

        commands:
          doctor                  show local tool and Travel import state
          check-ci-actions        validate immutable GitHub Action pins
          check-core-contracts    validate DNA JSON schemas and examples
          check-core-reference    compile and test the Kotlin reference implementation
          check-travel-history    verify the flattened TDNA history and directory
          check-travel            run the TDNA non-emulator verification suite
          check-travel-emulator   run the isolated Android emulator verification
          check                   run all non-emulator DNA and Travel checks
        EOF
            ;;
          doctor)
            python3 --version
            java -version
            git --version
            if [ -f "$ROOT/domains/travel/tools/tdna" ]; then
              echo "Travel directory: present"
              if git -C "$ROOT" merge-base --is-ancestor "$IMPORTED_TDNA_SHA" HEAD; then
                echo "Travel source history: reachable ($IMPORTED_TDNA_SHA)"
              else
                echo "Travel source history: NOT reachable ($IMPORTED_TDNA_SHA)"
              fi
            else
              echo "Travel directory: missing"
            fi
            ;;
          check-ci-actions)
            python3 "$ROOT/tools/check_ci_actions.py" "$ROOT"
            ;;
          check-core-contracts)
            python3 "$ROOT/scripts/validate-schemas.py"
            ;;
          check-core-reference)
            "$ROOT/scripts/test-reference.sh"
            ;;
          check-travel-history)
            check_travel_history
            ;;
          check-travel)
            check_travel_history
            (cd "$ROOT/domains/travel" && sh tools/tdna check)
            ;;
          check-travel-emulator)
            check_travel_history
            (cd "$ROOT/domains/travel" && sh tools/tdna check-android-emulator)
            ;;
          check)
            "$0" check-ci-actions
            "$0" check-core-contracts
            "$0" check-core-reference
            "$0" check-travel
            ;;
          *)
            echo "unknown command: $COMMAND" >&2
            exit 2
            ;;
        esac
        ''',
    )
    os.chmod(ROOT / "tools" / "dna", 0o755)

    write(
        "Makefile",
        '''\
        .PHONY: test core travel-history travel travel-emulator schemas kotlin demo ci-actions doctor

        test: core

        core: ci-actions schemas kotlin

        schemas:
        \tpython3 scripts/validate-schemas.py

        kotlin:
        \t./scripts/test-reference.sh

        travel-history:
        \tsh tools/dna check-travel-history

        travel:
        \tsh tools/dna check-travel

        travel-emulator:
        \tsh tools/dna check-travel-emulator

        ci-actions:
        \tsh tools/dna check-ci-actions

        doctor:
        \tsh tools/dna doctor

        demo:
        \t./scripts/run-demo.sh
        ''',
    )


def update_workflows() -> None:
    write(
        ".github/workflows/travel-ci.yml",
        '''\
        name: Travel Domain CI

        on:
          pull_request:
            branches: [main]
            paths:
              - "domains/travel/**"
              - "tools/dna"
              - ".github/workflows/travel-ci.yml"
          push:
            branches: [main]
            paths:
              - "domains/travel/**"
              - "tools/dna"
              - ".github/workflows/travel-ci.yml"
          workflow_dispatch:

        concurrency:
          group: ${{ github.workflow }}-${{ github.event.pull_request.number || github.ref }}
          cancel-in-progress: true

        permissions:
          contents: read

        jobs:
          travel:
            name: TDNA checks without emulator
            runs-on: ubuntu-latest
            timeout-minutes: 45
            steps:
              - name: Check out exact head
                uses: actions/checkout@34e114876b0b11c390a56381ad16ebd13914f8d5 # v4
                with:
                  ref: ${{ github.event.pull_request.head.sha || github.sha }}
                  fetch-depth: 0
                  submodules: false
              - name: Set up Java 21
                uses: actions/setup-java@c1e323688fd81a25caa38c78aa6df2d33d3e20d9 # v4
                with:
                  distribution: temurin
                  java-version: "21"
              - name: Verify hosted Android SDK 37
                shell: bash
                run: |
                  set -euo pipefail
                  test -n "${ANDROID_HOME:-}"
                  platform_jar=$(find "${ANDROID_HOME}/platforms" -maxdepth 2 -type f -path '*/android-37*/android.jar' | sort | sed -n '1p')
                  build_tools=$(find "${ANDROID_HOME}/build-tools" -maxdepth 1 -type d -name '36.0.0' | sort | sed -n '1p')
                  test -n "$platform_jar"
                  test -n "$build_tools"
                  printf 'ANDROID_HOME=%s\nplatform=%s\nbuild-tools=%s\n' "$ANDROID_HOME" "$platform_jar" "$build_tools"
              - name: Configure Gradle cache
                uses: gradle/actions/setup-gradle@3f131e8634966bd73d06cc69884922b02e6faf92 # v6
                with:
                  cache-provider: basic
                  add-job-summary: on-failure
              - name: Install stable Rust toolchain
                run: |
                  rustup toolchain install stable --profile minimal --component rustfmt
                  rustup default stable
              - name: Verify imported history and Travel
                run: sh tools/dna check-travel
        ''',
    )

    write(
        ".github/workflows/travel-emulator.yml",
        '''\
        name: Travel Android Emulator Evidence

        on:
          workflow_dispatch:

        concurrency:
          group: ${{ github.workflow }}-${{ github.ref }}
          cancel-in-progress: true

        permissions:
          contents: read

        jobs:
          emulator:
            name: Isolated TDNA emulator smoke
            runs-on: ubuntu-latest
            timeout-minutes: 35
            steps:
              - name: Check out exact head
                uses: actions/checkout@34e114876b0b11c390a56381ad16ebd13914f8d5 # v4
                with:
                  ref: ${{ github.sha }}
                  fetch-depth: 0
                  submodules: false
              - name: Set up Java 21
                uses: actions/setup-java@c1e323688fd81a25caa38c78aa6df2d33d3e20d9 # v4
                with:
                  distribution: temurin
                  java-version: "21"
              - name: Configure Gradle cache
                uses: gradle/actions/setup-gradle@3f131e8634966bd73d06cc69884922b02e6faf92 # v6
                with:
                  cache-provider: basic
                  add-job-summary: on-failure
              - name: Enable KVM access
                shell: bash
                run: |
                  set -euo pipefail
                  echo 'KERNEL=="kvm", GROUP="kvm", MODE="0666", OPTIONS+="static_node=kvm"' | sudo tee /etc/udev/rules.d/99-kvm4all.rules
                  sudo udevadm control --reload-rules
                  sudo udevadm trigger --name-match=kvm
              - name: Run isolated emulator evidence
                env:
                  TDNA_SUBSTANTIVE_SHA: ${{ github.sha }}
                  TDNA_EMULATOR_API_LEVEL: "35"
                  TDNA_EMULATOR_ABI: x86_64
                  TDNA_EMULATOR_BOOT_TIMEOUT_SECONDS: "360"
                run: sh tools/dna check-travel-emulator
              - name: Upload bounded emulator evidence
                if: always()
                uses: actions/upload-artifact@ea165f8d65b6e75b540449e92b4886f43607fa02 # v4
                with:
                  name: travel-android-emulator-observations
                  if-no-files-found: error
                  retention-days: 14
                  path: |
                    domains/travel/build/android-emulator/*.json
                    domains/travel/build/android-emulator/*.txt
                    domains/travel/build/android-emulator/*.log
                    domains/travel/build/android-emulator/*.png
                    domains/travel/apps/android/build/reports/androidTests/connected/**
                    domains/travel/apps/android/build/outputs/androidTest-results/connected/**
        ''',
    )


def update_documentation() -> None:
    old_guide = ROOT / "docs" / "governance" / "02-git-submodules.md"
    historical_guide = ROOT / "docs" / "migration" / "phase-a-git-submodules.md"
    historical_guide.parent.mkdir(parents=True, exist_ok=True)
    if old_guide.exists():
        old_guide.rename(historical_guide)
    prepend_once(
        historical_guide,
        "DOCUMENTO STORICO DELLA FASE A",
        '''\
        > **DOCUMENTO STORICO DELLA FASE A.** `domains/travel` non è più un
        > submodule. Questa guida resta per ricostruire la migrazione e non deve
        > essere usata per il flusso Git corrente.
        ''',
    )

    write(
        "docs/governance/02-git-monorepo.md",
        f'''\
        # Git nel monorepo DNA

        ## Stato corrente

        `domains/travel` è una directory ordinaria del repository DNA. La storia
        del repository TDNA è stata importata e il commit sorgente
        `{EXPECTED_TDNA_SHA}` è raggiungibile dalla cronologia corrente.

        Non servono più `--recurse-submodules`, `git submodule update` o checkout
        separati per modificare Travel.

        ## Primo clone

        ```bash
        git clone https://github.com/kinderp/dna.git
        cd dna
        sh tools/dna doctor
        sh tools/dna check-travel-history
        ```

        ## Aggiornare main

        ```bash
        git status --short
        git switch main
        git pull --ff-only
        sh tools/dna doctor
        ```

        ## Creare una slice

        Prima verificare issue, SHA di `main` e assenza di PR aperte:

        ```bash
        git switch main
        git pull --ff-only
        git switch -c agent/NOME-SLICE
        ```

        I file Travel si modificano direttamente:

        ```bash
        $EDITOR domains/travel/PERCORSO
        git status --short
        git add domains/travel/PERCORSO
        git commit -m "Descrizione coerente della slice"
        git push -u origin agent/NOME-SLICE
        ```

        ## Verifiche

        ```bash
        sh tools/dna check-core-contracts
        sh tools/dna check-core-reference
        sh tools/dna check-travel-history
        sh tools/dna check-travel
        ```

        L'emulatore resta un controllo intenzionale:

        ```bash
        sh tools/dna check-travel-emulator
        ```

        ## Consultare la storia TDNA

        Verificare la provenienza:

        ```bash
        git cat-file -t {EXPECTED_TDNA_SHA}
        git merge-base --is-ancestor {EXPECTED_TDNA_SHA} HEAD
        git log --graph --oneline --all --decorate
        ```

        Il subtree merge mantiene la cronologia TDNA raggiungibile come secondo
        ramo del commit di import. I vecchi path TDNA erano alla root del loro
        repository; i path correnti vivono sotto `domains/travel`.

        ## Regole

        - non aggiungere nuovi submodule per i domini applicativi iniziali;
        - non modificare direttamente storage o internals di un altro dominio;
        - aggiornare contratti, test e documentazione nella stessa PR;
        - usare una sola PR aperta e due review pulite sullo stesso SHA;
        - non archiviare `kinderp/tdna` finché la fase di chiusura non è stata
          revisionata separatamente.

        ## Documento storico

        La precedente procedura submodule è conservata in
        [`docs/migration/phase-a-git-submodules.md`](../migration/phase-a-git-submodules.md).
        ''',
    )

    write(
        "README.md",
        f'''\
        # DNA

        **DNA** è una piattaforma modulare per mettere in relazione persone,
        luoghi, intenzioni e servizi attraverso profili condivisibili, matching
        contestuale, mappe, comunità e diversi canali di comunicazione.

        Travel è il primo dominio applicativo ed è ora incluso direttamente in
        `domains/travel`. La cronologia del precedente repository TDNA è stata
        importata nel monorepo.

        ## Principi

        - DNA privato per impostazione predefinita.
        - Bounded context separati in un solo monorepo.
        - Mappa come superficie territoriale comune.
        - Comunicazione transport-agnostic.
        - Un ecosistema e più esperienze specializzate.
        - Alfred come piano asincrono di osservazione e correlazione.
        - Privacy, sicurezza, prove e documentazione by design.

        ## Checkout

        ```bash
        git clone https://github.com/kinderp/dna.git
        cd dna
        sh tools/dna doctor
        ```

        Non sono più necessari Git submodule.

        ## Controlli

        ```bash
        python3 -m pip install -r requirements-dev.txt
        sh tools/dna check-core-contracts
        sh tools/dna check-core-reference
        sh tools/dna check-travel-history
        sh tools/dna check-travel
        ```

        Il test emulatore Travel è separato e intenzionale:

        ```bash
        sh tools/dna check-travel-emulator
        ```

        ## Provenienza Travel

        - commit TDNA importato: `{EXPECTED_TDNA_SHA}`;
        - dominio corrente: `domains/travel`;
        - [manifest della migrazione](docs/migration/tdna-import.md);
        - [guida Git del monorepo](docs/governance/02-git-monorepo.md);
        - [guida storica della fase submodule](docs/migration/phase-a-git-submodules.md).

        Il repository `kinderp/tdna` resta disponibile finché una successiva
        operazione revisionata non ne stabilirà la modalità read-only e
        l'archiviazione.

        ## Risorse

        - [Documentazione italiana](docs/it/README.md)
        - [Governance](docs/governance/00-operational-rules.md)
        - [Review e merge](docs/governance/01-review-and-merge.md)
        - [Guida Git del monorepo](docs/governance/02-git-monorepo.md)
        - [Schemi JSON v0.1](schemas/v0.1/README.md)
        - [Implementazione Kotlin di riferimento](reference/kotlin/README.md)

        ## Licenza

        Da definire prima della pubblicazione del primo codice riutilizzabile.
        ''',
    )

    write(
        "domains/README.md",
        '''\
        # Domini DNA

        I domini sono bounded context applicativi nello stesso monorepo.

        - `travel` — Travel DNA, importato con la propria cronologia.
        - `shopping` — previsto dopo la stabilizzazione dei contratti comuni.
        - `social` — previsto per interessi e comunità contestuali.
        - `economy` — previsto per gestione economica personale e familiare.
        - `commons` — chat geografiche, gruppi e sottoscrizioni.

        Le decisioni trasversali vivono nella documentazione principale. I
        documenti di dominio non ridefiniscono i contratti comuni.
        ''',
    )

    write(
        "docs/migration/tdna-import.md",
        f'''\
        # Migrazione di TDNA nel monorepo DNA

        ## Stato

        **Fase B implementata nella branch di migrazione, in attesa di review e
        merge.** Il submodule transitorio è stato rimosso e TDNA è stato importato
        sotto `domains/travel` con storia raggiungibile.

        Commit sorgente fissato:

        ```text
        {EXPECTED_TDNA_SHA}
        ```

        ## Verifica

        ```bash
        test ! -e .gitmodules
        sh tools/dna check-travel-history
        git cat-file -e {EXPECTED_TDNA_SHA}^{{commit}}
        git merge-base --is-ancestor {EXPECTED_TDNA_SHA} HEAD
        ```

        ## Fonti autorevoli

        | Tema | Fonte autorevole |
        |---|---|
        | governance comune | `AGENTS.md`, `docs/governance/` |
        | architettura piattaforma | `docs/it/` e ADR di DNA |
        | contratti e codice Travel | `domains/travel/` |
        | documentazione didattica Travel | `domains/travel/docs/` |
        | roadmap cross-domain | DNA |
        | Alfred | `kinderp/alfred` e bridge documentato in DNA |

        ## Metodo usato

        La migrazione ha seguito una subtree merge history-aware:

        ```text
        verifica commit TDNA
        -> rimozione del gitlink transitorio
        -> fetch della storia TDNA
        -> import sotto domains/travel
        -> riallineamento di tooling, CI e documentazione
        -> test core, Travel ed emulatore
        ```

        La guida submodule della fase A è conservata come documento storico in
        [`phase-a-git-submodules.md`](phase-a-git-submodules.md).

        ## Lavoro residuo

        - review e merge della branch di flattening;
        - verifica di `main` dopo il merge;
        - eventuale riconciliazione di issue e link storici;
        - operazione separata per rendere `kinderp/tdna` read-only;
        - archiviazione soltanto dopo un periodo di stabilità del monorepo.
        ''',
    )

    index = ROOT / "docs" / "it" / "README.md"
    index_text = index.read_text(encoding="utf-8")
    index_text = index_text.replace(
        "[Git submodule: guida operativa e didattica](../governance/02-git-submodules.md)",
        "[Git nel monorepo DNA](../governance/02-git-monorepo.md)",
    )
    index_text = index_text.replace(
        "La documentazione tecnica e didattica Travel resta temporaneamente in `domains/travel/docs` durante la fase A della migrazione. Prima di usare quella directory, inizializzare e verificare il submodule seguendo la [guida Git](../governance/02-git-submodules.md).",
        "La documentazione tecnica e didattica Travel vive in `domains/travel/docs`. Travel è una directory ordinaria del monorepo; consultare la [guida Git](../governance/02-git-monorepo.md).",
    )
    index_text = index_text.replace(
        "| **Migration gitlink** | Riferimento temporaneo e verificabile al commit TDNA durante l'import history-aware. |\n",
        "| **Imported Travel history** | Cronologia TDNA raggiungibile dal monorepo e codice corrente sotto `domains/travel`. |\n",
    )
    index_text = index_text.replace(
        "| **Git submodule** | Repository Git figlio collegato dal repository padre mediante un commit preciso. |\n",
        "",
    )
    index.write_text(index_text, encoding="utf-8")

    agents = ROOT / "AGENTS.md"
    agents_text = agents.read_text(encoding="utf-8")
    agents_text = agents_text.replace(
        "5. `docs/migration/tdna-import.md` when touching Travel or repository structure\n6. `docs/governance/02-git-submodules.md` before initializing, updating, repairing or changing the Travel submodule pointer",
        "5. `docs/migration/tdna-import.md` when touching Travel history or repository structure\n6. `docs/governance/02-git-monorepo.md` before changing repository layout or Travel history",
    )
    agents_text = agents_text.replace(
        "- During TDNA migration, do not duplicate a document without classifying its authority.\n- Never use `git submodule update --remote` in the normal workflow; DNA must select a reviewed TDNA commit explicitly.\n- Before running Travel checks, execute `sh tools/dna check-travel-revision`.",
        "- Do not duplicate a document without classifying its authority.\n- Travel changes are committed directly under `domains/travel`.\n- Before running Travel checks, execute `sh tools/dna check-travel-history`.",
    )
    agents.write_text(agents_text, encoding="utf-8")

    rules = ROOT / "docs" / "governance" / "00-operational-rules.md"
    rules_text = rules.read_text(encoding="utf-8")
    rules_text = rules_text.replace(
        "Durante la fase A della migrazione Travel, i comandi Git obbligatori sono spiegati nella [guida ai submodule](02-git-submodules.md).",
        "I comandi Git correnti sono spiegati nella [guida del monorepo](02-git-monorepo.md).",
    )
    rules_text = rules_text.replace(
        "7. per Travel, leggere la guida ai submodule e verificare il commit TDNA registrato nel manifest di migrazione.",
        "7. per Travel, leggere la guida del monorepo e verificare la provenienza TDNA con `sh tools/dna check-travel-history`.",
    )
    rules.write_text(rules_text, encoding="utf-8")

    adr = ROOT / "docs" / "it" / "adr" / "0004-monorepo-and-tdna-migration.md"
    adr_text = adr.read_text(encoding="utf-8")
    adr_text = adr_text.replace(
        "- **Stato:** Accettata per la fase di migrazione",
        "- **Stato:** Implementata; fase submodule storica",
    )
    adr_text += textwrap.dedent(
        f'''\

        ## Esito della fase B

        Il commit TDNA `{EXPECTED_TDNA_SHA}` è stato importato sotto
        `domains/travel` mediante subtree merge. `.gitmodules` e il gitlink sono
        stati rimossi. La cronologia TDNA rimane raggiungibile dalla storia DNA;
        i domini applicativi iniziali vivono ora nello stesso monorepo.
        '''
    )
    adr.write_text(adr_text, encoding="utf-8")

    # Make imported Travel governance subordinate to the monorepo governance.
    prepend_once(
        TRAVEL / "AGENTS.md",
        "MONOREPO PRECEDENCE",
        '''\
        > **MONOREPO PRECEDENCE:** the root `AGENTS.md` and `docs/governance/`
        > are authoritative for branches, pull requests, review and merge. This
        > file adds Travel-specific invariants only.
        ''',
    )
    prepend_once(
        TRAVEL / "docs" / "it" / "00-regole-operative.md",
        "GOVERNANCE SUPERATA",
        '''\
        > **GOVERNANCE SUPERATA:** per branch, PR, review e merge usare
        > `../../../../docs/governance/`. Le sezioni seguenti restano utili per
        > invarianti e percorsi caldi specifici di Travel.
        ''',
    )
    prepend_once(
        TRAVEL / "docs" / "it" / "06-review-e-merge.md",
        "PROCEDURA SUPERATA",
        '''\
        > **PROCEDURA SUPERATA:** la procedura autorevole di review e merge vive
        > in `../../../../docs/governance/01-review-and-merge.md`. Questo testo è
        > conservato come storia del repository TDNA.
        ''',
    )
    prepend_once(
        TRAVEL / "README.md",
        "DOMINIO DEL MONOREPO DNA",
        '''\
        > **DOMINIO DEL MONOREPO DNA:** Travel è sviluppato in
        > `kinderp/dna/domains/travel`. Governance, issue e PR nuove appartengono
        > al repository DNA; questo README descrive il dominio Travel.
        ''',
    )


def final_static_checks() -> None:
    if (ROOT / ".gitmodules").exists():
        raise RuntimeError(".gitmodules still exists")
    if not (TRAVEL / "tools" / "tdna").is_file():
        raise RuntimeError("Flattened Travel directory is incomplete")
    gitlinks = run(
        "git", "ls-files", "--stage", "--", "domains/travel", capture=True
    )
    if any(line.startswith("160000 ") for line in gitlinks.splitlines()):
        raise RuntimeError("Travel is still recorded as a gitlink")
    run("git", "cat-file", "-e", f"{EXPECTED_TDNA_SHA}^{{commit}}")
    run("git", "merge-base", "--is-ancestor", EXPECTED_TDNA_SHA, "HEAD")


def main() -> None:
    os.chdir(ROOT)
    verify_phase_a()
    remove_submodule_bridge()
    import_tdna_history()
    update_root_tooling()
    update_workflows()
    update_documentation()
    final_static_checks()
    print("Flattening prepared successfully", flush=True)


if __name__ == "__main__":
    main()
