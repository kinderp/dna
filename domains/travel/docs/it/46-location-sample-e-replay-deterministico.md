# LocationSample, tempo monotono e replay deterministico

## Stato del capitolo

`implementation-backed — Foundations and Travel DNA Lab v0`

Questo capitolo documenta la prima pipeline eseguibile che trasforma una
sequenza sintetica di osservazioni di posizione in eventi accettati o rifiutati,
contatori bounded e tempo di playback deterministico.

Il codice si trova in:

```text
shared/geo-contracts/
shared/location-contracts/
shared/location-replay/
labs/location-replay-cli/
fixtures/gps/
```

La slice non implementa ancora GPS di piattaforma, map matching, route progress,
off-route o ricalcolo. Prima stabilisce il contratto dei dati e il modo corretto
di riprodurli.

## Cosa imparerai

Al termine dovresti saper spiegare:

1. perché una posizione non è soltanto latitudine e longitudine;
2. la differenza fra tempo civile e tempo monotono;
3. perché Android `Location` e iOS `CLLocation` non entrano nel core;
4. perché sequence e timestamp sono controlli distinti;
5. perché lo stream non viene ordinato silenziosamente;
6. come si rappresentano accuratezza, velocità, bearing e origine;
7. perché `GeoPoint` è stato estratto dal dominio routing;
8. come il primo campione stabilisce la baseline del replay;
9. come una velocità razionale evita deriva floating point;
10. come viene conservato il resto della divisione intera;
11. come funziona la state machine del replay;
12. perché il runner non dorme e non legge file;
13. come una fixture versionata diventa ground truth;
14. che cosa misura il benchmark e che cosa non dimostra;
15. come questa pipeline prepara map matching e guidance.

## Prerequisiti

- [Glossario](02-glossario.md)
- [Architettura generale](20-architettura-generale.md)
- [Contratti routing e fake provider](44-contratti-routing-e-fake-provider.md)
- [MapScene e fake renderer](45-map-scene-e-fake-renderer.md)
- [GPS replay e fixture](31-gps-replay-e-fixture.md)
- [Performance budget](32-performance-budget.md)

## Dal punto geografico al campione

Un punto WGS84 dice dove si trova un'osservazione, ma non dice:

- quando è arrivata;
- in quale ordine appartiene allo stream;
- quanto è accurata;
- se contiene una velocità;
- se contiene una direzione;
- se proviene dal dispositivo, dal simulatore o da un replay.

Per questo il runtime riceve un `LocationSample`:

```kotlin
LocationSample(
    sequence = LocationSequence(42),
    monotonicTime = MonotonicInstant(15_000),
    position = GeoPoint(41.9028, 12.4964),
    horizontalAccuracyMeters = 5.0,
    speedMetersPerSecond = 24.0,
    bearingDegrees = 135.0,
    origin = LocationSampleOrigin.Replay,
)
```

Il modello è immutabile e provider-neutral.

## Perché `GeoPoint` non appartiene al routing

La prima slice Kotlin aveva collocato `GeoPoint` in `routing-contracts`. Con la
localizzazione sarebbe nata questa dipendenza concettualmente sbagliata:

```text
location-contracts -> routing-contracts -> GeoPoint
```

Un campione GPS non è una route. Anche mappa, diario, presenza e fotografie
geolocalizzate useranno coordinate. Il tipo è quindi stato estratto in:

```text
shared/geo-contracts
```

La dipendenza diventa:

```text
geo-contracts
├── routing-contracts
├── location-contracts
└── altri domini geografici futuri
```

`routing-contracts` conserva temporaneamente un `typealias` Kotlin per la
compatibilità sorgente interna:

```kotlin
typealias GeoPoint = org.traveldna.geo.contracts.GeoPoint
```

Questa non è ancora una garanzia di compatibilità binaria pubblica.

### Zero con segno

IEEE 754 distingue `0.0` e `-0.0` a livello di bit. Geograficamente descrivono
lo stesso meridiano o parallelo. `GeoPoint` normalizza lo zero firmato prima di
calcolare uguaglianza e hash, così richieste e chiavi non cambiano per una
differenza priva di significato geografico.

## Tempo civile e tempo monotono

### Tempo civile

Il tempo civile è quello mostrato all'utente:

```text
17 luglio 2026, 18:30 Europe/Rome
```

Può cambiare per timezone, ora legale, NTP o correzione manuale. È utile al
diario, non per misurare intervalli nel loop di navigazione.

### Tempo monotono

