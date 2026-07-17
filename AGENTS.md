# Travel DNA instructions for contributors and AI coding agents

Travel DNA is a mobile, backend, mapping, navigation and social-travel project.
It is also an educational codebase. A change is not complete merely because it
compiles: contracts, tests, performance, privacy and teaching documentation are
part of the work.

## Always read first

Before changing code or stable documentation, read:

1. `docs/it/00-regole-operative.md`
2. `docs/it/03-guida-lettura-documentazione.md`
3. `docs/it/documentation-status.md`
4. `docs/it/50-registro-milestone.md`
5. the current milestone roadmap and the component contract relevant to the task

Before reviewing, marking ready, closing or merging a pull request, also read:

6. `docs/it/06-review-e-merge.md`

Do not read every Markdown file blindly. Use the reading guide to select the
smallest coherent set of documents.

## Before modifying anything

Answer these questions in the issue, PR, work log or task note:

1. Which user-visible use case is affected?
2. Which bounded context owns the behavior?
3. Which platforms are affected?
4. Which canonical contract may change?
5. Which third-party provider is involved, and is it still confined to an adapter?
6. Does the change touch a hot path?
7. Which permissions or personal data are involved?
8. Which tests, replay scenarios or benchmarks are required?
9. Which documentation and Lab scenarios must be updated?
10. Is this current milestone scope or future roadmap?

Before creating a branch or PR, inspect the repository state:

```text
current main SHA
open pull requests
active issue and dependency status
last merged slice
```

## Architectural invariants

- Domain and application modules must not import MapLibre, Valhalla, Ferrostar,
  Google Maps, Waze, Sygic or platform UI types.
- Provider-specific responses must be converted into canonical Travel DNA models
  at the adapter boundary.
- The mobile plugin registry is used during composition, not inside the
  location-to-navigation hot loop.
- Navigation, rendering, chat delivery and journey recording have separate
  execution paths and failure policies.
- The app must retain useful behavior when a provider is unavailable.
- External navigation is a first-class mode, not a temporary fallback.
- Location precision and retention must be minimized by design.
- Driver interaction must be designed for voice and low distraction; full text
  interaction belongs to passenger or stopped contexts.
- No real GPS trace, private photo or conversation may be committed as a public
  fixture without explicit anonymization and review.

## Work style

- Work in small, coherent vertical slices.
- Discuss semantics, alternatives and trade-offs before non-trivial structural
  changes.
- Avoid speculative abstractions. Architecture must pay rent.
- Keep vendor replacement possible, but do not rewrite mature libraries without
  a measured product reason.
- Prefer deterministic fixtures and replay before road testing.
- A behavior change must update tests and stable documentation together.
- A hot-path change must state expected cost and the benchmark used or planned.
- A privacy-sensitive change must include abuse cases and a data-flow review.

## Serial pull-request workflow

Normal autonomous development uses **one open pull request at a time**.

The normative sequence is:

```text
verify no ordinary PR is open
-> update local/remote view of main
-> create branch from the current main SHA
-> implement one coherent issue slice
-> open one draft PR
-> findings, fixes and CI
-> two clean reviews on one substantive head
-> ready and authorized merge
-> verify PR merged, issue state and new main SHA
-> only then create the next branch and PR
```

Rules:

- do not open stacked PRs merely to keep working ahead;
- do not create placeholder, empty or `noop` PRs;
- do not open the next PR while the current PR is open;
- after every merge, the next branch starts from the newly verified `main`;
- an issue may be prepared while a PR is open, but its implementation PR waits;
- an exception for parallel PRs requires an explicit maintainer decision recorded
  in the affected issues and PRs;
- accidental, duplicate, stacked or abandoned PRs may be closed administratively
  without two clean rounds because they ship no change, but the reason must be
  explicit and they must not be recorded as completed or merged work.

A branch may be preserved after administrative closure, but it must be rebased,
recreated or otherwise realigned from the future `main` before a new PR is opened.

## Pull request review gate

Every pull request that may ship a change requires **two consecutive clean review
rounds** before it can be marked ready or merged.

For each round record in the PR review timeline or body:

```text
substantive head SHA
risk level
focus
files/contracts inspected
CI and test evidence
findings or no new findings
consecutive clean-round count
```

Rules:

- a finding that requires a repository change resets the count to zero;
- any substantive commit after a clean round resets the count to zero;
- fixes need regression evidence when applicable;
- both clean rounds must inspect the same substantive head SHA;
- the two rounds must use distinct or complementary review focus;
- a CI rerun, PR-body edit, review comment, label or milestone change does not
  reset the count by itself;
- code, tests, contracts, fixtures, workflows, stable docs and technical reports
  are substantive changes;
- the PR review timeline/body is the authoritative same-head review ledger;
- the committed daily report records context, finding history, review plan and a
  link to that ledger, but is not changed after clean rounds merely to copy their
  outcome;
- final review and merge status is reconciled into historical documentation
  through a later reviewed change.

A green CI run is necessary but does not count as a review round. Two clean
reviews are necessary but do not replace CI.

## Merge authority

An agent may mark ready and merge only when all of these are true:

- the maintainer has granted explicit or standing merge authorization;
- exactly one ordinary PR is open;
- the PR head still equals the reviewed substantive SHA;
- required CI is green on that SHA;
- no finding or unresolved review thread remains;
- two consecutive clean rounds are recorded on that same SHA;
- no substantive commit followed the rounds;
- issue, milestone, report and PR body agree with the pre-merge state.

Use an expected-head guard when merging. If the head moved, CI is stale, another
PR appeared, or authorization is unclear, do not merge. Without maintainer
authorization, leave the PR ready for the maintainer.

After merge, verify the merge result, linked issue state and new `main` commit
before starting the next branch.

## Source of truth

```text
Discussion       = open reasoning and alternatives
ADR              = a specific architectural decision
Docs             = consolidated explanation and teaching material
Issue            = work to perform
Pull request     = one concrete reviewable change
PR review ledger = same-head evidence for ready and merge
Tests            = executable evidence
Benchmarks       = measured cost under a declared scenario
Daily index      = historical navigation across work sessions
Git history      = chronological evidence
```

## Scope guard

Do not implement production navigation, LoRa integration, traffic prediction,
full social matching, payments, minors' accounts, a Touring Club integration or
an offline map distribution system unless a milestone explicitly authorizes it.
Future vision documents orient decisions; they do not expand current scope.
