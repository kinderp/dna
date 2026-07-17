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

Before reviewing or merging a pull request, also read:

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

## Pull request review gate

Every pull request requires **two consecutive clean review rounds** before it can
be marked ready, closed as complete or merged.

For each round record in the PR:

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
- a CI rerun, PR-body edit, label or comment does not reset the count by itself;
- code, tests, contracts, fixtures, workflows, stable docs and technical reports
  are substantive changes;
- the final daily report must record the review evidence;
- agents must not merge when merge authority remains with the maintainer.

A green CI run is necessary but does not count as a review round. Two clean
reviews are necessary but do not replace CI.

## Source of truth

```text
Discussion  = open reasoning and alternatives
ADR         = a specific architectural decision
Docs        = consolidated current explanation and teaching material
Issue       = work to perform
Pull request= concrete reviewable change
Tests       = executable evidence
Benchmarks  = measured cost under a declared scenario
Git history = chronological evidence
```

## Scope guard

Do not implement production navigation, LoRa integration, traffic prediction,
full social matching, payments, minors' accounts, a Touring Club integration or
an offline map distribution system unless a milestone explicitly authorizes it.
Future vision documents orient decisions; they do not expand current scope.
