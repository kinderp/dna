# Tracepoint Model v0

## Scopo

Un tracepoint logico è un nome stabile per uno stage interno. Serve a spiegare,
testare e visualizzare un percorso senza dipendere dal nome corrente di una
funzione o da una riga di log.

Per v0 i tracepoint sono documentali. Non introducono automaticamente telemetria,
allocazioni o I/O nel percorso caldo.

## Relazione tra gli oggetti

```text
canonical model     = contratto dati
tracepoint           = nome logico dello stage
Lab scenario         = percorso didattico
runtime log          = diagnostica implementativa
public telemetry     = eventuale contratto futuro separato
```

## Campi concettuali

| Campo | Significato |
| --- | --- |
| `name` | Nome stabile in maiuscolo. |
| `domain` | navigation, map, journey, conversation, presence, provider. |
| `stage` | receive, validate, accept, reject, match, project, emit, persist. |
| `hot_path` | yes, no oppure conditional. |
| `data_in` | Modello concettuale in ingresso. |
| `data_out` | Modello concettuale in uscita. |
| `evidence` | Test, fixture, snapshot, benchmark o report. |
| `stability` | candidate, stable-doc, public-output, deprecated. |
| `privacy` | Classe dei dati e vincoli. |
| `non_goal` | Cosa il tracepoint non dimostra. |

## Stabilità

```text
candidate -> stable-doc -> public-output
```

- `candidate`: può cambiare liberamente;
- `stable-doc`: usato in capitoli e Lab;
- `public-output`: richiede schema, versione e compatibilità;
- `deprecated`: indica successore e migrazione.

## Naming

Buoni:

```text
LOCATION_SAMPLE_RECEIVED
LOCATION_SAMPLE_REJECTED
POSITION_MAP_MATCHED
ROUTE_PROGRESS_UPDATED
OFF_ROUTE_CONFIRMED
NAVIGATION_SNAPSHOT_PUBLISHED
DAILY_PAGE_COMPOSED
DRIVER_SAFE_MESSAGE_PUBLISHED
```

Deboli:

```text
FUNCTION_X_CALLED
LINE_123_LOGGED
THING_HANDLED
MAPLIBRE_UPDATED
VALHALLA_JSON_PARSED
```

Il nome descrive un fatto Travel DNA, non il vendor o l'implementazione corrente.

## Percorso caldo

Un nome documentale non giustifica logging nel hot path. Un futuro trace runtime
deve essere:

- opt-in;
- bounded;
- senza payload sensibile;
- misurato;
- accompagnato da una politica di perdita;
- versionato se esposto.

## Reference routing Lab

| Nome | Stato | Significato |
| --- | --- | --- |
| `REFERENCE_FIXTURE_PARSED` | stable-doc | La fixture grafo è validata. |
| `REFERENCE_FRONTIER_NODE_SELECTED` | stable-doc | La priority queue seleziona il candidato. |
| `REFERENCE_EDGE_RELAXED` | stable-doc | È registrato un costo migliore. |
| `REFERENCE_ROUTE_RECONSTRUCTED` | stable-doc | La catena dei predecessori diventa route. |
| `REFERENCE_REPORT_EMITTED` | stable-doc | Una implementazione emette il report. |
| `REFERENCE_REPORTS_MATCHED` | stable-doc | Java e Rust coincidono. |

## Location replay Lab

| Nome | Stato | Significato |
| --- | --- | --- |
| `LOCATION_REPLAY_FIXTURE_PARSED` | stable-doc | Header, campioni e ground truth sono validati dal parser JVM. |
| `LOCATION_SAMPLE_RECEIVED` | stable-doc | Il runner osserva il prossimo campione nell'ordine dichiarato. |
| `LOCATION_SAMPLE_ACCEPTED` | stable-doc | Sequence e tempo crescono rispetto alla baseline. |
| `LOCATION_SAMPLE_REJECTED` | stable-doc | Il campione non modifica gate, clock o rate scaler. |
| `REPLAY_CLOCK_BASELINE_ESTABLISHED` | stable-doc | Il primo accepted imposta il clock con delta zero. |
| `REPLAY_CLOCK_ADVANCED` | stable-doc | Un accepted successivo produce un delta positivo. |
| `REPLAY_STATE_CHANGED` | stable-doc | La state machine cambia stato tramite start/pause/resume/step/cancel. |
| `LOCATION_REPLAY_COMPLETED` | stable-doc | Tutti i campioni dichiarati sono stati processati. |
| `LOCATION_REPLAY_REPORT_EMITTED` | stable-doc | Summary e ground truth coincidono e la CLI emette il report. |

