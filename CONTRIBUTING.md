# Contributing to Travel DNA

The complete Italian contributor guide is:

- [docs/it/04-come-contribuire.md](docs/it/04-come-contribuire.md)

The normative operational and review rules are:

- [docs/it/00-regole-operative.md](docs/it/00-regole-operative.md)
- [docs/it/06-review-e-merge.md](docs/it/06-review-e-merge.md)

Code comments, commit messages and pull requests are written in English. The
teaching documentation in `docs/it` is written in Italian.

## Serial contribution flow

```text
verify current main and open-PR inventory
-> personal fork or authorized repository branch
-> small topic branch from current main
-> focused issue
-> one draft pull request
-> automated checks
-> review round
-> findings and fixes, resetting the clean counter
-> clean review round 1
-> clean review round 2 on the same substantive head
-> ready for review
-> authorized merge
-> verify linked issue and new main
-> only then start the next branch and PR
```

During normal project development exactly one pull request is open at a time.
Do not create stacked, placeholder or `noop` pull requests. Preparing future
issues and design notes is allowed; opening their PR waits for the active PR to
merge or be explicitly abandoned.

Parallel PRs require an explicit maintainer exception. Accidental, duplicate,
stacked or abandoned PRs may be closed administratively without the two-review
gate because they ship no change; their closure reason must be explicit.

## Review and merge

Every shipping pull request requires two consecutive review rounds with no new
findings on the same substantive head. Any substantive commit after a clean
round resets the count.

The PR review timeline/body is the authoritative same-head ledger. It records:

- reviewed SHA;
- risk and complementary focus;
- files and contracts inspected;
- CI/test evidence;
- findings or `no new findings`;
- consecutive clean-round count.

A green CI run is required but is not a review round.

An autonomous agent may mark ready and merge only when the maintainer has granted
explicit or standing authorization and every gate is satisfied. The merge uses
an expected-head guard. Otherwise the maintainer performs the merge.

After every merge, the next branch must be created from the newly verified
`main`; an old dependent branch must be realigned before it can become a PR.

The committed daily report contains context, finding history, review plan and a
link to the PR ledger. Do not create a new commit merely to copy final review
outcomes into that report; reconcile them later through a separately reviewed
change.
