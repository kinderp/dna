# Off-route, missed exit e coordinamento del reroute

## Stato del capitolo

`implementation-backed — Navigation Runtime Replay v0`

Questo capitolo documenta la prima state machine eseguibile che interpreta
evidenza normalizzata di deviazione e coordina un ricalcolo senza sostituire la
route corrente prima che la nuova route sia completamente validata.

```text
LocationSample / map-match evidence
-> OffRouteObservation
-> OffRouteTracker
-> Suspected / Confirmed
-> RerouteCommand
-> RoutePlannerPort
-> correlated RerouteOutcome
-> validated RoutePlan replacement
```

Il codice si trova in:

```text
shared/off-route-contracts/
shared/off-route-state-machine/
shared/reroute-coordinator/
labs/missed-exit-cli/
fixtures/navigation/
```

La slice usa dati sintetici e soglie didattiche. Non stabilisce soglie di
produzione, non legge un grafo stradale, non esegue map matching reale e non
misura affidabilità su strada.

## Cosa imparerai

Al termine dovresti saper spiegare:

1. perché evidenza off-route e decisione off-route sono responsabilità diverse;
2. perché un singolo campione sospetto non deve provocare un reroute;
3. perché la conferma richiede sia un numero di osservazioni sia una durata;
4. che cosa significa `Indeterminate` e perché non equivale a `Suspicious`;
5. come si recupera da un falso allarme;
6. perché lo stato `Confirmed` resta sticky fino a una decisione del runtime;
7. a cosa serve un `OffRouteEpisodeId`;
8. perché un reroute usa un `RerouteAttemptId` separato;
9. come viene costruita la `RouteRequest` canonica;
10. perché la vecchia route resta attiva mentre il provider lavora;
11. come si impediscono richieste parallele;
12. come si ignorano outcome tardivi o appartenenti a un’altra route;
13. perché la cancellazione coroutine deve propagare dopo il cleanup;
14. come un’eccezione ordinaria del provider diventa un errore canonico;
15. quali postcondizioni deve superare la route sostitutiva;
16. perché la nuova route deve avere un nuovo `RouteId`;
17. quali componenti devono essere ricreati dopo la sostituzione;
18. che cosa misura il benchmark e che cosa lascia fuori.

## Prerequisiti

- [Contratti routing e fake provider](44-contratti-routing-e-fake-provider.md)
- [LocationSample e replay](46-location-sample-e-replay-deterministico.md)
- [Route progress](47-posizione-matched-e-route-progress.md)
- [Porta di map matching](48-porta-map-matching-e-fake-deterministico.md)
- [Strategia test](30-strategia-test.md)
- [Performance budget](32-performance-budget.md)

## Il problema: una deviazione non è un booleano

Un navigatore reale non riceve normalmente un segnale perfetto:

```text
offRoute = true
```

Riceve osservazioni rumorose:

- nessun candidato di matching;
- confidence bassa;
- distanza laterale elevata;
- heading incoerente;
- manovra attesa non eseguita;
- GPS temporaneamente assente;
- tunnel;
- strada parallela;
- route geometry imprecisa;
- provider non disponibile.

Questi fatti non hanno tutti lo stesso significato. La slice introduce un
modello normalizzato che il runtime può ricevere da componenti diversi senza
conoscere i dettagli del provider.

## Evidenza normalizzata

```kotlin
sealed interface OffRouteEvidence {
    data object OnRoute
    data class Suspicious(val reason: OffRouteEvidenceReason)
    data class Indeterminate(val diagnosticCode: String?)
}
```

### `OnRoute`

Esiste evidenza sufficiente per considerare il campione coerente con la route
corrente.

### `Suspicious`

Esiste evidenza concreta di possibile deviazione. I motivi v0 sono:

```text
Unmatched
LowConfidence
LateralDeviation
MissedExpectedManeuver
```

Il motivo è descrittivo; la state machine v0 non applica soglie diverse per
ciascun motivo.

### `Indeterminate`

Il runtime non possiede evidenza sufficiente né per confermare coerenza né per
aggiungere un nuovo sospetto.

Esempi:

- provider temporaneamente non disponibile;
- campione assente;
- tunnel;
- accuratezza troppo bassa per decidere;
- risultato del matcher non utilizzabile.

`Indeterminate` non incrementa il contatore sospetto e non recupera la state
machine. Mantiene l’episodio corrente e aggiorna soltanto la baseline temporale
dello stream accettato.

