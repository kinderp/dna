# Porta di map matching e fake deterministico

## Stato del capitolo

`implementation-backed — Navigation Runtime Replay v0`

Questo capitolo documenta il confine che completa il percorso fra il campione di
posizione e il tracker di avanzamento sulla route:

```text
LocationSample
-> MapMatcherPort.bind(RoutePlan)
-> MapMatchSession.match(sample)
-> Matched / Unmatched / Failure
-> RouteProgressTracker.accept(position)
```

Il codice si trova in:

```text
shared/map-matching-contracts/
shared/map-matching-testkit/
shared/fake-map-matcher/
labs/map-matching-cli/
fixtures/navigation/
```

La slice non implementa un algoritmo stradale. Il fake restituisce esiti
predefiniti da un catalogo esatto e serve a studiare contratto, ownership,
postcondizioni, errori e integrazione con il route progress.

## Cosa imparerai

Al termine dovresti saper spiegare:

1. che cosa distingue un campione GPS da una posizione associata alla route;
2. perché map matching, route progress e off-route sono problemi diversi;
3. perché la route viene legata una volta a una sessione;
4. perché non passiamo l'intera `RoutePlan` a ogni campione;
5. la differenza fra `Matched`, `Unmatched` e `Failure`;
6. quali identità devono essere conservate da un risultato matched;
7. perché `Unmatched` non è un'eccezione;
8. perché una cancellazione coroutine non è un errore provider;
9. perché la provenance del provider resta esplicita;
10. come funziona un fake a catalogo esatto;
11. perché il test di determinismo usa una sessione fresca;
12. come si mantengono bounded le diagnostiche e i report del testkit;
13. quali controlli appartengono al testkit riutilizzabile;
14. come un risultato validato entra nel `RouteProgressTracker`;
15. cosa misura il benchmark e cosa lascia intenzionalmente fuori.

## Prerequisiti

- [Contratti routing e fake provider](44-contratti-routing-e-fake-provider.md)
- [LocationSample e replay deterministico](46-location-sample-e-replay-deterministico.md)
- [Posizione matched e route progress](47-posizione-matched-e-route-progress.md)
- [Architettura plugin e provider](22-architettura-plugin-provider.md)
- [Performance budget](32-performance-budget.md)

## Il problema di confine

Il capitolo 46 produce campioni canonici:

```text
LocationSample
  sequence
  monotonicTime
  position
  horizontalAccuracy
  speed opzionale
  bearing opzionale
```

Il capitolo 47 inizia da una posizione già associata alla route:

```text
MatchedRoutePosition
  routeId
  sampleSequence
  monotonicTime
  RouteCoordinate
  lateralDistance
  confidence
```

Mancava il componente che trasforma il primo modello nel secondo senza
obbligare applicazione e test a conoscere Valhalla, Ferrostar, un motore futuro
in Rust o un SDK mobile.

La nuova porta rende esplicito quel confine:

```text
MapMatcherPort
-> MapMatchSession
-> MapMatchResult
```

## Che cos'è il map matching

Un map matcher reale riceve osservazioni rumorose e valuta una o più posizioni
plausibili su una rete o su una route. Può considerare:

- distanza geometrica;
- direzione del veicolo;
- accuratezza dichiarata;
- velocità;
- continuità con le osservazioni precedenti;
- topologia e restrizioni stradali;
- probabilità o score specifici del motore.

Questa slice non implementa nessuno di questi algoritmi. Definisce soltanto come
Travel DNA chiede un risultato e come lo valida prima di usarlo.

## Tre problemi distinti

```text
map matching
  sceglie una posizione plausibile sulla route

route progress
  verifica ordine e avanzamento sulla route scelta

off-route policy
  interpreta evidenza ripetuta e decide se confermare una deviazione
```

Fondere questi problemi in una sola classe renderebbe più difficile:

- sostituire il provider;
- testare il progress senza GPS;
- confrontare algoritmi;
- spiegare gli errori;
- misurare i singoli costi;
- introdurre isteresi senza soglie nascoste.

## Perché una sessione legata alla route

La porta è una factory:

```kotlin
interface MapMatcherPort : TravelDnaPlugin {
    fun bind(route: RoutePlan): MapMatchSession
}
```

La sessione contiene la route canonica:

```kotlin
interface MapMatchSession {
    val route: RoutePlan
    suspend fun match(sample: LocationSample): MapMatchResult
}
```

Questa forma sostituisce deliberatamente un ipotetico:

```kotlin
match(MapMatchRequest(route, sample))
```

che ripeterebbe la route a ogni campione.

Il binding permette a un adapter reale di preparare una volta:

- indice dei segmenti;
- strutture geometriche;
- mapping fra geometria provider e `RouteCoordinate`;
- cache temporanee bounded;
- configurazione del profilo di matching.

Nel percorso caldo rimane:

```text
session.match(sample)
```

La route non viene serializzata, copiata o cercata nel registry per ogni update.

## Cancellazione e lifecycle

`match` è una funzione sospendibile. Una cancellazione coroutine può significare:

- viaggio terminato;
- route sostituita;
- schermata o servizio distrutto;
- nuovo tentativo che rende inutile quello precedente;
- shutdown del runtime.

Questa cancellazione è un segnale del caller, non un errore restituito dal
provider. Un adapter deve quindi lasciare propagare la cancellazione e non
convertirla in:

```kotlin
MapMatchResult.Failure(
    MapMatchError(MapMatchErrorCode.Internal, ...)
)
```

Confondere i due casi provocherebbe retry indesiderati, metriche false e cleanup
incompleto. Timeout e indisponibilità del provider restano invece errori
canonici quando sono realmente esiti del provider o della policy applicativa.

## Capability

Il provider dichiara capability provider-neutral:

```text
navigation.map-match
navigation.map-match.deterministic
navigation.map-match.offline
```

`navigation.map-match` è obbligatoria per implementare la porta.

`deterministic` non significa che un algoritmo reale sia privo di stato. Significa
che, a parità di route, configurazione e stream iniziale, una nuova sessione
produce lo stesso esito osservabile.

`offline` dichiara che il provider può eseguire il matching senza una richiesta
di rete nel percorso critico. Non dimostra automaticamente consumo, precisione o
disponibilità dei dati necessari.

## I tre esiti

### `Matched`

```kotlin
MapMatchResult.Matched(
    position = MatchedRoutePosition(...),
    provenance = MapMatchProvenance(...),
)
```

Il provider ha scelto una posizione sulla route. Prima che il risultato lasci il
confine vengono verificate postcondizioni precise.

### `Unmatched`

```kotlin
MapMatchResult.Unmatched(
    MapMatchUnmatched(
        reason = MapMatchUnmatchedReason.NoCandidate,
    ),
)
```

È un esito normale: il provider non possiede abbastanza evidenza per associare
il campione alla route.

Motivi v0:

```text
NoCandidate
InsufficientConfidence
OutsideRouteEnvelope
```

`Unmatched` non è un crash, non è necessariamente off-route e non autorizza da
solo un reroute.

### `Failure`

```kotlin
MapMatchResult.Failure(
    MapMatchError(
        code = ProviderUnavailable,
        message = "...",
        retryable = true,
    )
)
```

Il provider non ha potuto eseguire correttamente l'operazione.

Codici v0:

```text
InvalidRequest
ProviderUnavailable
Timeout
RateLimited
Internal
```

`InvalidRequest` non è retryable senza cambiare la richiesta o il contratto.

La distinzione fondamentale è:

```text
Unmatched = operazione eseguita, nessuna associazione affidabile
Failure   = operazione non completata correttamente
Cancelled = caller/runtime interrompe il lavoro; la cancellazione propaga
```

## Postcondizioni di un risultato matched