Questi nomi non sono chiamate di logging nel runner. Le prove sono fixture, test,
summary e output CLI.

## Navigation

| Nome | Stato | Significato |
| --- | --- | --- |
| `LOCATION_SAMPLE_RECEIVED` | stable-doc | Il runtime riceve un campione canonico. |
| `LOCATION_SAMPLE_ACCEPTED` | stable-doc | Il campione supera il gate strutturale/temporale. |
| `POSITION_MAP_MATCHED` | stable-doc | È prodotta una posizione matched con confidenza. |
| `ROUTE_PROGRESS_UPDATED` | stable-doc | Progresso e distanze sono aggiornati. |
| `MANEUVER_SELECTED` | stable-doc | È selezionata la manovra attiva. |
| `OFF_ROUTE_SUSPECTED` | stable-doc | Evidenza iniziale non ancora confermata. |
| `OFF_ROUTE_CONFIRMED` | stable-doc | La state machine conferma deviazione. |
| `REROUTE_REQUESTED` | stable-doc | Parte un solo ricalcolo logico. |
| `ROUTE_REPLACED` | stable-doc | Una nuova route valida sostituisce la precedente. |
| `NAVIGATION_SNAPSHOT_PUBLISHED` | stable-doc | È pubblicato uno snapshot compatto. |

## Map

| Nome | Stato | Significato |
| --- | --- | --- |
| `MAP_BASE_SCENE_INSTALLED` | stable-doc | Scena statica, route e marker iniziali sono installati. |
| `MAP_PROGRESS_DELTA_APPLIED` | stable-doc | È aggiornato soltanto il progresso. |
| `MAP_PRESENCE_DELTA_APPLIED` | candidate | Sono aggiornati compagni approssimati. |

## External navigation

| Nome | Stato | Significato |
| --- | --- | --- |
| `EXTERNAL_NAVIGATION_LAUNCHED` | stable-doc | Handoff a un navigatore esterno riuscito. |
| `SHADOW_ROUTE_CONFIDENCE_CHANGED` | stable-doc | Cambia l'affidabilità del contesto ombra. |

## Journey e Journal

| Nome | Stato | Significato |
| --- | --- | --- |
| `JOURNEY_EVENT_APPENDED` | stable-doc | Un evento è salvato localmente. |
| `STOP_CANDIDATE_DETECTED` | stable-doc | Una sosta viene proposta. |
| `MEDIA_ASSOCIATION_PROPOSED` | stable-doc | Una foto è candidata a un momento. |
| `DAILY_PAGE_COMPOSED` | stable-doc | È composta una bozza giornaliera. |
| `USER_EDIT_PRESERVED` | stable-doc | Una rigenerazione conserva l'edit. |
| `DNA_CARD_SANITIZED` | stable-doc | Il frammento condiviso è minimizzato. |

## Conversation

| Nome | Stato | Significato |
| --- | --- | --- |
| `MESSAGE_DURABLY_RECEIVED` | stable-doc | Il messaggio è nel local store. |
| `DRIVE_POLICY_EVALUATED` | stable-doc | Sono determinate le capability sicure. |
| `DRIVER_SAFE_MESSAGE_PUBLISHED` | stable-doc | Il messaggio usa una superficie sicura. |
| `VOICE_REPLY_QUEUED` | stable-doc | La risposta vocale è in outbox. |

## Presence

| Nome | Stato | Significato |
| --- | --- | --- |
| `PRESENCE_SIGNAL_APPROXIMATED` | stable-doc | La posizione precisa è ridotta prima della rete. |
| `PRESENCE_SIGNAL_PUBLISHED` | candidate | Il segnale bounded viene inviato con TTL. |
| `COMPANION_AGGREGATE_RECEIVED` | candidate | Arriva un read model approssimato. |

## Esempio completo

```text
Tracepoint: LOCATION_SAMPLE_REJECTED
Status: stable-doc
Domain: navigation
Stage: reject
Hot path: yes in futuro
Data in: LocationSample + last accepted baseline
Data out: rejection reason, unchanged baseline
Evidence: deterministic replay test and Lab report
Privacy: exact synthetic position remains process-local
Meaning: sequence or monotonic time did not increase
Non-goal: does not evaluate GPS quality or map matching
```

## Promozione a output pubblico

Richiede:

1. schema;
2. versione;
3. test;
4. retention;
5. privacy review;
6. performance benchmark;
7. delivery/loss semantics;
8. compatibility policy.
