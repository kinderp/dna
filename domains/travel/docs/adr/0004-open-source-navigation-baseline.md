# ADR-0004 — Baseline open source MapLibre, Valhalla e Ferrostar

- Status: Accepted
- Date: 2026-07-16

## Contesto

Travel DNA vuole sperimentare una navigazione integrata senza costruire subito
renderer, router e guidance engine completi. Servono componenti open source con
confini sufficientemente chiari.

## Decisione

Baseline iniziale:

- OpenStreetMap come fonte geografica principale;
- MapLibre Native per il rendering della mappa;
- Valhalla dietro `RoutePlannerPort` e `MapMatcherPort`;
- Ferrostar dietro `NavigationRuntimePort` e porte più specifiche quando utile.

Questa è una baseline sostituibile, non il modello del dominio.

## Pro

- componenti open source;
- controllo dello stile e degli overlay;
- routing self-hostable;
- guidance mobile con core Rust;
- buon terreno didattico per distinguere dati, rendering, routing e sessione.

## Contro e rischi

- OSM non fornisce un servizio di produzione gratuito illimitato;
- tile, ricerca e traffico richiedono infrastruttura o provider;
- qualità dei dati varia territorialmente;
- Ferrostar è ancora pre-1.0 e va incapsulato;
- integrazione e validazione restano responsabilità nostra;
- una navigazione affidabile richiede molti test di strada e replay.

## Non-obiettivi

- promettere parità immediata con Waze;
- riscrivere MapLibre;
- scaricare in massa tile dai server pubblici OSM;
- esporre risposte Valhalla al resto dell'app.
