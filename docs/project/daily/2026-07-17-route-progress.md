# Report di sviluppo — 17 luglio 2026 — Matched position e route progress

## Stato

`pre-final-review — ledger autorevole nella PR #18`

Il report è committato prima dei due round finali. CI finale, review e merge
saranno registrati nella timeline della
[PR #18](https://github.com/kinderp/tdna/pull/18), evitando un commit successivo
che invalidi lo SHA revisionato.

## Tracciabilità

- issue: [#17](https://github.com/kinderp/tdna/issues/17);
- PR: [#18](https://github.com/kinderp/tdna/pull/18);
- branch: `agent/matched-route-progress`;
- base: `020f8495f7fbbae81f1463b098b0ddd2a079c873`;
- capitolo: `docs/it/47-posizione-matched-e-route-progress.md`;
- scenario: `docs/it/lab/scenarios/route-progress-tracker.md`.

## Obiettivo

```text
MatchedRoutePosition
-> RouteProgressTracker
-> active leg / upcoming maneuver / arrived
-> RouteProgressMapBinding
-> MapSceneDelta.UpdateRouteProgress
```

Il map matching reale resta fuori scope.

## Codice

### `shared/navigation-contracts`

- `RouteCoordinate` con indice e frazione `[0, 1)`;
- `MatchedRoutePosition` con route ID, sequence, tempo, lateral distance e confidence;
- snapshot, decisioni e motivi di rifiuto;
- invarianti per leg completate e snapshot arrived.

### `shared/route-progress`

- tracker single-owner legato a una route immutabile;
- sequence, tempo e progresso controllati separatamente;
- stationary updates accepted;
- regressioni rejected senza mutazione;
- binary search per leg e manovre;
- policy deterministica al confine;
- `Arrive` finale pre-selezionata;
- `inspect`, `accept` e `reset`.

### `shared/route-progress-map-projector`

- binding route-overlay verificato all'installazione;
- confronto route ID e geometria una sola volta;
- update `O(1)` con scene/overlay ID, indice e frazione;
- nessuna copia della geometria nel loop.

### Lab

- route sintetica con quattro punti e due leg;
- sei posizioni matched;
- una regressione intenzionale;
- ground truth sul confine, manovra, arrival e delta;
- benchmark con 10.000 posizioni, fino a 100 leg e 101 manovre.

## Finding risolti

1. **Scansioni lineari nel hot path** — sostituite da binary search.
2. **Overlay same-ID con geometria diversa** — introdotto binding con confronto
   completo all'installazione.
3. **Benchmark con una sola leg e nessuna manovra** — scenario multi-leg.
4. **Preprocessing incluso nella misurazione** — tracker e cursori costruiti prima;
   reset fuori dal timer.
5. **Ground truth del confine non osservabile** — report con `boundary_leg` e
   `boundary_maneuver`.
6. **Arrival ambiguo** — snapshot arrived espone `Arrive` o `null`.
7. **Test projector incompleto** — aggiunto same-length/different-geometry.
8. **Named argument Kotlin errato** — CI ha rifiutato `position`; corretto in
   `location`.
9. **Tracepoint fuorviante** — route progress non afferma più di aggiornare
   distanze o ETA.
10. **Snapshot pubblico troppo permissivo** — rifiuta cursori di leg completate e
    manovre non-Arrive quando `arrived`.
11. **Assertion di test dentro il timer** — rimossa la verifica per campione;
    snapshot finale, sequence e coordinata sono controllati dopo il timestamp di
    fine di ogni run.

Ogni finding e commit sostanziale ha azzerato il clean-review counter.

## Test

- coordinate, zero firmato e lateral distance;
- route mismatch e indici/frazioni invalidi;
- sequence/tempo non crescenti;
- regressione di indice e frazione;
- stationary accepted e rifiuto senza mutazione;
- confine fra leg e priorità della nuova leg;
- tie-break `Arrive`;
- `inspect` e `reset`;
- binding route-overlay e geometria alterata;
- projector update;
- benchmark arguments e verifica finale fuori dal timer;
- common/JVM/Linux x64;
- architecture boundaries e Foundation CI.

## Lab

```bash
sh tools/tdna lab route-progress
```

```json
{"scenario":"reference-route-progress-v0","accepted":5,"rejected":1,"rejection":"RegressedAlongRoute","boundary_leg":1,"boundary_maneuver":"Continue","completed_index":3,"fraction":0.0,"active_leg":1,"upcoming_maneuver":"Arrive","arrived":true,"delta_index":3}
```

## Benchmark diagnostico osservato

Evidenza tecnica precedente al finding 11:

- Foundation CI run
  [#110](https://github.com/kinderp/tdna/actions/runs/29591739761);
- technical head `db98ac639c02c861a73e2373b100e3d6d3cb01c2`;
- artifact `8411476950`;
- digest `sha256:db8a071fea4f9b76903d57e8db1e7e4692de54ee691143546b6cb3d7097148b8`.

```json
{"benchmark":"route-progress-v0","samples":10000,"legs":100,"maneuvers":101,"warmups":3,"iterations":7,"min_elapsed_ns":1031616,"median_elapsed_ns":9649963,"max_elapsed_ns":13341044,"median_ns_per_sample":965.00}
```

Quel dato includeva una assertion leggera per campione e resta solo una evidenza
storica. La CI finale riesegue il benchmark con:

```text
route, input, tracker e cursori fuori dal timer
reset fuori dal timer
nessuna assertion per campione nella finestra
verifica finale di arrival, sequence e coordinata dopo il timer
```

La misurazione osserva `accept`, binary search, snapshot e commit. Non misura GPS,
filter, map matching, projector/renderer, rete, database, mobile, batteria o
strada. Nessun risultato è un SLA o una soglia CI.

## Documentazione

- [Capitolo 47](../../it/47-posizione-matched-e-route-progress.md)
- [Scenario Lab](../../it/lab/scenarios/route-progress-tracker.md)
- [Mappa del codice](../../it/40-mappa-codice-e-stati.md)
- [Tracepoint Model](../../it/41-tracepoint-model-v0.md)
- indici repository, Lab, milestone e sviluppo.

## Review plan

### Round 1

Contratti, monotonicità, precedenza rifiuti, policy leg/manovra, snapshot,
binding, mutazione e test.

### Round 2

Architettura, hot path, privacy/provenance, benchmark, documentazione, CI e scope.

Servono due round consecutivi sullo stesso substantive head finale.

## Decisioni richieste

Nessuna. Il merge autonomo è autorizzato soltanto dopo tutti i gate.

## Debito e non-obiettivi

- map matching e filtro GPS;
- distanza, ETA, off-route e reroute;
- adapter Android/iOS e MapLibre;
- test concorrente;
- benchmark mobile/batteria;
- Gradle Wrapper.

## Prossimo passo

Fissare lo SHA finale, ottenere CI verde, svolgere due round puliti, mergiare con
expected-head guard e verificare issue e `main`. La slice successiva partirà
soltanto dal nuovo `main` e con una sola PR aperta.
