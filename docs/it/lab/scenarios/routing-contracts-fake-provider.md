# Scenario: routing contracts and deterministic fake provider

id: `lab.routing.provider-neutral-contracts.v0`

status: `executable`

scenario kind: `routing-contract`

## Learning goal

Capire come l'applicazione richiede una route senza conoscere il provider e come
un fake deterministico permette di verificare contratto, capability, provenance,
errori e invarianti prima di integrare un motore reale.

## Prerequisites

- [Architettura plugin e provider](../../22-architettura-plugin-provider.md)
- [Routing e navigazione](../../24-routing-e-navigazione.md)
- [Routing Java/Rust](../../43-reference-routing-java-rust.md)
- [Contratti neutrali e fake provider](../../44-contratti-routing-e-fake-provider.md)

## User story

Un use case Travel DNA costruisce un `RouteRequest` da `A` a `E`, riceve un
`RoutePlan` canonico e può usarlo senza importare classi MapLibre, Valhalla,
Ferrostar o del laboratorio Java/Rust.

## Platforms

- Kotlin common code;
- JVM;
- Linux x64 Kotlin/Native;
- GitHub Actions Linux;
- CLI Lab JVM.

## Fixture

La fixture vive nel codice di test:

```text
FakeRouteFixtures.ReferenceRequest
FakeRouteFixtures.ReferenceRoute
FakeRouteFixtures.UnroutableRequest
```

Usa coordinate sintetiche e il percorso concettuale:

```text
A -> C -> D -> E
3600 metri
240 secondi
```

Non importa tipi dal reference router Java/Rust.

## Trigger

Verifica completa della slice:

```bash
sh tools/tdna check-architecture
sh tools/tdna check-kotlin
```

Esecuzione didattica:

```bash
sh tools/tdna lab routing-contracts
```

## Expected evidence

- Gradle configura i moduli KMP;
- `plugin-sdk` compila per JVM e Linux x64;
- `routing-contracts` compila senza provider;
- il fake restituisce una route canonica;
- la route contiene quattro punti e quattro manovre;
- provenance e descriptor usano lo stesso `PluginId`;
- una richiesta sconosciuta produce `NoRoute` non retryable;
- il fake registra le richieste;
- il contract probe passa;
- l'architecture checker non trova import vendor o Lab;
- il report CLI è deterministico.

## Expected domain events

Il Lab non introduce eventi di produzione. Le evidenze logiche sono:

```text
PLUGIN_DESCRIPTOR_VALIDATED
ROUTE_REQUEST_CREATED
ROUTE_PLANNER_SELECTED
ROUTE_PLAN_RETURNED
ROUTE_PROVENANCE_VALIDATED
ROUTE_PROVIDER_CONTRACT_PASSED
```

## Logical tracepoints

```text
PLUGIN_DESCRIPTOR_VALIDATED
ROUTE_REQUEST_VALIDATED
FAKE_ROUTE_CATALOG_MATCHED
ROUTE_PLAN_VALIDATED
ROUTE_PROVIDER_CONTRACT_PASSED
ROUTING_LAB_REPORT_EMITTED
```

Stato: `stable-doc`. Non generano tracing runtime.

## Module path

```text
labs/routing-contracts-cli MainKt
-> FakeRouteFixtures.planner()
-> FakeRoutePlanner.plan()
-> RoutePlanningResult.Success
-> RoutePlan invariants
-> deterministic summary
```

Percorso del contract test:

```text
FakeRoutePlannerTest
-> RoutePlannerContractProbe.verify()
-> descriptor capability
-> routable request
-> deterministic repeat
-> unroutable request
-> canonical NoRoute
```

## Dependency path

```text
routing-contracts -> plugin-sdk
routing-testkit   -> routing-contracts
fake planner      -> routing-contracts
Lab CLI           -> fake planner
```

Relazioni vietate:

```text
routing-contracts -X-> fake planner
routing-contracts -X-> Valhalla
routing-contracts -X-> MapLibre
routing-contracts -X-> Java/Rust reference Lab
```

## State changes

