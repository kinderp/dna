# Project records

## Live records

- [development-status.md](development-status.md)
- [daily report index](daily/README.md)

## Milestone records

- [Foundations v0 closure](foundation-v0-closure.md)
- [Android Pilot 0 shell report](daily/2026-07-18-android-first-pilot-shell.md)
- [Android emulator smoke report](daily/2026-07-19-android-emulator-smoke.md)
- [Android Auto compliance roadmap report](daily/2026-07-19-android-auto-compliance-roadmap.md)

## Recent operational chains

### Android Pilot 0 v0.2

```text
issue #27
-> PR #28
-> bounded navigation and semantic tests
-> official-SDK emulator runner
-> exact-head build + emulator CI
-> bounded artifact
-> two clean reviews
-> expected-head merge 6198bd0a6c5a89f0e32fa218015125b5d452703e
```

PR #28 is the durable ledger. Emulator evidence remains distinct from physical
and field evidence.

### Android Auto compliance roadmap

```text
issue #29
-> ADR-0011
-> chapter 61
-> Pilot 0 driver-safe snapshot plan
-> automotive readiness POI/DHU spike
-> Pilot 1 POI companion
-> later Navigation beta
-> exact-head docs CI and review ledger in the PR
```

This chain is decision/documentation evidence only. It does not claim Car App
Library runtime, Android Auto compliance, Google Play approval or road evidence.

## Foundation history

- [FOUNDATION-REPORT.md](FOUNDATION-REPORT.md)
- [MANIFEST.md](MANIFEST.md)
- [PUBLISHING.md](PUBLISHING.md)

Stable architecture/product contracts live in `docs/it`; durable choices live in
`docs/adr`; GitHub issues and pull requests preserve operational evidence.
