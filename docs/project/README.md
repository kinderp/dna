# Project records

## Live records

- [development-status.md](development-status.md)
- [daily report index](daily/README.md)

## Milestone records

- [Foundations v0 closure](foundation-v0-closure.md)
- [Android Pilot 0 shell report](daily/2026-07-18-android-first-pilot-shell.md)
- [Android emulator smoke report](daily/2026-07-19-android-emulator-smoke.md)

## Android emulator operational chain

```text
issue #27
-> PR #28
-> bounded navigation and semantic tests
-> official-SDK emulator runner
-> exact-head build + emulator CI
-> bounded artifact
-> two clean reviews
-> expected-head merge
```

PR #28 is the durable ledger for the chain, both during review and after merge.
Emulator evidence remains distinct from physical-device and field evidence.

## Foundation history

- [FOUNDATION-REPORT.md](FOUNDATION-REPORT.md)
- [MANIFEST.md](MANIFEST.md)
- [PUBLISHING.md](PUBLISHING.md)

Stable architecture/product contracts live in `docs/it`; durable choices live in
`docs/adr`; GitHub issues and pull requests preserve operational evidence.