Un adapter non può restituire liberamente qualsiasi `MatchedRoutePosition`.
`requireMatches` verifica:

```text
position.routeId == bound route.id
position.sampleSequence == input sample.sequence
position.monotonicTime == input sample.monotonicTime
geometry index dentro route.geometry.indices
fraction == 0 sul punto geometrico finale
provenance.providerId == provider selezionato
```

Questi controlli proteggono da errori come:

- risultato appartenente a una route precedente;
- risposta correlata al campione sbagliato;
- timestamp ricostruito dal provider;
- indice provider non convertito nel modello canonico;
- frazione oltre la fine della route;
- provenance falsa o mancante.

Non viene richiesto che la coordinata matched coincida con la posizione raw: il
map matching serve proprio a produrre una posizione diversa ma plausibile sulla
route.

## Provenance

```kotlin
MapMatchProvenance(
    providerId = PluginId("org.example.matcher"),
    providerMatchId = "optional-bounded-id",
)
```

La provenance permette di:

- diagnosticare quale adapter ha prodotto l'esito;
- confrontare provider in shadow mode;
- correlare report interni bounded;
- evitare che un risultato di un provider venga attribuito a un altro.

`providerMatchId` è opzionale e bounded. Non deve contenere coordinate, token,
URL firmati o payload personali soltanto per comodità di debug.

## Il fake a catalogo esatto

`FakeMapMatcher` riceve una lista di:

```text
RoutePlan + LocationSample -> MapMatchResult
```

Il fake non calcola distanza e non effettua snapping. Cerca una chiave esatta:

```text
routeId + LocationSample
```

Un catalog miss produce:

```text
Unmatched(NoCandidate, "fake.catalog-miss")
```

Questa scelta rende i test leggibili:

```text
dato questo campione
su questa route
restituisci esattamente questo esito
```

Il fake valida all'avvio:

- massimo 100.000 entry;
- nessuna chiave duplicata;
- stesso `RouteId` non riutilizzato per route diverse;
- risultato matched coerente con route, campione e provider;
- descriptor con capability obbligatoria.

## Lookup e diagnostica bounded

Il catalogo usa una mappa per lookup medio `O(1)`.

Le ultime chiamate sono conservate in una `ArrayDeque` bounded:

```text
maxRecordedCalls <= 10.000
```

Quando la finestra è piena viene rimosso l'elemento più vecchio in tempo
ammortizzato `O(1)`. Non usiamo una `MutableList.removeAt(0)`, che sposterebbe gli
elementi a ogni campione.

Il fake conserva:

```text
counter totale bounded da Long
finestra recente bounded
nessuna cronologia completa del viaggio
```

È single-threaded. La concorrenza di un adapter reale appartiene a test di
integrazione e al runtime che possiede la sessione.

## Determinismo e sessioni stateful

Un errore comune sarebbe verificare il determinismo così:

```text
session.match(sample)
session.match(sample)
```

Un matcher reale può mantenere storia dello stream. Ripetere lo stesso sequence e
lo stesso tempo nella stessa sessione potrebbe essere illegittimo.

Il testkit usa invece:

```text
session A, route R -> match(sample S)
session B nuova, route R -> match(sample S)
confronta i risultati
```

Questa definizione permette provider stateful ma richiede riproducibilità a
partire dallo stesso stato iniziale.

## Testkit riutilizzabile

`MapMatcherContractProbe` verifica:

```text
dichiara navigation.map-match
lega la RoutePlan senza cambiarla
restituisce un Matched canonico
ripete lo stesso esito da una sessione fresca se deterministic
restituisce Unmatched per la fixture prevista
restituisce Failure con il codice previsto per la fixture prevista
```

Il report crea una copia difensiva della lista dei check e applica bounds:

```text
providerId: non vuoto, massimo 128 caratteri
checks: da 1 a 32
ogni check: non vuoto, unico, massimo 128 caratteri
```