## Separare normalizzazione e conferma

La pipeline target è:

```text
LocationSample
-> filter / matcher / maneuver context
-> evidence normalization
-> OffRouteTracker
```

`OffRouteTracker` non calcola distanza e non legge confidence direttamente. Il
vantaggio è che possiamo:

- testare la conferma con dati sintetici;
- cambiare matcher senza cambiare la state machine;
- confrontare policy di normalizzazione;
- spiegare esattamente quale livello ha preso una decisione;
- evitare soglie provider-specifiche nel dominio centrale.

## `OffRouteObservation`

Ogni osservazione conserva:

```text
routeId
LocationSample
OffRouteEvidence
```

`LocationSample` porta con sé:

```text
sequence
monotonicTime
position
accuracy
speed/bearing opzionali
origin
```

Il tracker può quindi verificare separatamente:

- identità della route;
- ordine dello stream;
- ordine temporale;
- semantica dell’evidenza.

## Policy bounded

```kotlin
OffRoutePolicy(
    requiredConsecutiveSuspicious = 3,
    minimumSuspiciousDurationMillis = 2_000,
)
```

Bounds v0:

```text
requiredConsecutiveSuspicious: 2 .. 20
minimumSuspiciousDurationMillis: 1 .. 120.000
```

La conferma richiede entrambe le condizioni:

```text
suspiciousCount >= requiredConsecutiveSuspicious
AND
elapsed >= minimumSuspiciousDurationMillis
```

Questo protegge da due errori opposti.

### Solo count

Tre campioni in pochi millisecondi possono essere lo stesso errore ripetuto ad
alta frequenza.

### Solo durata

Un solo campione vecchio non deve diventare conferma soltanto perché è trascorso
del tempo.

La combinazione count+durata non rende la policy “corretta per la produzione”:
crea una state machine esplicita, testabile e configurabile.

## State machine

```text
OnRoute
  | Suspicious
  v
Suspected
  | OnRoute                 -> OnRoute / Recovered
  | Indeterminate           -> Suspected / HeldIndeterminate
  | Suspicious insufficient -> Suspected / SuspicionContinued
  | Suspicious count+time   -> Confirmed / ConfirmedNow
  v
Confirmed
```

Dopo `Confirmed`, nuove osservazioni producono `StayedConfirmed`.

La conferma è sticky perché il tracker non sa se:

- il reroute è partito;
- il provider è ancora in esecuzione;
- l’utente ha annullato;
- la route è stata sostituita;
- il runtime ha deciso di ignorare l’episodio.

Il reset appartiene al runtime che installa una nuova route o avvia una nuova
sessione.

## Falso allarme

Scenario:

```text
OnRoute
-> Suspicious
-> OnRoute
```

Risultato:

```text
SuspicionStarted
-> Recovered
-> state OnRoute
```

Il falso allarme non crea un comando di reroute.

## `Indeterminate` durante un sospetto

Scenario:

```text
Suspicious count 1
Suspicious count 2
Indeterminate
Suspicious count 3 dopo durata minima
```

L’indeterminate:

- non incrementa il count;
- non azzera l’episodio;
- non diventa off-route;
- consente alla durata monotona di continuare;
- aggiorna la baseline sequence/time accettata.

La documentazione usa “consecutive suspicious” nel senso di sequenza di evidenze
sospette non interrotta da un’esplicita evidenza `OnRoute`; un indeterminate è un
gap, non una prova contraria.

## Identità dell’episodio

Ogni nuovo sospetto riceve:

```kotlin
OffRouteEpisodeId
```

L’ID distingue:

```text
falso allarme #1
episodio confermato #2
```

Serve a correlare la conferma con un futuro comando o con una visualizzazione
didattica. È monotono nella vita del tracker e viene azzerato soltanto da un
reset esplicito della sessione.

## Validazione degli stati pubblici

`Suspected` e `Confirmed` sono modelli pubblici e possono essere costruiti anche
da test o adapter. I costruttori impediscono combinazioni impossibili.

`Suspected` richiede:

- prima e ultima osservazione entrambe `Suspicious`;
- stessa route;
- count bounded;
- count 1 implica stessa osservazione iniziale/finale;
- count > 1 implica sequence e tempo crescenti.

`Confirmed` richiede:

- prima e conferma entrambe `Suspicious`;
- stessa route;
- sequence e tempo crescenti;
- count almeno 2;
- `suspiciousDurationMillis` uguale all’elapsed monotono reale.

