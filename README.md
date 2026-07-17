# Travel DNA

Travel DNA è un progetto didattico e di prodotto per costruire una guida-diario
sociale dei viaggiatori, capace di funzionare sia con navigatori esterni sia con
una futura navigazione integrata.

Il progetto nasce da quattro idee unite:

1. il viaggio deve avere valore anche quando non si incontra nessun altro utente;
2. il diario deve raccogliere automaticamente percorso, soste e luoghi, lasciando
   all'utente il controllo su fotografie, pensieri e condivisione;
3. gli incontri lungo il viaggio devono diventare consigli, saluti, conversazioni
   e Cartoline DNA senza trasformare l'app in uno strumento di sorveglianza;
4. l'intera codebase deve essere studiabile da studenti e nuovi contributori.

## Stato

La **Documentation Foundation v0** è presente e la milestone
**Foundations and Travel DNA Lab v0** è in corso.

Sono disponibili due vertical slice didattiche:

```text
1. grafo sintetico -> Java/Rust -> Dijkstra/A* -> report confrontato
2. RouteRequest -> RoutePlannerPort -> fake provider -> RoutePlan canonico
```

Non esiste ancora un navigatore mobile di produzione. Le slice stabiliscono
metodo, contratti, fixture, test, CI e documentazione prima di introdurre
OpenStreetMap, MapLibre, Valhalla, Ferrostar, Android o iOS.

## Laboratorio 1 — algoritmo di routing

Prerequisiti minimi:

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

- Gradle compatibile; la CI usa esattamente Gradle 9.5.1;
- Kotlin 2.4.0 risolto dal version catalog.

```bash
sh tools/tdna check-architecture
sh tools/tdna check-kotlin
sh tools/tdna lab routing-contracts
```

Capitolo:
[Contratti routing e fake provider](docs/it/44-contratti-routing-e-fake-provider.md).

## Verifica completa

```bash
sh tools/tdna doctor
sh tools/tdna check
```

Il comando completo controlla documentazione, confini architetturali, Java,
Rust, confronto cross-language, Kotlin Multiplatform e fake provider.

## Da dove iniziare

- Indice della documentazione: [docs/README.md](docs/README.md)
- Studente o nuovo lettore: [docs/it/03-guida-lettura-documentazione.md](docs/it/03-guida-lettura-documentazione.md)
- Contributore: [docs/it/00-regole-operative.md](docs/it/00-regole-operative.md)
- Stato dello sviluppo: [docs/project/development-status.md](docs/project/development-status.md)
- Report giornalieri: [docs/project/daily/README.md](docs/project/daily/README.md)
- Architettura: [docs/it/20-architettura-generale.md](docs/it/20-architettura-generale.md)
- Tecnologie: [docs/it/52-matrice-tecnologie-decisioni.md](docs/it/52-matrice-tecnologie-decisioni.md)
- Navigazione: [docs/it/24-routing-e-navigazione.md](docs/it/24-routing-e-navigazione.md)
- Travel DNA Lab: [docs/it/lab/README.md](docs/it/lab/README.md)
- Decisioni architetturali: [docs/adr/README.md](docs/adr/README.md)
- Rapporto della fondazione v0: [docs/project/FOUNDATION-REPORT.md](docs/project/FOUNDATION-REPORT.md)

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
[ADR-0009](docs/adr/0009-project-licensing-model.md) e aggiunto il file
`LICENSE` appropriato.