Un chiamante non può mutare retroattivamente un report già validato né usarlo per
accumulare diagnostica illimitata.

Il testkit non misura accuratezza geografica. Un provider può superare il
contratto e restare un pessimo matcher: correttezza dell'interfaccia e qualità
dell'algoritmo sono proprietà diverse.

## Lab eseguibile

```bash
sh tools/tdna lab map-matching
```

La fixture code-defined contiene sei campioni:

```text
sequence 0 -> Matched, geometry index 0
sequence 1 -> Matched, geometry index 1
sequence 2 -> Unmatched / NoCandidate
sequence 3 -> Matched, geometry index 2
sequence 4 -> Matched, geometry index 3 / arrival
sequence 5 -> Failure / ProviderUnavailable
```

Soltanto i quattro `Matched` entrano nel `RouteProgressTracker`.

Output canonico:

```json
{"scenario":"reference-map-matching-v0","provider":"org.traveldna.fake-map-matcher","contract_checks":6,"matched":4,"unmatched":1,"failed":1,"unmatched_reason":"NoCandidate","failure_code":"ProviderUnavailable","progress_accepted":4,"final_index":3,"arrived":true,"calls":6}
```

Il Lab dimostra che:

- matched, unmatched e failure restano distinti;
- la postcondizione viene applicata prima del progress;
- il campione unmatched non modifica il tracker;
- il failure non viene convertito in unmatched;
- i matched successivi possono continuare lo stream;
- il risultato finale raggiunge l'arrival della route sintetica.

## Percorso del codice

```text
Main.runLab
-> MapMatcherContractProbe.verify
-> FakeMapMatcher.bind(route)
-> MapMatchSession.match(sample)
   -> exact catalog lookup
   -> bounded call record
-> Matched.requireMatches
-> RouteProgressTracker.accept
-> canonical JSON report
```

Ownership:

| Componente | Stato posseduto | Durata |
| --- | --- | --- |
| `RoutePlan` | geometria, leg, manovre | sessione |
| `MapMatchSession` | route legata e futuro stato matcher | sessione |
| `FakeMapMatcher` | catalogo, counter e finestra chiamate | vita fake |
| `MapMatchResult` | un esito immutabile | una chiamata |
| `RouteProgressTracker` | ultimo progress accepted | sessione |
| CLI | contatori del report | una esecuzione |

## Benchmark diagnostico

```bash
sh tools/tdna bench map-matching 10000 7
```

La preparazione avviene fuori dal timer:

```text
RoutePlan
LocationSample list
catalog entries
FakeMapMatcher
MapMatchSession
RouteProgressTracker
```

Prima del timer un pass completo verifica che ogni campione produca `Matched` e
che ogni posizione venga accettata dal tracker.

La finestra misurata contiene:

```text
exact catalog lookup
bounded diagnostic record
Matched result cast
RouteProgressTracker.accept
```

Il benchmark usa:

- 10.000 campioni di default;
- tre warm-up;
- un numero dispari di run;
- minimo, mediana e massimo;
- nessuna soglia CI.

Non misura:

- ricerca di segmenti;
- indice spaziale;
- HMM o Viterbi;
- geometria reale;
- GPS;
- rete o database;
- MapLibre;
- Android/iOS;
- batteria;
- accuratezza su strada.

Il nome `map-matching-pipeline-v0` descrive il confine fake più il downstream
progress, non un algoritmo reale di matching.

## Confini Gradle

```text
map-matching-contracts
  -> plugin-sdk
  -> location-contracts
  -> navigation-contracts
  -> routing-contracts

map-matching-testkit
  -> map-matching-contracts
  -> location-contracts
  -> routing-contracts

fake-map-matcher
  -> map-matching-contracts
  -> map-matching-testkit nei test
```

Il testkit espone `RoutePlan` e `LocationSample` nella propria API, quindi queste
sono dipendenze dirette dichiarate, non transitive accidentali.