`MonotonicInstant` contiene millisecondi non negativi da una sorgente monotona.
Serve per:

- ordinare osservazioni;
- calcolare intervalli;
- misurare timeout;
- riprodurre una traccia;
- evitare salti del calendario.

```kotlin
@JvmInline
value class MonotonicInstant(val milliseconds: Long)
```

L'adapter Android potrà derivarlo da una sorgente elapsed-realtime. L'adapter
iOS dovrà scegliere e documentare una sorgente monotona equivalente. Il core non
conosce l'API di piattaforma.

## Sequence e timestamp

Il timestamp da solo non identifica sempre univocamente un campione:

- due misure possono condividere lo stesso millisecondo;
- un provider può riconsegnare lo stesso evento;
- una coda può duplicare un messaggio;
- un test deve distinguere chiaramente gli elementi.

`LocationSequence` è un contatore locale alla sessione o alla sorgente canonica.
Non è un identificatore globale dell'utente e non deve uscire come dato sociale.

Il gate richiede contemporaneamente:

```text
new.sequence > lastAccepted.sequence
new.monotonicTime > lastAccepted.monotonicTime
```

## Metadati bounded

### Accuratezza orizzontale

```text
0 < accuracy <= 100000 metri
```

Il limite superiore evita input arbitrari. Un campione valido nel contratto non
è automaticamente adatto alla navigazione: una policy successiva potrà ridurre
la confidenza o ignorarlo.

### Velocità

```text
null oppure 0 <= speed <= 200 m/s
```

Il limite è volutamente permissivo e non costituisce un sistema antifrode.

### Bearing

```text
null oppure 0 <= bearing < 360 gradi
```

`360` non è accettato perché duplicherebbe `0`.

### Origine

```text
Platform
Replay
Simulator
```

L'origine aiuta test e diagnostica. Non sostituisce provenance, autorizzazioni o
trust model.

## `LocationSampleGate`

Il gate possiede soltanto l'ultimo campione accettato. Non mantiene una storia
illimitata.

```text
sample
-> sequence valida?
-> monotonic time valido?
-> Accepted oppure Rejected
```

Motivi di rifiuto v0:

```text
NonIncreasingSequence
NonIncreasingMonotonicTime
```

Un campione rifiutato:

- non aggiorna `lastAccepted`;
- non muove il clock virtuale;
- non modifica il resto del rate scaler;
- non viene riscritto;
- non viene riordinato;
- rimane osservabile come rifiuto.

### Perché non ordinare automaticamente

Dato:

```text
seq 10, t=1000
seq 12, t=3000
seq 11, t=2000
```

ordinare a posteriori nasconderebbe un problema di consegna. Nel runtime reale
`11` è arrivato dopo `12`; la pipeline deve trattarlo come stantio.

## Scenario di replay

`LocationReplayScenario` contiene:

```text
id stabile
snapshot dei campioni
PlaybackRate
```

Invarianti:

- almeno un campione;
- massimo 100.000 campioni;
- tutti i campioni hanno origine `Replay`;
- l'ordine è quello dichiarato;
- la lista del chiamante viene copiata.

Il limite di 100.000 rende bounded fixture, runner, summary e aspettative. Non è
un limite del futuro viaggio reale, che userà uno stream e storage separati.

## Velocità razionale

Una velocità di playback è una frazione positiva normalizzata:

```text
1/2 = metà velocità
1/1 = tempo reale logico
2/1 = doppia velocità
3/2 = 1,5x
```

Numeratore e denominatore sono compresi fra 1 e 1000.

Il delay teorico è:

```text
playbackDelay = sourceDelta * denominator / numerator
```

Usare una frazione evita divergenze floating point tra piattaforme.

### Conservare il resto

Con rate `3/2`, tre intervalli da un millisecondo devono produrre due
millisecondi totali:

```text
1 ms -> 0 ms, resto 2
1 ms -> 1 ms, resto 1
1 ms -> 1 ms, resto 0
```

`ReplayDelayScaler` conserva il resto. Le moltiplicazioni e il totale sono
controllati contro overflow.

## Il primo campione come baseline

Un timestamp monotono può essere assoluto rispetto all'avvio del dispositivo:

```text
5_432_100 ms
```

Il replay non deve attendere 5.432 secondi prima del primo evento. Il primo
campione accettato stabilisce la baseline:

```text
source delta = 0
playback delay = 0
clock = timestamp del primo campione
```

I campioni successivi usano la differenza dall'ultimo campione accettato.

## Orologio virtuale

`VirtualReplayClock.accept()`:

- al primo campione salva la baseline e restituisce zero;
- in seguito richiede tempo strettamente crescente;
- restituisce il delta;
- non legge il wall clock;
- non dorme;
- non avanza per campioni rifiutati.

Questo permette di simulare ore di viaggio in pochi millisecondi di CPU.

## State machine

```text
Ready
├── start -> Running
├── step  -> Paused oppure Completed
└── cancel -> Cancelled

Running
├── advance
├── pause -> Paused
├── runToEnd -> Completed
└── cancel -> Cancelled

Paused
├── step
├── resume -> Running
├── runToEnd -> Completed
└── cancel -> Cancelled
```

`Completed` e `Cancelled` sono terminali.

Il runner è single-owner e sincrono. La cancellazione v0 avviene fra due passi;
non è ancora cancellazione coroutine concorrente.

## Stato bounded del runner

Il runner conserva:

```text
nextIndex
acceptedSamples
rejectedSamples
rejectionCounts per enum
last accepted sample
virtual clock
rate remainder
total playback delay
state
```

Non conserva l'elenco completo degli eventi prodotti. Il chiamante decide se
streammarli, aggregarli o salvarli.

## `ReplaySummary`

Il summary pubblico verifica:

- ogni contatore tra zero e 100.000;
- somma accepted/rejected eseguita in `Long`;
- reason counts positive e bounded;
- somma reason uguale ai rifiuti;
- clock e sequence presenti esattamente quando esiste un accepted;
- delay zero quando non esistono accepted;
- `Ready` senza campioni processati;
- `Completed` con almeno un campione accettato.

Queste regole proteggono anche costruzioni manuali fuori dal runner.

## Fixture `TDNA_LOCATION_REPLAY_V0`

La fixture è line-oriented e leggibile durante una lezione:

```text
TDNA_LOCATION_REPLAY_V0
scenario reference-location-replay-v0
rate 2 1
sample 0 0 37.500000 15.100000 5.0 20.0 90.0 replay
...
expect accepted 4
expect rejected 2
expect final_time_ms 3000
expect playback_delay_ms 1500
expect last_sequence 4
expect reason non_increasing_sequence 1
expect reason non_increasing_monotonic_time 1
```

Il parser JVM resta fuori dal core common:

```text
file I/O
-> ReplayFixtureParser JVM
-> LocationReplayScenario common
-> DeterministicReplayRunner common
```

Il parser verifica:

- file non vuoto e massimo 32 MiB;
- header come prima direttiva;
- scenario e rate prima dei campioni;
- massimo 100.000 campioni;
- origine obbligatoria `replay`;
- nessun campione dopo le aspettative;
- aspettative uniche, positive e bounded;
- somma delle aspettative uguale ai campioni;
- reason counts coerenti;
- ground truth uguale al summary reale.

## Report canonico del Lab

Comando:

```bash
sh tools/tdna lab location-replay
```

Forma dell'output:

```json
{"scenario":"reference-location-replay-v0","rate":"2/1","state":"completed","processed":6,"accepted":4,"rejected":2,"rejection_counts":{"non_increasing_monotonic_time":1,"non_increasing_sequence":1},"final_time_ms":3000,"playback_delay_ms":1500,"last_sequence":4}
```

Il JSON ristretto è un output didattico deterministico, non uno schema pubblico
di telemetria.

## Mappa del codice

```text
Main.main()
-> ReplayFixtureParser.parse()
-> LocationReplayScenario
-> DeterministicReplayRunner.runToEnd()
   -> LocationSampleGate.evaluate()
   -> VirtualReplayClock.accept()
   -> ReplayDelayScaler.scale()
   -> ReplayEvent
-> ReplaySummary
-> ReplayExpectations.requireMatches()
-> canonicalReplayReport()
```

Ownership:

| Componente | Stato posseduto | I/O |
| --- | --- | --- |
| `ReplayFixtureParser` | builder temporanei | file JVM |
| `LocationReplayScenario` | snapshot input | nessuno |
| `LocationSampleGate` | ultimo accepted | nessuno |
| `VirtualReplayClock` | ultimo tempo accepted | nessuno |
| `ReplayDelayScaler` | resto intero | nessuno |
| `DeterministicReplayRunner` | state machine e contatori | nessuno |
| CLI | report e benchmark | stdout |

## Tracepoint logici

I nomi sono documentali, non logging nel hot path:

```text
LOCATION_REPLAY_FIXTURE_PARSED
LOCATION_SAMPLE_RECEIVED
LOCATION_SAMPLE_ACCEPTED
LOCATION_SAMPLE_REJECTED
REPLAY_CLOCK_BASELINE_ESTABLISHED
REPLAY_CLOCK_ADVANCED
REPLAY_STATE_CHANGED
LOCATION_REPLAY_COMPLETED
LOCATION_REPLAY_REPORT_EMITTED
```

## Test

La slice verifica:

- confini WGS84 e zero con segno;
- accuratezza, velocità e bearing invalidi;
- sequence duplicata;
- tempo non crescente;
- rifiuto senza avanzamento della baseline;
- primo timestamp non zero;
- rate normalizzato e resto conservato;
- pause, step, resume e cancel;
- snapshot delle collezioni;
- summary e aspettative bounded;
- fixture malformate o incoerenti;
- report esatto;
- JVM e Linux x64;
- confini architetturali.

## Benchmark diagnostico

Comando:

```bash
sh tools/tdna bench location-replay 10000 7
```

Metodo:

- 10.000 campioni sintetici validi;
- tre warm-up;
- sette run misurati;
- minimo, mediana e massimo;
- mediana per campione;
- nessuna soglia CI.

Il benchmark misura soltanto:

```text
LocationSampleGate
+ VirtualReplayClock
+ ReplayDelayScaler
+ contatori/state machine
```

Non misura GPS, parser fixture, rete, database, MapLibre, map matching, batteria
o latenza end-to-end mobile. Ambiente e risultato della sessione sono registrati
nel report giornaliero indicizzato.

## Percorso caldo futuro

```text
platform adapter
-> LocationSample
-> LocationSampleGate
-> filter/map match
-> route progress
-> NavigationSnapshot
-> MapSceneDelta
```

Il file parser, la CLI e il report JSON non entrano in questo percorso.

## Alternative considerate

### Ordinare i campioni nel runner

Scartato: nasconde difetti di consegna e non rappresenta il comportamento real-time.

### Usare epoch time

Scartato per intervalli: può saltare per correzioni civili.

### Usare `Double` per il rate

Scartato nel replay deterministico: introduce arrotondamento dipendente dalla
sequenza di operazioni.

### Conservare tutti gli eventi nel runner

Scartato: trasforma un componente bounded in una cronologia crescente.

### Mettere il parser nel modulo common

Scartato: mescolerebbe I/O e hot-path contracts.

### Riutilizzare la vecchia PR stacked

Scartato: era basata su un `main` precedente e le sue review non erano riutilizzabili.
La slice è stata ricostruita dal `main` verificato.

## Errori comuni

- confondere monotonic time e data del diario;
- accettare sequence uguali;
- correggere silenziosamente un timestamp;
- avanzare il clock per un rifiuto;
- attendere il timestamp assoluto del primo campione;
- perdere il resto della divisione;
- usare collezioni non bounded;
- chiamare il benchmark una prova di produzione;
- mettere file I/O nel runner;
- importare tipi Android/iOS nei contratti shared;
- pubblicare una traccia GPS personale come fixture.

## Esercizi

1. Aggiungere una fixture con bearing assente.
2. Dimostrare con un test che un rifiuto non consuma il resto del rate scaler.
3. Progettare un nuovo motivo `AccuracyPolicyRejected` senza confonderlo con la
   validazione strutturale del modello.
4. Aggiungere un sink che conserva soltanto gli ultimi N eventi.
5. Confrontare rate `1/2`, `1/1`, `2/1` e `3/2`.
6. Progettare l'adapter Android senza importare `android.location.Location` nel
   modulo common.
7. Disegnare la futura pipeline raw position -> matched position.
8. Spiegare perché la sequence non deve essere un identificatore social.
9. Creare un test property-based per sequenze valide e invalide.
10. Valutare come serializzare il contratto senza congelare prematuramente l'ABI.

## Non-obiettivi

- GPS reale;
- adapter Android o iOS;
- map matching;
- route progress;
- off-route e reroute;
- accuratezza stradale;
- consumo batteria;
- prestazioni di produzione;
- memorizzazione di viaggi personali;
- sincronizzazione cloud.

## Documenti successivi

- [Scenario Lab location replay](lab/scenarios/location-replay-deterministico.md)
- [Mappa del codice e degli stati](40-mappa-codice-e-stati.md)
- [Tracepoint Model](41-tracepoint-model-v0.md)
- [Routing e navigazione](24-routing-e-navigazione.md)
- [Performance budget](32-performance-budget.md)
- [Registro milestone](50-registro-milestone.md)
