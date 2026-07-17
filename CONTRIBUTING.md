# Contributing to Travel DNA

The complete Italian contributor guide is:

- [docs/it/04-come-contribuire.md](docs/it/04-come-contribuire.md)

The operational rules are:

- [docs/it/00-regole-operative.md](docs/it/00-regole-operative.md)
- [docs/it/06-review-e-merge.md](docs/it/06-review-e-merge.md)

Code comments, commit messages and pull requests are written in English. The
teaching documentation in `docs/it` is written in Italian.

The intended contribution flow is:

```text
upstream repository
    -> personal fork
    -> small topic branch
    -> focused issue
    -> draft pull request
    -> automated checks
    -> review round
    -> findings and fixes, resetting the clean counter
    -> two consecutive clean review rounds on the same substantive head
    -> ready for review
    -> maintainer merge
    -> linked issue closure
```

Every pull request requires two consecutive review rounds with no new findings
before it can be marked ready or merged. Any substantive commit after a clean
round resets the count.

The pull request review timeline/body is the authoritative same-head ledger. It
records the reviewed SHA, focus, findings, CI evidence and clean-round count.
The committed daily report contains context, finding history, review plan and a
link to that ledger. Do not create a new commit merely to copy final review
outcomes into the report; reconcile final review and merge status later through
a separately reviewed documentation change.
