# Posizione matched e avanzamento canonico sulla route

## Stato del capitolo

`implementation-backed — Foundations and Travel DNA Lab v0`

Questo capitolo documenta il confine fra un futuro componente di map matching e
il runtime che segue una `RoutePlan` già nota. Il codice si trova in:

```text
shared/navigation-contracts/
shared/route-progress/
shared/route-progress-map-projector/
labs/route-progress-cli/
fixtures/navigation/
```

La slice non decide su quale strada si trovi un campione GPS. Riceve una
posizione già associata alla geometria della route e verifica identità, ordine,
progresso, leg attiva, manovra e arrivo.

## Cosa imparerai

Al termine dovresti saper spiegare:

1. la differenza fra posizione raw, filtrata, matched e route progress;
2. perché `MatchedRoutePosition` non è un algoritmo di map matching;
3. come indice e frazione descrivono un punto lungo una polilinea;
4. perché route ID, sequence, tempo e coordinata hanno invarianti separate;
5. perché un veicolo fermo può produrre coordinate route uguali;
6. perché una regressione viene rifiutata invece di essere corretta in silenzio;
7. come si sceglie la leg attiva a un confine condiviso;
8. come si sceglie la prossima manovra senza scansioni lineari;
9. perché all'arrivo viene preferita una manovra `Arrive` esplicita;
10. perché indice geometrico, distanza e tempo non sono sinonimi;
11. come si verifica una volta il legame route-overlay;
12. come lo snapshot diventa un delta mappa `O(1)` per campione;
13. cosa misura il benchmark e cosa lascia fuori;
14. come questa slice prepara off-route e rerouting senza implementarli.

## Prerequisiti

- [Routing e navigazione](24-routing-e-navigazione.md)
- [Contratti routing e fake provider](44-contratti-routing-e-fake-provider.md)
- [MapScene e fake renderer](45-map-scene-e-fake-renderer.md)
- [LocationSample e replay](46-location-sample-e-replay-deterministico.md)
- [Performance budget](32-performance-budget.md)

## Quattro rappresentazioni diverse

### Posizione raw

È l'osservazione del sensore o del provider:

```text
latitudine, longitudine, accuratezza, velocità, bearing, tempo
```

Travel DNA la normalizza in `LocationSample`.

### Posizione filtrata

Un futuro filtro ridurrà rumore, spike e valori non plausibili. Questa slice non
contiene ancora il filtro.

### Posizione matched

Un futuro map matcher sceglierà una route o strada candidata e produrrà una
ipotesi del tipo:

```text
route R
segmento fra i punti 42 e 43
frazione 0,35
lateral distance 2,4 m
confidence Medium
```

Il risultato canonico è `MatchedRoutePosition`.

### Route progress

Il tracker confronta la nuova ipotesi con la route e con l'ultima posizione
accettata:

```text
route corretta?
indice valido?
sequence crescente?
tempo crescente?
coordinata non regressiva?
quale leg è attiva?
quale manovra viene dopo?
siamo arrivati?
```

Non ripete il map matching.

## `RouteCoordinate`

```kotlin
RouteCoordinate(
    completedGeometryIndex = 42,
    fractionToNext = 0.35,
)
```

Significa che il punto `42` è l'ultimo punto geometrico raggiunto e la posizione
è al 35% del segmento verso il punto `43`.

Invarianti:

```text
completedGeometryIndex >= 0
fraction finita
0 <= fraction < 1
```

`1.0` è escluso perché duplichererebbe la forma canonica:

```text
index 42, fraction 1.0
index 43, fraction 0.0
```

Lo zero firmato viene normalizzato, quindi `-0.0` e `0.0` hanno uguaglianza e
hash coerenti.

## `MatchedRoutePosition`

Il contratto contiene:

```text
routeId
sampleSequence
monotonicTime
RouteCoordinate
lateralDistanceMeters
confidence
```

### Route ID

Collega la posizione a una route canonica precisa. Un tracker legato alla route
A rifiuta una posizione per la route B. Il processo applicativo non deve riusare
lo stesso ID per geometrie differenti.

### Sequence e tempo

Derivano dallo stream di posizione e devono crescere entrambi rispetto
all'ultima posizione accepted.

### Distanza laterale

```text
0 <= lateralDistanceMeters <= 100000
```

