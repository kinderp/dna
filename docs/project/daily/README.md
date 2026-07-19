# Indice dei report giornalieri

I report collegano issue, PR, commit, prove, finding, benchmark e decisioni; non
sostituiscono il ledger della pull request.

| Data / sessione | Tema | Issue / PR | Stato nel file |
| --- | --- | --- | --- |
| [2026-07-19 — Android Auto compliance roadmap](2026-07-19-android-auto-compliance-roadmap.md) | POI-first, snapshot driver-safe, DHU e Navigation beta. | [#29](https://github.com/kinderp/tdna/issues/29) / PR della slice | Decision-backed; ledger operativo finale nella PR. |
| [2026-07-19 — Android emulator smoke](2026-07-19-android-emulator-smoke.md) | Navigazione bounded, instrumentation, AVD e install evidence. | [#27](https://github.com/kinderp/tdna/issues/27) / [#28](https://github.com/kinderp/tdna/pull/28) | Implementation-backed; merged as `6198bd0a6c5a89f0e32fa218015125b5d452703e`. |
| [2026-07-18 — Android-first Pilot 0](2026-07-18-android-first-pilot-shell.md) | ADR, roadmap/studio/protocollo e shell Compose installabile. | #25 / #26 | Mergiata come `cc6f4389c3ff03c7b4c3c3a45a3cc3ddc95ae95f`. |
| [2026-07-18 — Gradle Wrapper](2026-07-18-gradle-wrapper-foundation-closure.md) | Wrapper, checksum, Action pinning e closure Foundations. | #23 / #24 | Mergiata come `7090882b40e747a85d812decb0b3567a1276d701`. |
| [2026-07-18 — missed exit](2026-07-18-missed-exit-reroute.md) | Evidenza, conferma, reroute e replacement. | #21 / #22 | Mergiata come `8570eb466b43384756f2678da2303929295e087a`. |
| [2026-07-18 — map matcher](2026-07-18-map-matcher-port.md) | Porta, fake, testkit e pipeline progress. | #19 / #20 | Mergiata come `95cf2f0d900b039efb86ba0a570e3ca3f8d8cef5`. |
| [2026-07-17 — route progress](2026-07-17-route-progress.md) | Matched position, manovra, arrival e binding. | #17 / #18 | Mergiata. |
| [2026-07-17 — replay](2026-07-17-location-replay.md) | LocationSample, clock e replay. | #11 / #16 | Mergiata. |
| [2026-07-17 — disciplina PR](2026-07-17-pr-discipline.md) | Una PR e expected-head. | #14 / #15 | Mergiata. |
| [2026-07-17 — MapScene](2026-07-17-map-scene.md) | Scena, delta e renderer. | #7 / #10 | Mergiata. |
| [2026-07-17 — review policy](2026-07-17-review-policy.md) | Due round consecutivi. | #8 / #9 | Mergiata. |
| [2026-07-17](2026-07-17.md) | Routing contracts e fake planner. | #5 / #6 | Mergiata. |
| [2026-07-16](2026-07-16.md) | Routing Java/Rust. | #3 / #4 | Mergiata. |

Dopo i round finali il ledger autorevole resta la PR, evitando commit che
invaliderebbero lo SHA revisionato.
