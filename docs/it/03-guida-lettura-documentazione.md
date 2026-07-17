# Guida alla lettura della documentazione

La documentazione ha pubblici diversi. Questo capitolo evita di leggere file a
caso o di confondere decisioni correnti con visioni future.

## Percorso 1: capire Travel DNA da zero

1. [Visione del prodotto](01-visione-prodotto.md)
2. [Glossario](02-glossario.md)
3. [Use case principali](11-use-case-principali.md)
4. [DDD e bounded context](10-ddd-bounded-context.md)
5. [Architettura generale](20-architettura-generale.md)
6. [Stato delle funzionalità](12-stato-funzionalita.md)
7. [Matrice tecnologie](52-matrice-tecnologie-decisioni.md)

Dopo questo percorso il lettore deve saper distinguere diario, guida, socialità,
navigazione interna, navigazione esterna e componenti tecnici.

## Percorso 2: contribuire al progetto

1. [Regole operative](00-regole-operative.md)
2. [Come contribuire](04-come-contribuire.md)
3. [Struttura del repository](21-struttura-repository.md)
4. [Documentazione della milestone corrente](50-registro-milestone.md)
5. Documento del componente interessato
6. [Strategia test](30-strategia-test.md)
7. [Stile dei commenti](../commenting-style.md)

Prima si comprende il contratto; poi si modifica; quindi si aggiornano prove e
documentazione.

## Percorso 3: imparare come funziona un navigatore

1. [Primo routing eseguibile in Java e Rust](43-reference-routing-java-rust.md)
2. [Scenario Lab eseguibile](lab/scenarios/reference-routing-java-rust.md)
3. [OpenStreetMap e cartografia](23-openstreetmap-e-cartografia.md)
4. [Routing e navigazione](24-routing-e-navigazione.md)
5. [GPS replay e fixture](31-gps-replay-e-fixture.md)
6. [Prestazioni](32-performance-budget.md)
7. [Tracepoint Model](41-tracepoint-model-v0.md)
8. [Scenario futuro missed exit e reroute](lab/scenarios/navigation-missed-exit-reroute.md)

Per iniziare dal codice invece che dalla teoria:

```bash
sh tools/tdna check-java
sh tools/tdna lab reference-routing astar
```

Ordine concettuale:

```text
dati geografici
-> grafo
-> route
-> campioni GPS
-> map matching
-> route progress
-> manovra
-> voce e UI
-> deviazione e ricalcolo
```

## Percorso 4: imparare Android e Kotlin

1. [Stack linguaggi e GUI](28-stack-linguaggi-e-gui.md)
2. [Architettura generale](20-architettura-generale.md)
3. [Plugin e provider](22-architettura-plugin-provider.md)
4. [Navigatori esterni e automotive](25-navigatori-esterni-e-automotive.md)
5. [Prestazioni](32-performance-budget.md)
6. [Debugging e strumenti](34-debugging-e-strumenti.md)

Con il codice futuro, aggiungere nell'ordine:

```text
shared contracts
-> Android composition root
-> MapHost
-> ActiveTrip presentation
-> foreground/background lifecycle
-> Android Auto surface
```

## Percorso 5: imparare iOS e Swift

1. [Stack linguaggi e GUI](28-stack-linguaggi-e-gui.md)
2. [Navigatori esterni e automotive](25-navigatori-esterni-e-automotive.md)
3. [Privacy e background](33-privacy-security-driving-safety.md)
4. [Prestazioni](32-performance-budget.md)
5. [Debugging e strumenti](34-debugging-e-strumenti.md)

Con il codice futuro:

```text
SwiftUI shell
-> UIKit MapLibre host
-> Core Location adapter
-> shared application models
-> ActivityKit/CarPlay surfaces
```

## Percorso 6: imparare Java

Travel DNA usa Java come linguaggio didattico e di interoperabilità, non come
duplicazione inutile della UI Android.