È un metadato del matcher, non una decisione off-route. La v0 non introduce una
soglia di deviazione.

### Confidenza

```text
Low, Medium, High
```

È una classificazione Travel DNA, non una probabilità universale. Il tracker non
rifiuta automaticamente `Low`; una futura policy potrà sospendere o degradare la
guidance.

## Tre monotonicità separate

```text
new.sequence > previous.sequence
new.time > previous.time
new.coordinate >= previous.coordinate
```

L'uguaglianza della coordinata è valida: un veicolo fermo può ricevere campioni
nuovi senza avanzare sulla geometria. Una coordinata inferiore viene rifiutata
come `RegressedAlongRoute`.

La v0 non applica tolleranze nascoste. Rumore e isteresi appartengono al matcher
o a una futura policy esplicita e testabile.

## Precedenza dei rifiuti

Il tracker usa un ordine stabile:

```text
1. RouteMismatch
2. GeometryIndexOutOfBounds
3. FinalPointHasFraction
4. NonIncreasingSequence
5. NonIncreasingMonotonicTime
6. RegressedAlongRoute
```

Un input può violare più regole. La precedenza rende deterministici test,
diagnostica e report.

## `inspect`, `accept` e `reset`

```kotlin
val decision = tracker.inspect(position) // non muta
val decision = tracker.accept(position)  // commit solo se Accepted
tracker.reset()                          // nuova sessione sulla stessa route
```

Una route sostitutiva usa un nuovo `RouteProgressTracker` legato al nuovo
`RoutePlan`.

## Stato posseduto

Il tracker conserva:

```text
RoutePlan immutabile
cursori manovra precomputati
manovra Arrive finale pre-selezionata
ultimo RouteProgressSnapshot accepted
```

Non conserva campioni storici, decisioni storiche, file, rete, database, stato
del renderer o geometrie ricostruite per campione. Lo stato è bounded dalla
route installata e non cresce con la durata del viaggio.

## Leg attiva

Le leg sono contigue e condividono il punto di confine:

```text
leg 0: geometry 0 .. 2
leg 1: geometry 2 .. 3
```

Policy v0:

```text
prima del punto 2 -> leg 0
al punto 2        -> leg 1
oltre il punto 2  -> leg 1
punto finale      -> ultima leg
```

La ricerca usa binary search sugli `geometryEndIndex`:

```text
O(log numero-leg)
```

## Prossima manovra

Ogni `RouteManeuverCursor` conserva:

```text
legIndex
maneuverIndex nella leg
RouteManeuver
```

Policy:

- con `fraction == 0`, una manovra sul punto corrente è ancora eleggibile;
- con `fraction > 0`, le manovre sul punto corrente sono considerate superate;
- al confine vengono saltate le manovre della leg precedente;
- viene scelta la prima manovra della leg attiva o di una leg futura;
- in assenza di manovre il valore è `null`.

La ricerca usa binary search per indice geometrico e un piccolo skip dei soli
duplicati di confine:

```text
O(log numero-manovre)
```

## Arrivo e tie-break `Arrive`

La v0 considera arrivata una posizione quando:

```text
completedGeometryIndex == route.geometry.lastIndex
fractionToNext == 0
```

Una route può contenere più manovre sul punto finale. Il tracker pre-seleziona,
una volta durante la costruzione, la prima manovra `Arrive` della leg finale. A
`arrived = true` espone soltanto quella manovra oppure `null`; non presenta come
"prossima" una svolta non-arrival già collocata sul punto finale.

`arrived` non dimostra che il veicolo sia parcheggiato o che il viaggio sia stato
chiuso. Descrive soltanto la fine della geometria route.

## `RouteProgressSnapshot`

Contiene:

```text
MatchedRoutePosition accepted
activeLegIndex
upcomingManeuver oppure null
arrived
```

Il contratto rifiuta un cursore appartenente a una leg già completata. Uno
snapshot arrived può esporre soltanto una manovra `Arrive` oppure nessuna
manovra.

Non contiene ancora distanza percorsa/rimanente, distanza alla manovra, ETA,
velocità filtrata o stato off-route.

## Perché non calcolare distanza dall'indice

I punti della geometria non sono equidistanti. Cinquanta punti su cento non
significano il 50% della distanza e non permettono di ricostruire il costing del
provider. Fino a quando il contratto non possiede lunghezze cumulative affidabili,
Travel DNA non fabbrica distanze o ETA.

