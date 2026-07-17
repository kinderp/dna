# Tracepoint Model v0

## Scopo

Un tracepoint logico è un nome stabile per uno stage interno. Serve a spiegare,
testare e visualizzare un percorso senza dipendere da nomi di funzione o righe di
log.

Per v0 è documentale: non introduce automaticamente telemetria, allocazioni o
I/O nel percorso caldo.

## Schema concettuale

| Campo | Significato |
| --- | --- |
| `name` | Nome stabile in maiuscolo. |
| `domain` | navigation, map, journey, conversation, presence, provider. |
| `stage` | receive, validate, accept, reject, bind, project, emit, persist. |
| `hot_path` | yes, no o conditional. |
| `data_in` / `data_out` | Modelli concettuali. |
| `evidence` | Test, fixture, snapshot, benchmark o report. |
| `stability` | candidate, stable-doc, public-output, deprecated. |
| `privacy` | Classe dati e vincoli. |
| `non_goal` | Cosa non dimostra. |

```text
candidate -> stable-doc -> public-output
```

Un nome descrive un fatto Travel DNA, non un vendor:

```text
POSITION_MAP_MATCHED        corretto
ROUTE_PROGRESS_ACCEPTED     corretto
MAPLIBRE_UPDATED            debole e provider-specifico
```

## Percorso caldo

Un futuro trace runtime deve essere opt-in, bounded, privo di payload sensibile,
misurato e accompagnato da una politica di perdita. La documentazione non
legittima logging verboso nel loop.

## Reference routing Lab

| Nome | Stato | Significato |
| --- | --- | --- |
| `REFERENCE_FIXTURE_PARSED` | stable-doc | Grafo/query/ground truth validati. |
| `REFERENCE_FRONTIER_NODE_SELECTED` | stable-doc | Priority queue seleziona il candidato. |
| `REFERENCE_EDGE_RELAXED` | stable-doc | È registrato un costo migliore. |
| `REFERENCE_ROUTE_RECONSTRUCTED` | stable-doc | Predecessori trasformati in route. |
| `REFERENCE_REPORT_EMITTED` | stable-doc | Una implementazione emette il report. |
| `REFERENCE_REPORTS_MATCHED` | stable-doc | Java e Rust coincidono. |

## Location replay Lab

| Nome | Stato | Significato |
| --- | --- | --- |
| `LOCATION_REPLAY_FIXTURE_PARSED` | stable-doc | Parser JVM valida fixture e ground truth. |
| `LOCATION_SAMPLE_RECEIVED` | stable-doc | Runner osserva il prossimo sample dichiarato. |
| `LOCATION_SAMPLE_ACCEPTED` | stable-doc | Sequence e tempo superano il gate. |
| `LOCATION_SAMPLE_REJECTED` | stable-doc | Sample rifiutato senza mutare baseline/clock. |
| `REPLAY_CLOCK_BASELINE_ESTABLISHED` | stable-doc | Primo accepted, delta zero. |
| `REPLAY_CLOCK_ADVANCED` | stable-doc | Accepted successivo, delta positivo. |
| `REPLAY_STATE_CHANGED` | stable-doc | Start/pause/resume/step/cancel. |
| `LOCATION_REPLAY_COMPLETED` | stable-doc | Tutti i sample sono processati. |
| `LOCATION_REPLAY_REPORT_EMITTED` | stable-doc | Summary e ground truth coincidono. |

## Route progress Lab

| Nome | Stato | Significato |
| --- | --- | --- |
| `MATCHED_ROUTE_POSITION_RECEIVED` | stable-doc | Il tracker riceve una posizione già associata alla route. |
| `MATCHED_ROUTE_POSITION_REJECTED` | stable-doc | Identità, ordine o progresso non validi; snapshot invariato. |
| `ROUTE_PROGRESS_ACCEPTED` | stable-doc | Coordinata, leg, manovra e arrival sono pubblicati. |
| `ROUTE_ACTIVE_LEG_CHANGED` | stable-doc | Il confine condiviso attiva una nuova leg. |
| `ROUTE_UPCOMING_MANEUVER_CHANGED` | stable-doc | Cambia il cursore della prossima manovra. |
| `ROUTE_ARRIVAL_REACHED` | stable-doc | Raggiunto il punto geometrico finale canonico. |
| `MAP_ROUTE_PROGRESS_BOUND` | stable-doc | Route e overlay sono verificati durante l'installazione. |
| `MAP_PROGRESS_DELTA_PROJECTED` | stable-doc | Binding e snapshot producono un delta compatto. |

`ROUTE_PROGRESS_ACCEPTED` **non** afferma che distanza o ETA siano aggiornate:
questa slice possiede soltanto coordinata route, leg, manovra e arrival.

Esempio:

```text
Tracepoint: MATCHED_ROUTE_POSITION_REJECTED
Status: stable-doc
Domain: navigation
Stage: reject
Hot path: yes in futuro
Data in: MatchedRoutePosition + last accepted snapshot
Data out: rejection reason, unchanged snapshot
Evidence: RouteProgressTrackerTest and route-progress Lab
Privacy: synthetic/local position; no network publication
Non-goal: does not perform map matching or off-route detection
```

## Navigation target

| Nome | Stato | Significato |
| --- | --- | --- |
| `POSITION_MAP_MATCHED` | stable-doc | Un matcher produce posizione e confidenza. |
| `ROUTE_PROGRESS_UPDATED` | stable-doc | È aggiornato il read model di progresso, senza implicare distanza. |
| `MANEUVER_SELECTED` | stable-doc | È selezionata la manovra attiva/upcoming. |
| `OFF_ROUTE_SUSPECTED` | stable-doc | Evidenza iniziale non confermata. |
| `OFF_ROUTE_CONFIRMED` | stable-doc | La state machine conferma deviazione. |
| `REROUTE_REQUESTED` | stable-doc | Parte un solo ricalcolo logico. |
| `ROUTE_REPLACED` | stable-doc | Una route valida sostituisce la precedente. |
| `NAVIGATION_SNAPSHOT_PUBLISHED` | stable-doc | Snapshot compatto per UI/voce/mappa. |

## Map

| Nome | Stato | Significato |
| --- | --- | --- |
| `MAP_BASE_SCENE_INSTALLED` | stable-doc | Scena, route e marker installati. |
| `MAP_ROUTE_PROGRESS_BOUND` | stable-doc | Overlay verificato contro la route canonica. |
| `MAP_PROGRESS_DELTA_APPLIED` | stable-doc | Renderer aggiorna soltanto il progresso. |
| `MAP_PRESENCE_DELTA_APPLIED` | candidate | Aggiornati compagni approssimati. |

## Journey, conversation e presence

| Nome | Stato | Significato |
| --- | --- | --- |
| `JOURNEY_EVENT_APPENDED` | stable-doc | Evento locale salvato. |
| `DAILY_PAGE_COMPOSED` | stable-doc | Bozza giornaliera composta. |
| `DNA_CARD_SANITIZED` | stable-doc | Proiezione condivisa minimizzata. |
| `MESSAGE_DURABLY_RECEIVED` | stable-doc | Messaggio nel local store. |
| `DRIVE_POLICY_EVALUATED` | stable-doc | Determinate capability sicure. |
| `DRIVER_SAFE_MESSAGE_PUBLISHED` | stable-doc | Superficie adatta alla guida. |
| `PRESENCE_SIGNAL_APPROXIMATED` | stable-doc | Posizione ridotta prima della rete. |

## Promozione a output pubblico

Richiede schema, versione, compatibilità, test, retention, privacy review,
benchmark e delivery/loss semantics.
