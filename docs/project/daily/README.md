# Indice dei report giornalieri

Questa cartella conserva un report Markdown per ogni giornata o sessione
autonoma significativa. I report collegano issue, PR, commit, prove, finding,
benchmark e decisioni; non li sostituiscono.

## Report

| Data / sessione | Tema | Issue / PR | Stato registrato nel file |
| --- | --- | --- | --- |
| [2026-07-18 — Gradle Wrapper e chiusura Foundations v0](2026-07-18-gradle-wrapper-foundation-closure.md) | Wrapper 9.5.1, checksum, Action pinning, build-bootstrap e closure report. | [#23](https://github.com/kinderp/tdna/issues/23) / [#24](https://github.com/kinderp/tdna/pull/24) | Closure candidate; ledger finale nella PR. |
| [2026-07-18 — missed exit e reroute](2026-07-18-missed-exit-reroute.md) | Evidenza normalizzata, conferma, correlazione e route replacement. | [#21](https://github.com/kinderp/tdna/issues/21) / [#22](https://github.com/kinderp/tdna/pull/22) | Mergiata come `8570eb466b43384756f2678da2303929295e087a`. |
| [2026-07-18 — porta map matching](2026-07-18-map-matcher-port.md) | Sessione route-bound, esiti, fake, testkit e pipeline progress. | [#19](https://github.com/kinderp/tdna/issues/19) / [#20](https://github.com/kinderp/tdna/pull/20) | Mergiata come `95cf2f0d900b039efb86ba0a570e3ca3f8d8cef5`. |
| [2026-07-17 — route progress](2026-07-17-route-progress.md) | Matched position, leg/manovra, arrival, binding e benchmark. | [#17](https://github.com/kinderp/tdna/issues/17) / [#18](https://github.com/kinderp/tdna/pull/18) | Mergiata come `9921fbcc1da1000e6434bdae49646122cae8f0e0`. |
| [2026-07-17 — LocationSample e replay](2026-07-17-location-replay.md) | Tempo monotono, ordering gate, replay atomico e benchmark. | [#11](https://github.com/kinderp/tdna/issues/11) / [#16](https://github.com/kinderp/tdna/pull/16) | Mergiata come `020f8495f7fbbae81f1463b098b0ddd2a079c873`. |
| [2026-07-17 — disciplina PR](2026-07-17-pr-discipline.md) | Una PR, expected-head e riallineamento. | #14 / #15 | Mergiata come `76680433089842db5805d28eb50416a23c7d0a88`. |
| [2026-07-17 — MapScene](2026-07-17-map-scene.md) | Scena, delta, fake renderer e projector. | #7 / #10 | Mergiata. |
| [2026-07-17 — review policy](2026-07-17-review-policy.md) | Due review e hardening contratti. | #8 / #9 | Mergiata. |
| [2026-07-17](2026-07-17.md) | Routing contracts, plugin SDK e fake planner. | #5 / #6 | Mergiata. |
| [2026-07-16](2026-07-16.md) | Routing Java/Rust, Dijkstra e A*. | #3 / #4 | Mergiata. |

## Convenzione

```text
YYYY-MM-DD.md
YYYY-MM-DD-argomento.md
```

Alla chiusura di una sessione si aggiornano report e indice, distinguendo sempre
fatti verificati da piani. Dopo i round finali il ledger autorevole resta la PR,
per non creare un commit che invalidi le review.