Il checker continua a vietare nei moduli common:

```text
android.location
CLLocation / CoreLocation
MapLibre
Valhalla
Ferrostar
Waze
Google Maps
Sygic
reference-routing Lab types
```

## Privacy e sicurezza

La fixture usa coordinate sintetiche. Il contratto non richiede invio in rete.

Un adapter reale deve evitare nei messaggi diagnostici:

- coordinate precise;
- cronologia dei campioni;
- identificativi utente;
- URL o token del provider;
- payload completi non redatti.

`Unmatched` e confidence non devono essere pubblicati automaticamente ad altri
utenti. Sono segnali locali del runtime di navigazione.

## Alternative considerate

### Un'unica funzione stateless con route e campione

Scartata nel hot path: ripete un modello grande e impedisce preparazione/caching
per sessione.

### Restituire `null` quando non esiste un match

Scartata: non distingue assenza di candidato, confidenza insufficiente e failure
del provider.

### Usare eccezioni per ogni unmatched

Scartata: `Unmatched` è un esito normale e frequente in presenza di rumore,
tunnel o route lontana.

### Convertire la cancellazione in `Failure`

Scartata: nasconde il lifecycle del caller, può causare retry errati e rende
inaffidabili metriche e cleanup.

### Testare determinismo con due chiamate identiche nella stessa sessione

Scartata: imporrebbe idempotenza a provider stateful e confonderebbe determinismo
di una nuova sessione con gestione dei duplicati nello stream.

### Implementare subito un matcher geometrico semplificato

Scartata: un algoritmo giocattolo rischierebbe di sembrare una base produttiva
senza gestire topologia, heading, accuratezza, transizioni e strade reali.

## Errori comuni

- chiamare il fake “map matcher reale”;
- trasformare `Unmatched` in `Failure`;
- trasformare una cancellazione in `Failure`;
- trasformare ogni unmatched in off-route;
- perdere sequence o monotonic time;
- usare un risultato della route precedente;
- ricostruire l'indice della route a ogni campione;
- attribuire il risultato al provider sbagliato;
- conservare tutta la cronologia delle chiamate;
- usare `removeAt(0)` in una finestra diagnostica calda;
- lasciare report diagnostici non bounded;
- interpretare confidence come probabilità universale;
- presentare il benchmark fake come accuratezza o prestazione stradale.

## Esercizi

1. Aggiungere un esito `InsufficientConfidence` senza modificare il tracker.
2. Creare un provider che non dichiara `Deterministic` e spiegare quale check
   viene saltato.
3. Aggiungere una failure `RateLimited` e discutere `retryable`.
4. Scrivere un adapter finto stateful che usa due campioni per migliorare la
   confidence.
5. Progettare un indice dei segmenti preparato in `bind`.
6. Definire una policy separata che accumula unmatched senza decidere subito
   off-route.
7. Confrontare il costo della `ArrayDeque` con una lista che rimuove l'indice 0.
8. Progettare un contract test per la cancellazione coroutine.
9. Aggiungere un secondo provider e una shadow comparison senza cambiare il
   `RouteProgressTracker`.
10. Elencare i dati che devono essere redatti prima di una diagnostica remota.

## Non-obiettivi

- map matching geometrico reale;
- ricerca di candidate roads;
- indice OSM;
- HMM, Viterbi o particle filter;
- filtro GPS;
- off-route e rerouting;
- adapter Android/iOS;
- rete;
- accuratezza o SLA di produzione.

## Documenti successivi

- [Scenario Lab map matching](lab/scenarios/map-matching-fake-provider.md)
- [Mappa del codice e degli stati](40-mappa-codice-e-stati.md)
- [Tracepoint Model v0](41-tracepoint-model-v0.md)
- [Scenario missed exit](lab/scenarios/navigation-missed-exit-reroute.md)
- [Registro milestone](50-registro-milestone.md)