In questo modo un `Confirmed` non può mentire sul proprio intervallo temporale.

## Rifiuti non mutanti

Il tracker rifiuta:

```text
WrongRoute
NonIncreasingSequence
NonIncreasingMonotonicTime
```

La precedenza è deterministica:

```text
route -> sequence -> time
```

Un rifiuto non modifica:

- state;
- episode counter;
- last accepted observation.

`inspect` costruisce la decisione senza commit; `accept` applica soltanto una
decisione accepted.

## Dalla conferma al reroute

La conferma non chiama direttamente un provider. Produce input per:

```text
RerouteCoordinator.begin(confirmed)
```

Il coordinator crea:

```kotlin
RerouteCommand(
    attemptId,
    episodeId,
    sourceRouteId,
    RouteRequest(
        origin = latest suspicious sample position,
        destination = active route destination,
        profile = configured profile,
        requestedAlternatives = 1,
    ),
)
```

La `RouteRequest` è canonica e provider-neutral.

## Perché l’origine usa la posizione locale

Quando l’utente ha lasciato la route, la vecchia route coordinate non descrive
più necessariamente il punto reale. La v0 usa la posizione del campione che ha
confermato l’episodio come nuova origine.

In futuro una policy potrà scegliere:

- raw filtered position;
- matched position su una strada alternativa;
- posizione stimata in tunnel;
- punto sicuro di riaggancio.

Questa scelta resterà esplicita e separata dal provider.

## Un solo tentativo in flight

Stati del coordinator:

```text
Ready(activeRoute, optional lastFailure)
InFlight(activeRoute, command)
```

Mentre lo stato è `InFlight`:

```text
begin(second confirmation)
-> Ignored(AlreadyInFlight)
```

Non vengono create richieste parallele né nuovi attempt ID.

## Identità del tentativo

Ogni comando usa:

```kotlin
RerouteAttemptId
```

L’outcome conserva:

```text
attemptId
sourceRouteId
```

Prima di applicarlo il coordinator verifica entrambi.

Questo impedisce che:

- un risultato vecchio completi un retry più recente;
- un risultato della route precedente sostituisca quella corrente;
- una callback tardiva riapra una sessione conclusa.

## Outcome stantio

Se coordinator e outcome non coincidono:

```text
state non InFlight
OR attemptId diverso
OR sourceRouteId diverso
```

il risultato è:

```text
RerouteApplyDecision.Stale
```

La route e lo stato in-flight valido non cambiano.

## Vecchia route autorevole

Durante il reroute:

```text
activeRoute == oldRoute
```

La vecchia route resta disponibile per:

- mappa;
- indicazioni degradate;
- diario;
- fallback;
- diagnostica;
- eventuale recupero.

Non viene sostituita quando il provider inizia, ma soltanto dopo un outcome
correlato e una validazione completa.

## Executor separato

```text
RerouteCoordinator
  stato e decisioni pure/sincrone

RerouteExecutor
  chiamata sospendibile a RoutePlannerPort
```

`OffRouteTracker.accept` e `RerouteCoordinator.begin/apply` non fanno rete e non
bloccano.

L’executor traduce:

```text
RoutePlanningResult.Success -> RerouteOutcome.Planned
RoutePlanningResult.Failure -> RerouteOutcome.Failed
ordinary Exception          -> bounded Internal failure
CancellationException       -> propaga
```

Cattura `Exception`, non l’intero `Throwable`: errori fatali del runtime non
vengono mascherati da un normale failure provider.

## Cancellazione

Se `executePending` viene cancellato:

```text
cancelAttempt(attemptId)
-> state Ready(oldRoute)
-> pending command cleared
-> CancellationException rethrown
```

La cancellazione non diventa:

```text
RoutePlanningError(Internal)
```

Il lifecycle del caller resta osservabile e lo stato non rimane bloccato in
`InFlight`.

## Eccezione ordinaria

Se il provider lancia una normale eccezione inattesa:

```text
RerouteOutcome.Failed(
    code = Internal,
    retryable = true,
    diagnostic = reroute.executor-exception,
)
```

`apply` torna a `Ready(oldRoute, lastFailure)` e permette una decisione esplicita
di retry successivo.

La slice non implementa backoff automatico.

## Provenance del planner

Una route di successo deve dichiarare:

```text
replacement.provenance.providerId == planner.descriptor.id
```

