## Summary

Describe the change and why it is needed.

## User-visible use case

Which user behavior or teaching scenario changes?

## Scope

- Bounded context:
- Platforms:
- Risk level: R0 / R1 / R2 / R3
- Current milestone issue:

## Contracts and architecture

- Canonical contracts changed:
- ADRs changed or added:
- Provider involved:
- Are provider-specific types confined to the adapter? Yes / No / N/A
- Does the change alter lifecycle, ownership, concurrency or cancellation?

## Performance and energy

- Hot path affected: navigation / rendering / conversation / journey / none
- Expected cost:
- Baseline or benchmark:
- Memory, frame or battery considerations:

## Privacy, permissions and driving safety

- Personal data or permissions involved:
- Retention/precision/visibility changes:
- Abuse or distraction cases considered:

## Verification

- [ ] Unit tests
- [ ] Property/state-machine tests
- [ ] Contract tests
- [ ] GPS replay
- [ ] Integration tests
- [ ] Android/iOS UI tests
- [ ] Performance/soak tests
- [ ] Field audit
- Commands or scenarios executed:
- Required CI run:

## Documentation

- [ ] Stable docs updated
- [ ] Documentation status updated
- [ ] Code/state map updated
- [ ] Lab scenario updated or added
- [ ] ADR updated or added
- [ ] Daily report and index contain context, finding history, review plan and this PR link
- [ ] No documentation change required, with reason below

## Non-goals and follow-up

State explicitly what this PR does not implement.

## Reviewer focus

Point reviewers to the most important risks or decisions.

## Review round 1

- Substantive head SHA:
- Focus:
- Files/contracts inspected:
- CI/test evidence:
- Findings:
- Outcome: findings / no new findings
- Consecutive clean rounds after this round: 0 / 1

## Review round 2

- Substantive head SHA:
- Focus:
- Files/contracts inspected:
- CI/test evidence:
- Findings:
- Outcome: findings / no new findings
- Consecutive clean rounds after this round: 0 / 1 / 2

## Additional review rounds

Add a new section whenever a finding or substantive commit resets the sequence.
Do not overwrite historical rounds.

The PR review timeline/body is the authoritative same-head review ledger. Do not
create a new commit merely to copy clean-round outcomes into the daily report;
reconcile final review and merge status through a later reviewed documentation
change.

## Ready and merge gate

- [ ] All findings are resolved or explicitly approved as non-goals
- [ ] Required CI is green on the current substantive head
- [ ] Two consecutive review rounds have no new findings
- [ ] Both clean rounds reference the same substantive head SHA
- [ ] No substantive commit was added after the clean rounds
- [ ] PR ledger contains exact outcomes; report links the PR and records pre-review context
- [ ] Issue and milestone agree with the pre-merge state
- [ ] Maintainer has authorized ready/merge

A PR must remain draft and must not be merged until every applicable gate above
is satisfied. See `docs/it/06-review-e-merge.md`.
