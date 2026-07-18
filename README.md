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

La sequenza didattica implementation-backed comprende sette slice:

```text
1. grafo sintetico -> Java/Rust -> Dijkstra/A* -> report confrontato
2. RouteRequest -> RoutePlannerPort -> fake provider -> RoutePlan canonico
3. RoutePlan -> MapScene/MapSceneDelta -> FakeMapRenderer -> snapshot
4. fixture GPS sintetica -> LocationSample -> clock/replay -> report bounded
5. MatchedRoutePosition -> route progress -> manovra/arrival -> delta mappa
6. LocationSample -> fake MapMatcherPort -> Matched/Unmatched/Failure -> progress
7. evidence off-route -> conferma -> reroute correlato -> route replacement
```

Questi sono laboratori e contratti di fondazione, non un navigatore mobile di
produzione. Lo stato vivo di pubblicazione, CI e review è registrato in
[development-status.md](docs/project/development-status.md) e nelle pull request.

## Laboratori

### 1 — algoritmo di routing

```bash
sh tools/tdna check-java
sh tools/tdna lab reference-routing astar
```

[Capitolo 43 — Routing Java/Rust](docs/it/43-reference-routing-java-rust.md)

### 2 — contratto e provider di routing

```bash
sh tools/tdna check-architecture
sh tools/tdna lab routing-contracts
```

[Capitolo 44 — Contratti routing e fake provider](docs/it/44-contratti-routing-e-fake-provider.md)

### 3 — scena cartografica

```bash
sh tools/tdna lab map-scene
```

[Capitolo 45 — MapScene e fake renderer](docs/it/45-map-scene-e-fake-renderer.md)

### 4 — posizione e replay

```bash
sh tools/tdna lab location-replay
sh tools/tdna bench location-replay 10000 7
```

[Capitolo 46 — LocationSample e replay](docs/it/46-location-sample-e-replay-deterministico.md)

### 5 — posizione matched e route progress

```bash
sh tools/tdna lab route-progress
sh tools/tdna bench route-progress 10000 7
```

[Capitolo 47 — Posizione matched e route progress](docs/it/47-posizione-matched-e-route-progress.md)

### 6 — porta di map matching

```bash
sh tools/tdna lab map-matching
sh tools/tdna bench map-matching 10000 7
```

[Capitolo 48 — Porta map matching e fake deterministico](docs/it/48-porta-map-matching-e-fake-deterministico.md)

Il sesto Lab usa un catalogo esatto. Non esegue ricerca di strade, snapping,
HMM/Viterbi o filtro GPS.

### 7 — missed exit e reroute

```bash
sh tools/tdna lab missed-exit
sh tools/tdna bench off-route 10000 7
```

[Capitolo 49 — Off-route, missed exit e reroute](docs/it/49-off-route-missed-exit-e-reroute.md)

Il settimo Lab usa evidenza normalizzata e soglie sintetiche. Non stabilisce
threshold di produzione, non usa traffico live e non misura affidabilità su
strada.

## Verifica completa

```bash
sh tools/tdna doctor
sh tools/tdna check
```

Il comando controlla documentazione, confini architetturali, Java, Rust,
contratto cross-language, Kotlin Multiplatform JVM/Linux, tutti i Lab e i
benchmark diagnostici senza threshold.

## Da dove iniziare

- [Indice della documentazione](docs/README.md)
- [Guida ai percorsi di lettura](docs/it/03-guida-lettura-documentazione.md)
- [Regole operative](docs/it/00-regole-operative.md)
- [Review e merge](docs/it/06-review-e-merge.md)
- [Stato dello sviluppo](docs/project/development-status.md)
- [Report giornalieri](docs/project/daily/README.md)
- [Architettura](docs/it/20-architettura-generale.md)
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
