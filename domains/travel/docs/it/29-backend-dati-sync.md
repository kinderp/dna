# Backend, dati locali e sincronizzazione

## Obiettivo

Il backend sostiene chat, presenza, sincronizzazione e catalogo, ma non deve
rendere ogni schermata dipendente dalla rete.

## Local-first

Principio:

```text
user action
-> local transaction
-> local UI state
-> queued sync operation
-> server acceptance
-> reconciliation
```

Eccezioni: azioni che richiedono autorizzazione server, per esempio aprire una
chat con un utente che non ha accettato. Anche in quel caso la UI mostra stato
pending senza bloccarsi.

## Local database

Tabelle/aggregate candidate:

```text
trip
trip_session
planned_stop
journey_event
journey_moment
daily_page
media_reference
conversation
message
outbox_operation
route_cache
place_cache
consent_snapshot
provider_state
```

Separare dati privati ad alta precisione da dati sincronizzati.

## Outbox pattern

Ogni mutazione sincronizzabile genera un'operazione locale atomica:

```text
operation_id
type
aggregate_id
payload_version
created_at
retry_count
next_retry_at
state
```

Vantaggi:

- offline;
- retry;
- audit;
- idempotenza;
- processo ucciso.

## Sync envelope

Campi:

```text
client id
device id pseudonymous
operation id
schema version
aggregate version
payload
consent context when required
```

Il server deduplica `operation_id`.

## Conflitti

Strategie diverse per dato:

- diario owner-only: last accepted version + merge UI;
- reaction/message append-only: dedup;
- visibility: versione monotona e revoca prioritaria;
- consent: revoca non sovrascritta da client vecchio;
- route cache: disposable;
- place content: server authoritative con local cache.

Non applicare un unico last-write-wins a tutto.

## Backend modular monolith

Moduli e ownership DB logica. Possibile schema separato o package enforcement.

```text
identity_consents
trips_journeys
journal_sync
conversations
road_presence
travel_dna
places_guide
routing_gateway
moderation
notifications
```

## API

Tipi:

- REST/HTTP per comandi e query;
- WebSocket/event stream per aggiornamenti live;
- push notification per background;
- object storage signed upload per media;
- internal jobs per proiezioni e moderazione.

Evitare WebSocket come unica sorgente di durabilità.

## Chat backend

Responsabilità:

- autorizzazione conversazione;
- ordine server;
- durable storage;
- fan-out;
- push;
- block/report;
- rate limiting;
- moderation hooks;
- expiry.

## Presence backend

Dati a breve vita, potenzialmente storage separato in futuro.

Requisiti:

- TTL;
- query per cell/corridoio;
- blocchi applicati;
- aggregation;
- anti-spoof;
- nessuna cronologia lunga di default;
- audit di accesso amministrativo.

## Routing gateway

Espone contratto Travel DNA e seleziona:

- Valhalla primario;
- provider alternativo;
- cache;
- shadow provider;
- traffic enrichment.

Aggiunge:

- timeout;
- circuit breaker;
- metrics;
- canonical mapping;
- provenance;
- rate limit;
- request validation.

## Place catalog

Combina:

- OSM-derived data;
- editorial content;
- partner data;
- user DNA summaries;
- freshness and provenance.

PostGIS aiuta query geospaziali, corridor e proximity. Le query di presenza
sensibile devono usare viste/permessi separati.

## Media

Upload:

```text
client requests upload slot
-> server authorizes purpose
-> client uploads directly
-> server verifies metadata/hash
-> processing job creates derivatives
-> reference becomes available
```

Non caricare foto in request JSON.

## Push

Payload minimo:

- opaque event/conversation ID;
- category;
- non-sensitive preview solo se consenso e OS policy;
- no coordinate precise;
- no DNA completo.

Il client recupera il contenuto autenticato.

## Authentication

Da decidere con threat model. Requisiti:

- token brevi;
- refresh sicuro;
- device registration revocabile;
- secure storage mobile;
- account deletion;
- pseudonymous public identity;
- server-side abuse accountability.

## Encryption

- TLS;
- database encryption/storage controls;
- media access signed;
- secret management;
- backups;
- eventuale E2EE come progetto separato.

## Observability

Metriche:

- sync lag;
- outbox depth;
- message delivery latency;
- presence TTL errors;
- routing latency/error;
- push success;
- DB query latency;
- moderation queue;
- data deletion completion.

Log senza payload personali.

## Data retention

Classi:

```text
operational short-lived
user-owned durable
social durable until deletion
analytics aggregated
security audit restricted
```

Ogni tabella deve dichiarare retention e delete path.

## Backup e delete

La cancellazione account deve propagare a:

- profilo;
- presence;
- tokens;
- media;
- diary cloud;
- messages secondo policy e diritti altri partecipanti;
- analytics identifiable;
- backups con finestra dichiarata.

## Backend language decision

Non ancora congelata.

### Java 21 + Spring Boot

Pro:

- didattica Java;
- ecosistema maturo;
- sicurezza, persistence e observability;
- virtual threads;
- molti contributori.

Contro:

- framework più pesante;
- rischio di magia e startup/memoria;
- duplicazione modelli con KMP se gestita male.

### Kotlin/JVM + Ktor

Pro:

- stack coerente;
- leggero e esplicito;
- condivisione di competenze;
- coroutine.

Contro:

- meno Java didattico;
- più decisioni infrastrutturali manuali;
- rischio di condividere modelli interni impropriamente.

Prima milestone backend: spike misurato e ADR.

## Test

- idempotenza outbox;
- conflitti;
- revoca consenso;
- WebSocket disconnect;
- push delayed;
- presence TTL;
- routing timeout/fallback;
- media upload incomplete;
- delete account;
- migration;
- PostGIS corridor query;
- load test separato.
