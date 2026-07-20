# ADR-0002 — Contratti canonici e adapter per i provider

- Status: Accepted
- Date: 2026-07-16

## Contesto

MapLibre, Valhalla, Ferrostar, Google Maps, Waze, Sygic, servizi di geocoding e
fonti di tile usano modelli e capacità differenti. Se i loro tipi entrano nel
dominio, sostituire un componente richiede modifiche diffuse e rende i test
vincolati al vendor.

## Decisione

Travel DNA definisce modelli e porte propri:

```text
GeoPoint
RouteRequest
RoutePlan
RouteManeuver
NavigationSnapshot
Place
MapScene
ExternalNavigationRequest
PluginDescriptor
ProviderCapability
```

Ogni dipendenza esterna è confinata in un adapter che:

1. valida input e output;
2. traduce tipi vendor in tipi canonici;
3. traduce errori in un modello stabile;
4. dichiara capability e limitazioni;
5. conserva provenance e riferimenti esterni senza usarli come identità primaria;
6. supera il test kit del contratto.

Il registry sceglie il provider durante la composizione o un cambio controllato,
non a ogni campione GPS.

## Alternative considerate

- usare direttamente i tipi del provider: rapido all'inizio, costoso dopo;
- creare un unico `MapService` che fa tutto: semplice da chiamare, impossibile da
  sostituire in parti;
- replicare l'intera API vendor nei nostri contratti: troppo grande e fragile.

## Conseguenze

- maggiore lavoro iniziale di modellazione;
- necessità di mantenere mapping e contract test;
- possibilità di fake provider, fallback, shadow mode e confronti;
- separazione chiara fra capacità del prodotto e dettagli della libreria.

## Vincolo prestazionale

L'astrazione non deve aggiungere conversioni ripetute di geometrie grandi nel
loop. Una route viene normalizzata una volta; gli aggiornamenti successivi usano
indici, progressi o delta compatti.
