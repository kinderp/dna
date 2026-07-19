# Stato della documentazione

## Stati

- `Foundation complete`
- `Implementation-backed`
- `Partial`
- `Planned`

## Implementation-backed

- governance e review: `00`, `03`, `04`, `06`;
- build/supply chain: `36`, `37`;
- repository/test/performance: `21`, `30`–`34`, `40`–`42`;
- navigation foundations: `43`–`49`;
- Android-first decision/roadmap: `55`, ADR-0010;
- Android study path and road protocol: `56`, `57`;
- Android Pilot 0 shell: `58` and scenario `android-pilot0-shell.md`;
- direct resources and purchase roadmap: `59`;
- Android emulator smoke/navigation: `60` and scenario `android-pilot0-emulator-smoke.md`;
- milestone/status/report records: `50`, project status and daily index.

## Foundation complete

Visione, glossario, DDD, use case, architettura generale, OSM/cartografia,
navigatori esterni, diario, presenza/chat, backend, privacy, qualità, librerie e
matrice tecnologie hanno una base narrativa che verrà aggiornata con le slice.

## Partial

- Android Pilot 0 ha build e runtime emulatore in validazione, ma non ancora una
  prova fisica/manuale;
- accessibilità automatizzata di base non sostituisce font scaling e TalkBack;
- la Demo usa ancora uno snapshot, non replay/progress interattivi.

## Planned

- telefono fisico e checklist manuale Pilot 0;
- replay/progress e missed-exit interattivi nell'app;
- GPS/foreground service reali;
- MapLibre/Valhalla adapters;
- backend, chat, journal e automotive runtime;
- LoRa research.

Un documento `Implementation-backed` deve restare coerente con codice, test e
non-obiettivi correnti. Un emulatore verde non costituisce prova su strada.
