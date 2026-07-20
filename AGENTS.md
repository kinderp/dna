# DNA instructions for contributors and AI coding agents

DNA is a modular ecosystem spanning mobile applications, navigation, commerce, social coordination, personal economy, shared platform services and integrations such as Alfred. It is also an educational codebase. A change is not complete merely because it compiles: contracts, tests, privacy, performance and teaching documentation are part of the work.

## Always read first

Before changing code or stable documentation, read:

1. `docs/governance/00-operational-rules.md`
2. `docs/governance/01-review-and-merge.md`
3. `docs/it/README.md`
4. the current issue and the component contract relevant to the task
5. `docs/migration/tdna-import.md` when touching Travel history or repository structure
6. `docs/governance/02-git-monorepo.md` before changing repository layout or Travel history

Do not read every document blindly. Select the smallest coherent set for the bounded context and surface involved.

## Before modifying anything

Record:

1. user-visible use case;
2. owning bounded context;
3. affected surfaces: mobile, Android Auto, Automotive, backend or tooling;
4. canonical contract that may change;
5. external provider and adapter boundary;
6. hot-path impact;
7. permissions and personal data;
8. tests, replay or benchmark evidence;
9. documentation that must move with the change;
10. whether the work is current scope or future roadmap;
11. current `main` SHA and open PR inventory.

## Architectural invariants

- Domains depend on DNA contracts, not concrete providers or platform UI types.
- Provider-specific responses are translated at adapter boundaries.
- Travel, Shopping, Social, Economy and Commons remain bounded contexts even inside one repository.
- A domain must not read another domain's storage directly.
- The common map surface receives layers and actions from domains; domains do not fork independent map platforms.
- Android Auto exposes driver-safe tasks, not the entire mobile domain switcher.
- Alfred is an asynchronous observation and correlation plane, not the event backbone or operational database.
- Communication transports are capability-based adapters; LoRa, BLE, NFC, Wi-Fi and Internet do not enter domain models.
- Location, identity, finance, messages and contacts are minimized by purpose and retention.
- A degraded provider must not disable unrelated useful behavior.
- No real private trace, photo, receipt, contact list or conversation is committed without explicit anonymization and review.

## Work style

- Work in small coherent vertical slices.
- Architecture must pay rent.
- Prefer deterministic fixtures and replay before field testing.
- Update behavior, tests and stable documentation together.
- State expected cost for hot-path changes.
- Include abuse cases and data-flow review for privacy-sensitive changes.
- Do not duplicate a document without classifying its authority.
- Travel changes are committed directly under `domains/travel`.
- Before running Travel checks, execute `sh tools/dna check-travel-history`.

## Serial pull-request workflow

The repository may contain at most one open pull request.

```text
verify no PR is open
-> read current main SHA
-> create branch from main
-> implement one coherent issue slice
-> open one draft PR
-> CI, findings and fixes
-> clean review round 1
-> clean review round 2 on the same substantive head
-> ready and authorized merge with expected-head guard
-> verify new main
-> only then start the next PR
```

No stacked, placeholder or `noop` PRs. A substantive commit after a clean round resets the count. CI does not replace review, and review does not replace CI.

## Merge authority

An agent may mark ready and merge only when the maintainer has authorized it, the PR is the only open PR, applicable CI is green on the reviewed SHA, no finding remains, two clean rounds are recorded on the same SHA and the merge uses an expected-head guard.

## Source of truth

```text
Discussion       = open reasoning
ADR              = architectural decision
Platform docs    = shared consolidated explanation
Domain docs      = domain-specific contracts and teaching material
Issue            = work to perform
Pull request     = the single active reviewable change
PR review ledger = same-head evidence
Tests            = executable evidence
Benchmarks       = measured cost
Git history      = chronological evidence
```

## Scope guard

Future vision does not authorize implementation. Do not introduce production payments, unrestricted social matching, minors' accounts, full LoRa deployment, banking operations, production navigation claims or autonomous enforcement unless a reviewed milestone explicitly includes them.