1. [Stack linguaggi e GUI](28-stack-linguaggi-e-gui.md)
2. [Primo routing eseguibile in Java e Rust](43-reference-routing-java-rust.md)
3. aprire `java/reference-routing/src/main/java`;
4. seguire parser, grafo, Dijkstra/A* e report con la
   [mappa del codice](40-mappa-codice-e-stati.md);
5. eseguire `sh tools/tdna check-java`;
6. modificare una copia della fixture e osservare i test;
7. leggere [DDD e bounded context](10-ddd-bounded-context.md) e
   [Plugin e provider](22-architettura-plugin-provider.md) prima dei futuri
   contratti di produzione.

## Percorso 7: imparare Rust

1. [Stack linguaggi e GUI](28-stack-linguaggi-e-gui.md)
2. [Primo routing eseguibile in Java e Rust](43-reference-routing-java-rust.md)
3. aprire `crates/tdna-reference-routing/src/lib.rs`;
4. eseguire `sh tools/tdna check-rust` con Rust stable installato;
5. eseguire `sh tools/tdna check-contract` per confrontare Java e Rust;
6. [Routing e navigazione](24-routing-e-navigazione.md);
7. [GPS replay](31-gps-replay-e-fixture.md);
8. [Prestazioni](32-performance-budget.md);
9. [Roadmap librerie open source](51-roadmap-librerie-open-source.md).

L'ordine didattico consigliato è:

```text
CLI e tipi puri
-> unit/property test
-> benchmark
-> fixture/replay
-> API stabile
-> binding mobile
```

Non iniziare dall'FFI: prima dimostrare il core in isolamento.

## Percorso 8: diario, fotografie e privacy

1. [Diario e Pagina del giorno](26-diario-media-pagina-giorno.md)
2. [Backend e local-first](29-backend-dati-sync.md)
3. [Privacy e sicurezza](33-privacy-security-driving-safety.md)
4. [Scenario diario](lab/scenarios/daily-page-photos-thoughts.md)

## Percorso 9: chat durante la guida

1. [Presenza, chat e DNA](27-presenza-chat-dna.md)
2. [Navigatori esterni e automotive](25-navigatori-esterni-e-automotive.md)
3. [Privacy, sicurezza e guida](33-privacy-security-driving-safety.md)
4. [Scenario chat con navigatore esterno](lab/scenarios/chat-with-external-navigation.md)

## Percorso 10: riaprire una decisione tecnologica

1. leggere l'ADR corrispondente in `docs/adr`;
2. leggere [Matrice tecnologie](52-matrice-tecnologie-decisioni.md);
3. leggere [Riferimenti tecnici](53-riferimenti-tecnici.md);
4. identificare il problema nuovo;
5. raccogliere benchmark o vincoli aggiornati;
6. aprire una Discussion;
7. aggiornare ADR e documenti solo dopo la decisione.

## Percorso 11: riprendere il progetto dopo una pausa

1. `AGENTS.md`;
2. [Regole operative](00-regole-operative.md);
3. [Stato documentazione](documentation-status.md);
4. [Registro milestone](50-registro-milestone.md);
5. issue madre e PR della milestone corrente;
6. documenti del task.

## Percorso 12: eseguire il primo Lab da zero

1. installare Java 21 e Python 3;
2. eseguire `sh tools/tdna doctor`;
3. leggere [il capitolo 43](43-reference-routing-java-rust.md);
4. aprire `fixtures/routes/reference-network-v0.tdna`;
5. eseguire `sh tools/tdna lab reference-routing dijkstra`;
6. eseguire `sh tools/tdna check-java`;
7. con Rust stable, eseguire `sh tools/tdna check`;
8. confrontare i due percorsi nel codice e rispondere agli esercizi del capitolo.

Questo percorso non richiede Android Studio, Xcode, OpenStreetMap o servizi di
rete.

## Percorso 13: futuro studio LoRa

Per ora leggere soltanto [Spike LoRa](54-spike-lora-roadmap.md). Il documento è
una lista di domande e non contiene ancora una decisione. La valutazione deve
separare LoRa fisico, LoRaWAN, mesh proprietarie, regolamentazione radio,
capacità dei telefoni e reale utilità per Travel DNA.