Un provider che restituisce una route attribuita a un altro provider produce un
failure canonico e non raggiunge la sostituzione.

## Postcondizioni della route sostitutiva

Prima del commit:

1. la route deve avere un `RouteId` diverso dalla route corrente;
2. `RoutePlan.requireMatches(command.request)` deve passare;
3. origine e destinazione devono coincidere con la richiesta;
4. deve esistere una leg per ogni segmento richiesto;
5. geometria, manovre, distanza e durata devono rispettare i contratti canonici;
6. l’outcome deve essere correlato al tentativo e alla source route.

Se una proprietà fallisce:

```text
InvalidReplacement
-> old route retained
-> state Ready(oldRoute, error)
```

## Sostituzione atomica

Soltanto dopo tutti i controlli:

```text
state = Ready(newRoute)
```

La decisione restituisce:

```text
previousRoute
activeRoute
attemptId
```

Il caller può quindi ricreare, come un’unica fase di installazione:

- `MapMatchSession` legata alla nuova route;
- `RouteProgressTracker` vuoto;
- `RouteProgressMapBinding` per il nuovo overlay;
- scena o route overlay;
- prompt/manovra state;
- trace context.

Il Lab dimostra almeno la creazione di un nuovo tracker con snapshot iniziale
vuoto.

## Lab eseguibile

```bash
sh tools/tdna lab missed-exit
```

Input sintetico:

```text
0 OnRoute
1 Suspicious         -> episodio 1
2 OnRoute            -> recovery
3 Suspicious         -> episodio 2
4 Suspicious
5 Indeterminate      -> hold
6 Suspicious         -> confirmation count 3 / duration 2500 ms
```

Poi:

```text
begin attempt 1
second begin ignored
stale attempt 2 outcome ignored
fake planner returns canonical replacement
old route held until apply
replacement committed
new progress tracker empty
```

Output canonico:

```json
{"scenario":"reference-missed-exit-v0","episodes":2,"recoveries":1,"indeterminate":1,"confirmations":1,"attempts":1,"duplicate_begin":"AlreadyInFlight","stale_outcome_ignored":true,"old_route_held":true,"replaced":true,"new_route_id":"reference-rerouted-route-v0","new_tracker_empty":true}
```

## Percorso del codice

```text
Main.runLab
-> referenceObservations
-> OffRouteTracker.accept
   -> rejection
   -> nextState
   -> continueSuspicion
-> RerouteCoordinator.begin
-> stale outcome apply
-> FakeRoutePlanner.plan
-> RerouteExecutor.execute
-> RerouteCoordinator.apply
   -> validate new ID
   -> RoutePlan.requireMatches
   -> atomic replacement
-> new RouteProgressTracker
-> report JSON
```

## Ownership

| Componente | Stato | Durata |
| --- | --- | --- |
| evidence normalizer futuro | soglie/provider-specific context | per campione/sessione |
| `OffRouteTracker` | state, accepted baseline, episode counter | route session |
| `RerouteCoordinator` | active route, pending command, attempt counter | navigation session |
| `RerouteExecutor` | nessun durable state | una chiamata |
| `RoutePlannerPort` | provider-specific | adapter/provider |
| replacement installers | matcher/progress/map/voice state | nuova route |

Nessuna lista di osservazioni cresce con il viaggio.

## Benchmark diagnostico

```bash
sh tools/tdna bench off-route 10000 7
```

Il benchmark usa una sequenza ripetuta:

```text
OnRoute
Suspicious
Suspicious
OnRoute
...
```

La policy richiede tre sospetti e 2 secondi, quindi il benchmark produce falsi
allarmi e recovery senza conferma né I/O provider.

L’ultimo campione viene forzato `OnRoute` per ogni `sampleCount` ammesso, così lo
stato finale atteso è deterministico anche per conteggi piccoli.

Fuori dal timer:

- costruzione delle osservazioni;
- costruzione del tracker;
- reset;
- pass completo con assert per campione;
- verifica finale.

Dentro il timer:

- validazione route/sequence/time;
- transizioni `OnRoute`/`Suspected`;
- conteggio/durata;
- commit della baseline.

Non misura:

- evidence normalization;
- map matching;
- routing provider;
- rete;
- route replacement;
- GPS;
- MapLibre;
- Android/iOS;
- batteria;
- correttezza delle soglie su strada.

## Test

La slice verifica:

- bounds policy, ID e diagnostics;
- stati pubblici incoerenti rifiutati;
- falso allarme e recovery;
- count+durata;
- indeterminate hold;
- stato confirmed sticky;
- route/sequence/time rejection senza mutazione;
- inspect non mutante e reset;
- un solo attempt in flight;
- stale outcome;
- failure e retry con attempt successivo;
- cancellazione con cleanup;
- ordinary exception con cleanup;
- provenance del provider;
- route ID nuovo;
- postcondizioni request/route;
- sostituzione atomica;
- Lab ground truth;
- benchmark bounds e conteggi piccoli;
- common/JVM/Linux;
- architecture boundary.

## Privacy e sicurezza

La fixture usa coordinate sintetiche e non contiene viaggi reali.

Il runtime futuro deve evitare di inviare in diagnostica:

- coordinate precise;
- sequenze complete di evidenza;
- identificatori utente;
- route geometry completa;
- token o response provider;
- cause exception non redatte.

Off-route e reroute influenzano una funzione usata durante la guida. Le soglie
v0 sono didattiche e non devono diventare valori di produzione senza field test,
benchmark mobile, analisi sicurezza e degraded mode.

## Alternative considerate

### Reroute al primo unmatched

Scartato: tunnel, GPS rumoroso e provider failure produrrebbero ricalcoli
continui.

### Un solo threshold temporale

Scartato: una singola evidenza vecchia potrebbe confermare senza ripetizione.

### Un solo threshold numerico

Scartato: burst molto rapido di errori potrebbe confermare troppo presto.

### Trattare `Indeterminate` come suspicious

Scartato: indisponibilità del provider non è evidenza fisica di deviazione.

### Trattare `Indeterminate` come recovery

Scartato: assenza di evidenza non prova il ritorno sulla route.

### Chiamare il planner nel tracker

Scartato: introdurrebbe sospensione/I/O nella state machine e impedirebbe test
puri.

### Sostituire la route all’avvio del provider

Scartato: non esiste ancora una nuova route valida e si perderebbe il fallback.

### Accettare outcome soltanto per attempt ID

Scartato: serve anche source route ID per impedire cross-route replacement.

### Riutilizzare lo stesso RouteId

Scartato: cache, matcher, overlay e callback non distinguerebbero due snapshot
diversi.

## Errori comuni

- chiamare `Suspicious` una conferma;
- contare `Indeterminate` come sospetto;
- azzerare l’episodio durante un gap senza policy esplicita;
- modificare lo stato su un sample rifiutato;
- non correlare outcome e source route;
- avviare più planner in parallelo;
- cancellare senza pulire `InFlight`;
- catturare `Throwable` e mascherare errori fatali;
- accettare provenance di un provider diverso;
- applicare una route prima di `requireMatches`;
- riutilizzare route ID;
- riutilizzare tracker/matcher della route precedente;
- presentare le soglie v0 come sicure per la strada;
- includere planner/rete nel benchmark del tracker.

## Esercizi

1. Aggiungere una policy che resetta il sospetto dopo troppi indeterminate.
2. Definire una finestra massima dell’episodio oltre alla durata minima.
3. Aggiungere un motivo `HeadingDivergence` senza cambiare il tracker.
4. Implementare un backoff esterno al coordinator.
5. Progettare una cancellazione esplicita quando il matcher torna affidabile
   prima del risultato del planner.
6. Aggiungere una seconda alternativa e una policy di ranking separata.
7. Progettare l’installazione atomica di matcher, progress e map binding.
8. Aggiungere property test per sequenze di evidenza generate.
9. Confrontare state machine sticky e auto-recovery dopo `Confirmed`.
10. Definire quali metriche field servono prima di scegliere soglie reali.

## Non-obiettivi

- soglie di produzione;
- filtro GPS;
- map matching reale;
- traffic-aware rerouting;
- retry/backoff automatico;
- route ranking;
- Android/iOS lifecycle;
- MapLibre;
- voice guidance;
- affidabilità o SLA su strada.

## Documenti successivi

- [Scenario Lab missed exit](lab/scenarios/navigation-missed-exit-reroute.md)
- [Mappa codice e stati](40-mappa-codice-e-stati.md)
- [Tracepoint Model](41-tracepoint-model-v0.md)
- [Navigatori esterni e automotive](25-navigatori-esterni-e-automotive.md)
- [Registro milestone](50-registro-milestone.md)
