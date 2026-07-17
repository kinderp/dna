# Stato delle funzionalità

Questo documento distingue visione, contratto, laboratorio e capacità di
prodotto. Lo stato vivo di CI e merge è in
[development-status.md](../project/development-status.md).

## Legenda

- `documented`: comportamento descritto, nessun codice;
- `executable lab`: codice, test/dati e comando eseguibili nel ramo che contiene il documento;
- `prototype planned`: previsto nella milestone tecnica;
- `future`: direzione approvata ma non pianificata ora;
- `open`: richiede discussione o spike;
- `non-goal`: escluso dal perimetro corrente.

## Stato corrente

| Funzionalità | Stato | Nota |
| --- | --- | --- |
| Visione e DDD | documented | Diario, guida, navigazione, socialità e bounded context. |
| Plugin SDK e routing contracts | executable lab | ID, capability, request, plan, legs, maneuvers e provenance. |
| Fake route planner/testkit | executable lab | Catalogo deterministico e conformance probe. |
| Dijkstra/A* Java e Rust | executable lab | Implementazioni indipendenti e report byte-identico. |
| `MapScene` e fake renderer | executable lab | Scena statica, delta, snapshot e conformance. |
| `LocationSample` e ordering gate | executable lab | Sequence e tempo monotono. |
| Clock virtuale e replay | executable lab | Rate razionale, stato bounded e fixture ground truth. |
| `MatchedRoutePosition` | executable lab | Output provider-neutral di un matcher futuro; non esegue matching. |
| Route progress tracker | executable lab | Leg, manovra, arrival e rifiuti deterministici. |
| Route-progress map binding | executable lab | Geometria verificata all'installazione e update `O(1)`. |
| Benchmark replay/progress | executable lab | JVM CI diagnostica, nessuna soglia o pretesa mobile. |
| Tooling foundation | executable lab | Documentazione, architettura, Java, Rust e KMP. |
| Gradle/KMP bootstrap | executable lab | JVM/Linux CI; wrapper locale ancora mancante. |
| Map matching reale | prototype planned | Candidati, scoring, confidenza e isteresi separati. |
| Off-route e rerouting | prototype planned | Scenario missed-exit deterministico. |
| MapLibre adapter | prototype planned | Dopo contratti e benchmark dispositivo. |
| Valhalla/Ferrostar adapter | prototype planned | Dietro porte Travel DNA. |
| Navigatore esterno e percorso ombra | prototype planned | Handoff e confidence esplicita. |
| Diario automatico e pagina del giorno | prototype planned | Event store, soste, foto e pensieri. |
| Chat reale/automotive | future | Core, outbox e driver policy prima dell'auto. |
| Road presence e Cartoline DNA | future | Backend, threat model, consenso e revoca. |
| Mappe/routing offline | future | Provider, licenza e distribuzione. |
| Contenuti Touring | open | Solo partnership/licenza. |
| LoRa/LoRaWAN | open | Spike dedicato. |
| Profili minori e verticali shopping/study | non-goal | Fuori MVP Travel DNA. |

## Cosa è dimostrato

```text
grafo -> Dijkstra/A* -> report Java/Rust
RouteRequest -> fake planner -> RoutePlan
RoutePlan -> MapScene -> fake renderer
Location fixture -> gate -> virtual clock -> replay summary
MatchedRoutePosition -> progress snapshot -> map binding/delta
```

Non sono dimostrati GPS o strade reali, map matching, distanza/ETA, off-route,
rerouting, MapLibre, adapter mobile, batteria o affidabilità su strada.

## Regola di comunicazione

Usare formulazioni precise: `documentato`, `Lab eseguibile`, `pianificato`,
`prototipo`, `sperimentale`, `disponibile`. Non dire che il prodotto supporta una
capacità soltanto perché esiste un Lab.
