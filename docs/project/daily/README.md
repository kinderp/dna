# Indice dei report giornalieri

Questa cartella conserva un report Markdown per ogni giornata o sessione
autonoma significativa. L'indice permette di ricostruire lavoro, rischi, prove e
decisioni.

## Come leggere i report

Controllare:

1. obiettivo;
2. codice e documentazione;
3. test e CI realmente eseguiti;
4. finding e fix;
5. review round e substantive head;
6. benchmark e limiti;
7. decisioni autonome/richieste;
8. debito e prossimo passo.

I report collegano issue, PR, commit, test e ADR; non li sostituiscono.

## Report

| Data / sessione | Tema principale | Issue / PR | Esito |
| --- | --- | --- | --- |
| [2026-07-17 — LocationSample e replay](2026-07-17-location-replay.md) | Geo contract, tempo monotono, ordering gate, replay atomico, fixture e benchmark. | [#11](https://github.com/kinderp/tdna/issues/11) / [#16](https://github.com/kinderp/tdna/pull/16) | Pre-review complete; CI finale e due round richiesti. |
| [2026-07-17 — disciplina PR seriale](2026-07-17-pr-discipline.md) | Una sola PR, merge autorizzato, expected head e riallineamento. | [#14](https://github.com/kinderp/tdna/issues/14) / [#15](https://github.com/kinderp/tdna/pull/15) | Mergiata; merge `76680433089842db5805d28eb50416a23c7d0a88`. |
| [2026-07-17 — MapScene](2026-07-17-map-scene.md) | MapScene, delta, fake renderer e route projector. | [#7](https://github.com/kinderp/tdna/issues/7) / [#10](https://github.com/kinderp/tdna/pull/10) | Mergiata; CI #80 e merge `2128f24b4a6ab00aeb437ed48a0af2910f33e9ec`. |
| [2026-07-17 — review policy](2026-07-17-review-policy.md) | Due review consecutive e hardening `RouteManeuver`. | [#8](https://github.com/kinderp/tdna/issues/8) / [#9](https://github.com/kinderp/tdna/pull/9) | Mergiata; merge `044e0773dd9afb1530db35688a00c56bfbd5eace`. |
| [2026-07-17](2026-07-17.md) | Contratti routing, plugin SDK, fake planner e KMP. | [#5](https://github.com/kinderp/tdna/issues/5) / [#6](https://github.com/kinderp/tdna/pull/6) | Mergiata. |
| [2026-07-16](2026-07-16.md) | Routing Java/Rust, Dijkstra, A* e contract diff. | [#3](https://github.com/kinderp/tdna/issues/3) / [#4](https://github.com/kinderp/tdna/pull/4) | Mergiata. |

## Convenzione

```text
YYYY-MM-DD.md
YYYY-MM-DD-argomento.md
```

## Regola di manutenzione

Alla chiusura di ogni sessione:

- creare o aggiornare il report;
- aggiornare questa tabella;
- collegare issue, PR, commit e CI;
- distinguere fatti verificati da lavoro pianificato;
- registrare finding e review plan;
- dichiarare benchmark e limiti;
- indicare decisioni richieste.
