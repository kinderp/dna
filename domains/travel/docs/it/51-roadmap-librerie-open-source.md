# Roadmap delle librerie open source Travel DNA

## Principio

Non riscrivere tutto. Essere in grado di sostituire tutto e possedere ciò che
crea vantaggio reale.

## Categorie

### Da possedere subito

- modelli canonici;
- plugin SDK;
- contract testkit;
- replay format e runner;
- journey/journal rules;
- privacy approximation contracts;
- DNA card sanitizer;
- performance methodology.

### Da contribuire upstream o incapsulare

- MapLibre;
- Valhalla;
- Ferrostar;
- KMP/mobile tooling;
- geocoding providers.

### Da sostituire progressivamente solo con prove

- navigation guidance runtime;
- route post-ranking;
- map matching selettivo;
- offline package tools;
- tile pipeline.

### Ultimo da riscrivere

- renderer vettoriale completo.

## Libreria 1: `tdna-geo-contracts`

Contenuto:

- coordinate;
- bbox;
- polyline/geometry handle;
- route;
- maneuver;
- lane guidance;
- units;
- provenance;
- error model.

Requisiti:

- provider-neutral;
- serializzazione separata;
- precisione documentata;
- property tests;
- Java/Kotlin/Rust mapping.

## Libreria 2: `tdna-plugin-sdk`

- descriptor;
- capability;
- maturity;
- lifecycle;
- health;
- selection context;
- license notices.

Non contiene business logic.

## Libreria 3: `tdna-plugin-testkit`

- abstract contract suites;
- fake clocks;
- fake providers;
- fixtures;
- cancellation tests;
- architecture checks.

Valore open source: altri progetti possono costruire provider compatibili.

## Libreria 4: `tdna-osm-normalizer`

- tag OSM -> categorie Travel DNA;
- provenance;
- data quality;
- tests con estratti piccoli/licenziati;
- extensible mapping;
- no user data.

Iniziare con categorie travel:

- campsite;
- caravan site;
- hotel;
- restaurant;
- service area;
- museum;
- viewpoint;
- beach;
- village/attraction.

## Libreria 5: `tdna-route-replay`

- fixture schema;
- virtual clock;
- event injection;
- engine adapter;
- report;
- compare;
- visualization data.

Prima CLI, poi integrazione mobile.

## Libreria 6: `tdna-journey-engine`

- stop candidates;
- visit proposals;
- timeline;
- media association scoring;
- DailyPage projection;
- user edit preservation.

Privacy-aware e local-first.

## Libreria 7: `tdna-guidance-core`

Futuro Rust:

- sample validation;
- route index;
- map matching route-biased;
- progress;
- off-route state;
- prompt scheduling model;
- snapshot.

Sequenza:

```text
CLI
-> replay tests
-> benchmark
-> desktop visualizer
-> UniFFI/other bindings
-> mobile shadow
-> limited rollout
```

## Libreria 8: `tdna-touristic-router`

Prima non è un router completo. È un ranker:

```text
candidate routes
-> features
-> tourist/family score
-> explanation
```

Solo in futuro può entrare nel path finding.

## Licenza

Da decidere per libreria. Candidati:

- Apache-2.0 per brevetti e uso ampio;
- MIT/BSD per semplicità;
- dual licence se esiste motivo commerciale.

La decisione non deve essere automatica. Considerare compatibilità con
dipendenze e contributi upstream.

## Governance

Ogni libreria pubblica necessita:

- README;
- scope/non-goals;
- versioning;
- changelog;
- contribution guide;
- code of conduct;
- security policy;
- CI;
- release process;
- API stability statement;
- examples;
- benchmark where relevant.

## Quando forkare

Fork solo se:

- upstream non accetta requisito essenziale;
- patch urgente e mantenibile;
- API/roadmap incompatibile;
- sicurezza;
- performance misurata.

Prima tentare contributo upstream.

## Shadow replacement

Ogni sostituzione documenta:

- baseline;
- equivalenza semantica;
- differenze;
- performance;
- rollout;
- fallback;
- removal plan.

## Rischio didattico

Non creare librerie artificiali solo per moltiplicare repository. Una libreria è
utile se ha confine, utenti potenziali e ciclo di release indipendente.