## Binding route-overlay verificato una volta

Confrontare l'intera geometria a ogni campione violerebbe il percorso caldo.
Controllare soltanto il `RouteId`, però, permetterebbe a un overlay stale con lo
stesso ID e geometria diversa di ricevere indici errati.

La soluzione è separare installazione e aggiornamento:

```kotlin
val binding = RouteProgressMapProjector.bind(
    sceneId = scene.id,
    overlay = routeOverlay,
    route = routePlan,
)
```

`bind` viene chiamato quando la route viene installata e verifica:

- route ID uguale;
- geometria overlay uguale alla geometria canonica;
- numero di punti da conservare nel binding.

Il binding non è costruibile liberamente fuori dal modulo projector.

Nel loop:

```kotlin
val delta = RouteProgressMapProjector.project(binding, snapshot)
```

La proiezione verifica soltanto route ID, indice, punto finale e frazione e
produce:

```text
MapSceneDelta.UpdateRouteProgress
  sceneId
  routeOverlayId
  completedGeometryIndex
  fractionToNext
```

Il costo per update è `O(1)` e non copia geometria né costruisce GeoJSON.

## Percorso caldo target

```text
LocationSample
-> filter
-> map matcher
-> MatchedRoutePosition
-> RouteProgressTracker.accept
-> RouteProgressSnapshot
-> RouteProgressMapProjector.project(binding, snapshot)
-> MapSceneDelta.UpdateRouteProgress
-> renderer adapter
```

Nel loop non entrano plugin lookup, file, parser, rete, database, logging verboso,
serializzazione o ricostruzione della route.

## Fixture didattica

Il Lab usa una route sintetica definita in Kotlin:

```text
4 punti geometrici
2 leg
confine condiviso all'indice 2
manovra finale della leg 0 all'indice 2
manovra iniziale della leg 1 all'indice 2
6 posizioni matched
1 regressione intenzionale
```

Metadati:

- [`reference-route-progress-v0.meta.yaml`](../../fixtures/navigation/reference-route-progress-v0.meta.yaml)
- [policy fixture navigation](../../fixtures/navigation/README.md)

Non viene introdotto un nuovo parser finché non esiste un requisito reale di
import/replay di posizioni matched.

## Output del Lab

```bash
sh tools/tdna lab route-progress
```

```json
{"scenario":"reference-route-progress-v0","accepted":5,"rejected":1,"rejection":"RegressedAlongRoute","boundary_leg":1,"boundary_maneuver":"Continue","completed_index":3,"fraction":0.0,"active_leg":1,"upcoming_maneuver":"Arrive","arrived":true,"delta_index":3}
```

Il Lab verifica il ground truth prima di stampare.

## Mappa del codice

```text
Main.runLab
-> referenceRoute
-> MatchedRoutePosition list
-> RouteProgressTracker.accept
   -> rejection
   -> findActiveLegIndex
   -> findUpcomingManeuver
   -> RouteProgressSnapshot
-> RouteProgressMapProjector.bind       // installazione
-> RouteProgressMapProjector.project    // update compatto
-> report JSON
```

Ownership:

| Componente | Stato | Frequenza |
| --- | --- | --- |
| `RoutePlan` | geometria, leg e manovre | installazione route |
| `RouteProgressTracker` | cursori e ultimo snapshot | sessione |
| `RouteProgressMapBinding` | identità scena/overlay/route e point count | installazione |
| `MatchedRoutePosition` | output matcher | per campione |
| `RouteProgressSnapshot` | read model | per accepted |
| projector update | nessuno | per accepted |
| renderer | geometria e progress | scena |

## Tracepoint logici

```text
MATCHED_ROUTE_POSITION_RECEIVED
MATCHED_ROUTE_POSITION_REJECTED
ROUTE_PROGRESS_ACCEPTED
ROUTE_ACTIVE_LEG_CHANGED
ROUTE_UPCOMING_MANEUVER_CHANGED
ROUTE_ARRIVAL_REACHED
MAP_ROUTE_PROGRESS_BOUND
MAP_PROGRESS_DELTA_PROJECTED
```

Sono nomi documentali, non logging automatico.

## Test

La slice verifica:

