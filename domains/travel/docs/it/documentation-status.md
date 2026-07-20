# Stato della documentazione

## Stati

- `Foundation complete`
- `Implementation-backed`
- `Decision-backed`
- `Partial`
- `Planned`

## Implementation-backed

- governance e review: `00`, `03`, `04`, `06`;
- build/supply chain: `36`, `37`;
- repository/test/performance: `21`, `30`–`34`, `40`–`42`;
- navigation foundations: `43`–`49`;
- Android-first runtime baseline: `55`, `58`, `60`, ADR-0010;
- Android study path and road protocol: `56`, `57`;
- direct resources and purchase roadmap: `59`;
- milestone/status/report records: `50`, project status e daily index.

Per capitolo 60 e relativo scenario, PR #28 conserva il ledger operativo esatto di
SHA, CI, artifact, review e merge. La documentazione descrive comportamento e
confini senza replicare uno stato temporaneo della pull request.

## Decision-backed

- Android Auto POI-first e superfici auto separate: `61`, ADR-0011;
- roadmap v0.3/v0.4, automotive readiness, Pilot 1 POI e Navigation beta: `55`,
  `61`.

`Decision-backed` significa che una scelta architetturale è accettata e coerente
nei documenti, ma il runtime corrispondente non è ancora implementato. Non deve
essere presentato come prodotto funzionante o compliant.

## Foundation complete

Visione, glossario, DDD, use case, architettura generale, OSM/cartografia,
navigatori esterni, diario, presenza/chat, backend, privacy, qualità, librerie e
matrice tecnologie hanno una base narrativa che verrà aggiornata con le slice.

## Partial

- Android Pilot 0 possiede build e runtime emulatore implementation-backed, ma non
  ancora una prova fisica/manuale;
- accessibilità automatizzata di base non sostituisce font scaling e TalkBack;
- la Demo usa ancora uno snapshot, non replay/progress interattivi;
- la strategia Android Auto è documentata, ma non esistono Car App Library module,
  manifest category, DHU evidence o vehicle evidence.

## Planned

- telefono fisico e checklist manuale Pilot 0;
- replay/progress e missed-exit interattivi nell'app;
- `DriverJourneySnapshot` o contratto driver-safe equivalente;
- GPS/foreground service reali;
- navigatori esterni;
- Car App Library POI spike e Desktop Head Unit;
- Android Automotive OS package;
- futura Navigation category con AUTO_DRIVE e guidance;
- MapLibre/Valhalla adapters;
- backend, chat e journal;
- LoRa research.

Un documento `Implementation-backed` deve restare coerente con codice, test e
non-obiettivi correnti. Un emulatore, DHU o AUTO_DRIVE verde non costituisce prova
su strada.
