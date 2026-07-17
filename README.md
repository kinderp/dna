# Travel DNA

Travel DNA è un progetto didattico e di prodotto per costruire una guida-diario
sociale dei viaggiatori, capace di funzionare sia con navigatori esterni sia con
una futura navigazione integrata.

Il progetto nasce da quattro idee unite:

1. il viaggio deve avere valore anche quando non si incontra nessun altro utente;
2. il diario deve raccogliere percorso, soste e luoghi lasciando all'utente il
   controllo su fotografie, pensieri e condivisione;
3. gli incontri lungo il viaggio devono diventare consigli, saluti,
   conversazioni e Cartoline DNA senza trasformare l'app in sorveglianza;
4. la codebase deve essere studiabile da studenti e nuovi contributori.

## Stato

La **Documentation Foundation v0** è presente e la milestone
**Foundations and Travel DNA Lab v0** è in corso.

Sono disponibili quattro vertical slice didattiche:

```text
1. grafo sintetico -> Java/Rust -> Dijkstra/A* -> report confrontato
2. RouteRequest -> RoutePlannerPort -> fake provider -> RoutePlan canonico
3. RoutePlan -> MapScene/MapSceneDelta -> FakeMapRenderer -> snapshot
4. fixture GPS sintetica -> LocationSample -> clock/replay -> report bounded
```

Non esiste ancora un navigatore mobile di produzione. Le slice stabiliscono
metodo, contratti, fixture, test, CI, prestazioni diagnostiche e documentazione
prima di introdurre GPS reale, OpenStreetMap, MapLibre, Valhalla, Ferrostar,
Android o iOS.

## Laboratorio 1 — algoritmo di routing

Prerequisiti:

- Java 21;
- Python 3;
- shell POSIX;
- Rust stable per il confronto completo.

```bash
sh tools/tdna check-java
sh tools/tdna lab reference-routing astar
```

Capitolo:
[Routing di riferimento in Java e Rust](docs/it/43-reference-routing-java-rust.md).

## Laboratorio 2 — contratto e provider

Prerequisiti aggiuntivi:

- Gradle compatibile; la CI usa Gradle 9.5.1;
- Kotlin 2.4.0 risolto dal version catalog.

```bash
sh tools/tdna check-architecture
sh tools/tdna lab routing-contracts
```

Capitolo:
[Contratti routing e fake provider](docs/it/44-contratti-routing-e-fake-provider.md).

## Laboratorio 3 — scena cartografica

```bash
sh tools/tdna lab map-scene
```

Il Lab installa una route e marker semantici in una `MapScene`, applica delta
bounded e osserva un fake renderer senza MapLibre o GPU.

Capitolo:
[MapScene e fake renderer](docs/it/45-map-scene-e-fake-renderer.md).

## Laboratorio 4 — posizione e replay

```bash
sh tools/tdna lab location-replay
sh tools/tdna bench location-replay 10000 7
```

Il Lab riproduce campioni sintetici con tempo monotono, rifiuta sequence e
timestamp non crescenti e produce un summary deterministico. Il benchmark è
soltanto diagnostico e non misura GPS, batteria o prestazioni mobili.

Capitolo:
[LocationSample e replay deterministico](docs/it/46-location-sample-e-replay-deterministico.md).

## Verifica completa

```bash
sh tools/tdna doctor
sh tools/tdna check
```

Il comando controlla:

- link e code fence della documentazione;
- confini architetturali Kotlin;
- Java e Rust;
- contratto cross-language;
- Kotlin Multiplatform JVM/Linux;
- fake route planner e fake map renderer;
- Lab location replay;
- benchmark diagnostico senza threshold.

## Da dove iniziare

- [Indice della documentazione](docs/README.md)
- [Guida ai percorsi di lettura](docs/it/03-guida-lettura-documentazione.md)
- [Regole operative](docs/it/00-regole-operative.md)
- [Review e merge](docs/it/06-review-e-merge.md)
- [Stato dello sviluppo](docs/project/development-status.md)
- [Report giornalieri](docs/project/daily/README.md)
- [Architettura](docs/it/20-architettura-generale.md)
- [Tecnologie](docs/it/52-matrice-tecnologie-decisioni.md)
- [Navigazione](docs/it/24-routing-e-navigazione.md)
- [Travel DNA Lab](docs/it/lab/README.md)
- [ADR](docs/adr/README.md)

## Principi

```text
Travel DNA dipende dai propri contratti, non dai provider.

Il navigatore è una capacità del prodotto, non l'intero prodotto.

La chat rimane attiva durante il viaggio, ma la superficie cambia in base al
contesto di guida.

Il diario è privato per impostazione predefinita; la condivisione è esplicita.

Ogni astrazione e ogni linguaggio devono pagare il proprio costo.

Una funzione corretta ma lenta, insicura o non documentata non è conclusa.
```

## Linguaggio della documentazione

- codice, API, commenti, commit e pull request: inglese;
- documentazione didattica in `docs/it`: italiano;
- nomi del dominio: inglese nel codice, con glossario italiano;
- riferimenti esterni: documentazione ufficiale quando disponibile.

## Licenza

Il modello di licenza non è ancora stato deciso. Prima di accettare contributi
esterni di codice deve essere chiuso
[ADR-0009](docs/adr/0009-project-licensing-model.md) e aggiunto `LICENSE`.
