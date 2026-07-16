# Tracepoint Model v0

## Scopo

Un tracepoint logico è un nome stabile per uno stage interno. Serve a spiegare,
testare e visualizzare il percorso senza dipendere dal nome corrente di una
funzione o da una riga di log.

Per v0 è documentale. Non introduce telemetria runtime né I/O nel percorso caldo.

## Relazione tra oggetti

```text
canonical model    = contratto dati
tracepoint          = nome logico dello stage
Lab scenario        = percorso didattico
runtime log         = diagnostica implementativa
public telemetry    = eventuale contratto futuro separato
```

## Campi

| Campo | Significato |
| --- | --- |
| `name` | Nome stabile in maiuscolo. |
| `domain` | navigation, map, journey, conversation, presence, provider. |
| `stage` | receive, normalize, match, project, emit, persist, publish. |
| `hot_path` | yes/no/conditional. |
| `data_in` | Modello concettuale di ingresso. |
| `data_out` | Modello concettuale di uscita. |
| `evidence` | Test, snapshot, metric o log che dimostra lo stage. |
| `stability` | candidate, stable-doc, public-output, deprecated. |
| `privacy` | Classe dati e vincoli. |
| `non_goal` | Cosa non dimostra. |

## Stabilità

```text
candidate -> stable-doc -> public-output
```

- `candidate`: può cambiare;
- `stable-doc`: usato nei Lab e nella documentazione;
- `public-output`: schema/versione/test richiesti;
- `deprecated`: sostituto e migrazione.

## Naming

Buoni:

```text
LOCATION_SAMPLE_RECEIVED
POSITION_MAP_MATCHED
ROUTE_PROGRESS_UPDATED
OFF_ROUTE_CONFIRMED
NAVIGATION_SNAPSHOT_PUBLISHED
DAILY_PAGE_COMPOSED
DRIVER_SAFE_MESSAGE_PUBLISHED
PRESENCE_SIGNAL_APPROXIMATED
```

Deboli:

```text
FUNCTION_X_CALLED
LINE_123_LOGGED
THING_HANDLED
MAPLIBRE_UPDATED
VALHALLA_JSON_PARSED
```

Il nome descrive il fatto Travel DNA, non il vendor.

## Percorso caldo

Un tracepoint può descrivere il hot path, ma non lo rende più costoso. Per v0
esiste in documentazione e test runner. Se diventa runtime:

- opt-in;
- bounded;
- senza payload sensibile;
- benchmark;
- politica di perdita;
- schema versionato.

## Tracepoint iniziali

### Navigation

| Nome | Stato | Significato |
| --- | --- | --- |
| `LOCATION_SAMPLE_RECEIVED` | stable-doc | Il runtime riceve un campione. |
| `LOCATION_SAMPLE_ACCEPTED` | stable-doc | Il campione supera validazione/ordine. |
| `POSITION_MAP_MATCHED` | stable-doc | È stata prodotta una posizione matched con confidenza. |
| `ROUTE_PROGRESS_UPDATED` | stable-doc | Progresso e distanza sono aggiornati. |
| `MANEUVER_SELECTED` | stable-doc | È selezionata la manovra attiva. |
| `OFF_ROUTE_SUSPECTED` | stable-doc | Evidenza iniziale non ancora confermata. |
| `OFF_ROUTE_CONFIRMED` | stable-doc | La state machine conferma deviazione. |
| `REROUTE_REQUESTED` | stable-doc | Parte un reroute unico. |
| `ROUTE_REPLACED` | stable-doc | Una route valida sostituisce atomically la precedente. |
| `NAVIGATION_SNAPSHOT_PUBLISHED` | stable-doc | Snapshot compatto pubblicato. |

### Map

| Nome | Stato | Significato |
| --- | --- | --- |
| `MAP_BASE_SCENE_INSTALLED` | stable-doc | Stile, route e layer base installati. |
| `MAP_PROGRESS_DELTA_APPLIED` | stable-doc | Aggiornato solo il progresso. |
| `MAP_PRESENCE_DELTA_APPLIED` | candidate | Aggiornati compagni approssimati. |

### External navigation

| Nome | Stato | Significato |
| --- | --- | --- |
| `EXTERNAL_NAVIGATION_LAUNCHED` | stable-doc | Handoff riuscito. |
| `SHADOW_ROUTE_CONFIDENCE_CHANGED` | stable-doc | Cambia affidabilità del contesto. |

### Journey/Journal

| Nome | Stato | Significato |
| --- | --- | --- |
| `JOURNEY_EVENT_APPENDED` | stable-doc | Evento salvato localmente. |
| `STOP_CANDIDATE_DETECTED` | stable-doc | Sosta proposta, non confermata. |
| `MEDIA_ASSOCIATION_PROPOSED` | stable-doc | Foto candidata. |
| `DAILY_PAGE_COMPOSED` | stable-doc | Bozza composta. |
| `USER_EDIT_PRESERVED` | stable-doc | Rigenerazione conserva edit. |
| `DNA_CARD_SANITIZED` | stable-doc | Frammento ripulito. |

### Conversation

| Nome | Stato | Significato |
| --- | --- | --- |
| `MESSAGE_DURABLY_RECEIVED` | stable-doc | Messaggio presente nel local store. |
| `DRIVE_POLICY_EVALUATED` | stable-doc | Determinate capability sicure. |
| `DRIVER_SAFE_MESSAGE_PUBLISHED` | stable-doc | Messaggio esposto via superficie sicura. |
| `VOICE_REPLY_QUEUED` | stable-doc | Risposta salvata per invio. |

### Presence

| Nome | Stato | Significato |
| --- | --- | --- |
| `PRESENCE_SIGNAL_APPROXIMATED` | stable-doc | Exact location ridotta prima della rete. |
| `PRESENCE_SIGNAL_PUBLISHED` | candidate | Segnale inviato con TTL. |
| `COMPANION_AGGREGATE_RECEIVED` | candidate | Read model approssimato. |

## Template

```text
Tracepoint: POSITION_MAP_MATCHED
Status: stable-doc
Domain: navigation
Stage: match
Hot path: yes
Data in: accepted LocationSample + active RoutePlan
Data out: MatchedPosition(confidence, segment, fraction)
Evidence: replay timeline and navigation contract tests
Privacy: exact location remains process-local
Meaning: runtime selected the most plausible route position
Non-goal: does not prove the user is physically on that road
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