| Stato | Proprietario | Durata |
| --- | --- | --- |
| route catalog | fake provider | vita del fake |
| recorded requests | fake provider | vita del fake/test |
| request | caller | una chiamata |
| planning result | caller | fino alla proiezione/uso |
| route plan | caller/cache futuro | definito dal use case |
| descriptor | plugin | vita del plugin |

Il fake è single-threaded. La lista delle richieste non è un log di produzione.

## Expected output

```json
{"provider":"org.traveldna.fake-route-planner","capabilities":["routing.alternatives","routing.deterministic","routing.maneuvers","routing.offline","routing.plan"],"result":"success","route_id":"reference-route-v0","geometry_points":4,"maneuvers":4,"distance_m":3600,"duration_s":240}
```

L'ordine delle capability è lessicografico per rendere stabile il report.

## Contract properties

### Request

- coordinate valide;
- origine diversa dalla tappa successiva;
- massimo tre alternative;
- profilo esplicito.

### Plan

- almeno due punti;
- almeno una leg;
- legs contigue;
- endpoint delle legs uguali alla geometry;
- manovre riferite a indici validi;
- somme distance/duration checked;
- provenance presente.

### Provider

- dichiara `routing.plan`;
- non restituisce più alternative del richiesto;
- provenance coerente col descriptor;
- risultati ripetibili se dichiara `routing.deterministic`;
- errore `NoRoute` non retryable per il miss della fixture.

## Performance properties

- nessun I/O;
- nessuna rete;
- nessun parsing vendor;
- matching esatto su strutture immutabili;
- nessuna pretesa di benchmark di produzione;
- contract validation fuori dal loop GPS.

## Privacy and safety properties

- dati sintetici;
- nessuna posizione reale;
- nessun utente;
- nessun uso durante la guida;
- nessuna persistenza o trasmissione;
- nessun contenuto di chat.

## Existing tests

- `PluginSdkTest`;
- `RouteContractsTest`;
- `FakeRoutePlannerTest`;
- `RoutePlannerContractProbe`;
- `tools/check_architecture.py`;
- Gradle JVM tests;
- Gradle Linux x64 tests;
- Foundation CI.

## Missing tests

- cancellazione di un provider sospendente;
- alternative multiple;
- route con più legs;
- overflow intenzionale su totali molto grandi;
- adapter da response vendor;
- compatibilità binaria/versioning;
- target iOS.

## Future tests

- provider Valhalla su piccolo grafo controllato;
- provider Rust tramite FFI;
- fallback fra provider;
- selection policy basata su capability;
- serialization schema separato;
- benchmark mapping provider -> canonical route.

## Common failures

- importare un tipo del fake nel contratto;
- usare `RoutePlan` come response JSON senza schema;
- marcare `NoRoute` retryable;
- restituire route con provider provenance errata;
- dichiarare `routing.deterministic` senza risultato stabile;
- catturare cancellazione come errore interno;
- aggiungere campi vendor al modello comune;
- far calcolare il percorso al fake;
- ritenere il Lab equivalente a Valhalla.

## Non-goals

- routing reale;
- traffico;
- OpenStreetMap;
- map rendering;
- GPS;
- guidance;
- rerouting;
- Android/iOS UI;
- API pubblica congelata;
- performance production-grade.

## Exercises

1. Aggiungere una seconda route alternativa e verificare il limite richiesto.
2. Creare un descriptor senza `routing.plan` e osservare il rifiuto.
3. Creare una leg con endpoint incoerente e leggere il messaggio dell'invariante.
4. Scrivere un secondo fake con provider ID diverso e riusare il probe.
5. Introdurre intenzionalmente un import `valhalla.*` nei contracts e verificare
   l'architecture check.
6. Progettare un adapter `ReferenceRouteAdapter` senza importarlo nel contratto.

## Related docs

- [Capitolo didattico](../../44-contratti-routing-e-fake-provider.md)
- [Struttura repository](../../21-struttura-repository.md)
- [Strategia test](../../30-strategia-test.md)
- [Mappa codice e stati](../../40-mappa-codice-e-stati.md)
- [Travel DNA Lab](../../42-traveldna-lab-roadmap.md)
- [Registro milestone](../../50-registro-milestone.md)