- indice/frazione e zero firmato;
- lateral distance bounded;
- route mismatch, indice fuori geometria e frazione invalida sul finale;
- sequence/tempo non crescenti e regressione di indice/frazione;
- rifiuto senza mutazione e posizione stationary accepted;
- boundary policy e priorità della nuova leg;
- tie-break `Arrive` al punto finale;
- snapshot con cursore di leg completata rifiutato;
- `inspect`, `accept` e `reset`;
- binding che rifiuta route ID o geometria differente anche a pari lunghezza;
- projector update per la route bound;
- test common, JVM e Linux x64;
- architecture boundaries.

## Benchmark diagnostico

```bash
sh tools/tdna bench route-progress 10000 7
```

Scenario:

- 10.000 posizioni accepted;
- geometria con 10.000 punti;
- fino a 100 leg;
- una manovra per leg più `Arrive`;
- tre warm-up;
- sette run dispari;
- minimo, mediana e massimo;
- nessuna soglia CI.

Route, input, costruzione del tracker e preprocessing dei cursori avvengono fuori
dalla finestra misurata. Prima di ogni run il tracker viene resettato fuori dal
timer. Il dato misura `accept`, le ricerche binarie, la costruzione dello snapshot
e il commit dello stato.

Non misura GPS, filtro, map matching, binding/projector, renderer, rete, database,
batteria, dispositivo mobile o affidabilità su strada.

Il risultato osservato viene registrato nel report giornaliero e nell'artifact
CI; non è un SLA.

## Alternative considerate

### Usare direttamente `RouteOverlayProgress`

Scartato: farebbe dipendere il runtime navigation dal contratto di rendering.

### Confrontare la geometria a ogni update

Scartato: è `O(numero punti)` per campione. Il binding fa il controllo una volta.

### Fidarsi soltanto del route ID

Scartato nel bordo map: un overlay stale o costruito male potrebbe avere lo stesso
ID. Il binding confronta anche la geometria durante l'installazione.

### Rifiutare coordinate uguali

Scartato: un veicolo fermo produce campioni nuovi senza avanzamento geometrico.

### Correggere automaticamente le regressioni

Scartato: nasconde il comportamento del matcher. L'isteresi sarà una policy
separata.

### Scansione lineare di leg e manovre

Scartata per il percorso caldo: la v0 usa binary search.

### Calcolare ETA dalla frazione geometrica

Scartato: indice e frazione non rappresentano distanza o tempo affidabili.

### Integrare subito map matching

Scartato: confonderebbe ricerca candidata, scoring e progress tracking.

## Errori comuni

- chiamare `MatchedRoutePosition` un GPS fix;
- credere che confidence sia una probabilità universale;
- trattare lateral distance come off-route già deciso;
- rifiutare un veicolo fermo;
- accettare una regressione senza policy;
- scegliere la leg precedente al confine;
- esporre una manovra non-Arrive dopo l'arrivo;
- confrontare tutta la geometria nel loop;
- fidarsi di un overlay same-ID ma non verificato;
- ricostruire la geometria a ogni update;
- calcolare distanza da indice/frazione;
- trasformare il benchmark CI in SLA.

## Esercizi

1. Aggiungere una posizione stationary con confidence `Low`.
2. Progettare una policy separata che sospende guidance con confidence `Low`.
3. Aggiungere tre manovre sullo stesso geometry index e definire il tie-break.
4. Progettare cumulative segment distances senza dipendere dal renderer.
5. Aggiungere un test con 100 leg e confini condivisi.
6. Disegnare il contratto di un vero map matcher.
7. Progettare una isteresi per piccole regressioni.
8. Collegare uno snapshot accepted a un fake renderer.
9. Misurare benchmark con 1, 10 e 100 leg.
10. Progettare route replacement senza riusare route ID.

## Non-obiettivi

- map matching reale;
- filtro GPS;
- distanza o ETA;
- off-route e rerouting;
- voce;
- adapter Android/iOS;
- MapLibre;
- dati stradali reali;
- benchmark di produzione.

## Documenti successivi

- [Scenario Lab route progress](lab/scenarios/route-progress-tracker.md)
- [Mappa codice e stati](40-mappa-codice-e-stati.md)
- [Tracepoint Model](41-tracepoint-model-v0.md)
- [Scenario missed exit](lab/scenarios/navigation-missed-exit-reroute.md)
- [Registro milestone](50-registro-milestone.md)
