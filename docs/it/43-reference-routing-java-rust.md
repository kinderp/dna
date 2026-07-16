# Primo laboratorio eseguibile: routing di riferimento in Java e Rust

## Perché questo laboratorio viene prima dell'app mobile

Travel DNA dovrà integrare mappe, GPS, navigatori esterni, servizi di routing,
chat e diario. Iniziare direttamente da Android o iOS renderebbe però difficile
capire dove nasce un errore: nel grafo, nell'algoritmo, nel provider, nel
lifecycle mobile o nella rappresentazione grafica.

Il primo laboratorio riduce il problema alla sua forma essenziale:

```text
una piccola rete stradale sintetica
-> una richiesta origine/destinazione
-> Dijkstra o A*
-> un percorso ricostruito
-> un report deterministico
```

La stessa fixture viene letta da due implementazioni indipendenti:

- Java 21, scelta come reference implementation leggibile;
- Rust stable, scelta come secondo runtime e futuro candidato per core
  deterministici ad alte prestazioni.

Il risultato viene confrontato byte per byte. Questo non dimostra che Java e
Rust siano intercambiabili in ogni aspetto; dimostra che entrambe le
implementazioni rispettano lo stesso piccolo contratto osservabile.

## Obiettivi didattici

Al termine del laboratorio lo studente dovrebbe saper spiegare:

1. perché una rete stradale può essere modellata come grafo;
2. la differenza fra nodo, strada, costo e percorso;
3. come Dijkstra usa il costo accumulato;
4. come A* aggiunge un'euristica;
5. che cosa significa euristica ammissibile;
6. come si ricostruisce il percorso tramite i predecessori;
7. perché fixture e output devono essere deterministici;
8. come due linguaggi possono essere confrontati tramite un contratto, non
   tramite l'identità del codice;
9. perché questo modello non è ancora il contratto mobile di produzione;
10. come test, documentazione e CI trasformano un algoritmo in un componente
    studiabile.

## Dove si trova il codice

```text
fixtures/routes/reference-network-v0.tdna
fixtures/routes/reference-network-v0.meta.yaml

java/reference-routing/
  src/main/java/org/traveldna/reference/routing/
  src/test/java/org/traveldna/reference/routing/

crates/tdna-reference-routing/
  Cargo.toml
  src/lib.rs
  src/geo.rs
  src/graph.rs
  src/fixture.rs
  src/router.rs
  src/main.rs
  src/tests.rs

tests/contract/test_reference_routing.sh
tools/tdna
.github/workflows/foundation-ci.yml
```

Le directory Java e Rust contengono implementazioni volutamente autonome. Non
condividono una libreria nativa e non chiamano provider esterni. Il punto è
osservare due realizzazioni dello stesso problema.

## La fixture `TDNA_REFERENCE_GRAPH_V0`

La fixture è un file di testo line-oriented. È semplice per scelta: può essere
letta a mano, modificata durante una lezione e parsata senza dipendenze esterne.

```text
TDNA_REFERENCE_GRAPH_V0
scenario reference-network-v0
node A 0.000000 0.000000
node B 0.000000 0.010000
node C 0.010000 0.000000
node D 0.010000 0.010000
node E 0.010000 0.020000
road A B 2500
road A C 1200
road C D 1200
road B D 1200
road D E 1200
road B E 4000
road C E 4000
query A E
expect 3600 A,C,D,E
```

### Header

```text
TDNA_REFERENCE_GRAPH_V0
```

Identifica la versione del formato. Un parser non deve indovinare il significato
di una fixture senza versione.

### Scenario

```text
scenario reference-network-v0
```

Fornisce un identificatore stabile usato nei report, nei test e nei documenti.
Gli identificatori v0 ammettono solo lettere ASCII, numeri, punto, trattino e
underscore. La restrizione evita escaping ambiguo nel piccolo report JSON del
Lab.

### Nodi

```text
node ID LATITUDE LONGITUDE
```

Ogni nodo rappresenta un punto attraversabile della rete. Le coordinate sono
WGS84 valide:

- latitudine fra `-90` e `90`;
- longitudine fra `-180` e `180`;
- valori finiti.

Nel routing reale un nodo OpenStreetMap può rappresentare un'intersezione, un
punto di forma o un'estremità di strada. Qui usiamo soltanto cinque nodi
sintetici.

### Strade

```text
road FROM TO COST_METRES
```

La direttiva crea una strada bidirezionale. Il costo è un intero positivo in
metri. Per il laboratorio v0 imponiamo anche:

```text
costo della strada >= distanza geodetica diretta fra i nodi
```

Questa regola è importante per A*: la distanza in linea d'aria non può
sovrastimare il costo reale della strada. Se la sovrastimasse, l'euristica non
sarebbe più ammissibile e A* potrebbe perdere la garanzia di trovare il percorso
ottimo.

Nel routing reale il costo non coincide sempre con la lunghezza. Può includere:

- tempo di percorrenza;
- velocità consentita;
- pedaggi;
- svolte;
- classe della strada;
- preferenze del veicolo;
- traffico;
- penalità turistiche o ambientali.

Il laboratorio usa un solo numero per non nascondere l'algoritmo dietro un
modello di costo prematuro.

### Query e ground truth

```text
query A E
expect 3600 A,C,D,E
```

La query indica origine e destinazione. `expect` registra il risultato atteso:

- costo totale: `3600` metri;
- percorso: `A -> C -> D -> E`.

La ground truth rende la fixture un test, non soltanto un esempio.

## Rappresentazione del grafo

Entrambe le implementazioni conservano:

```text
node ID -> coordinate
node ID -> lista ordinata di strade uscenti
```

Le liste sono ordinate per identificatore di destinazione. Questo dettaglio non
cambia il costo ottimo, ma rende stabile il comportamento quando due candidati
hanno la stessa priorità.

### Perché non usare una matrice di adiacenza

Una matrice per `N` nodi richiede `N × N` celle, anche quando quasi tutte le
coppie non sono collegate. Una rete stradale è normalmente sparsa: ogni nodo ha
poche strade adiacenti rispetto al numero totale dei nodi. Le liste di adiacenza
sono quindi più naturali e più economiche.

## Dijkstra

Dijkstra mantiene per ogni nodo il miglior costo conosciuto dall'origine.

Schema semplificato:

```text
best_cost[origin] = 0
frontier.push(origin, priority=0)

while frontier non vuota:
    current = elemento con priorità minima

    se current è la destinazione:
        ricostruisci il percorso

    per ogni strada current -> next:
        candidate = best_cost[current] + road_cost
        se candidate < best_cost[next]:
            best_cost[next] = candidate
            previous[next] = current
            frontier.push(next, priority=candidate)
```

La priority queue evita di scandire tutti i nodi a ogni passo. Una voce vecchia
può restare nella coda dopo che è stato trovato un costo migliore; quando viene
estratta, viene ignorata confrontandola con `best_cost`.

Questa tecnica evita l'operazione decrease-key, che non è offerta direttamente
né dalla `PriorityQueue` Java né dalla `BinaryHeap` Rust.

## A*

A* usa:

```text
priorità = costo già percorso + stima del costo rimanente
```

Nel laboratorio:

```text
g(n) = costo dall'origine al nodo n
h(n) = distanza WGS84 in linea d'aria da n alla destinazione
f(n) = g(n) + h(n)
```

Dijkstra è quindi il caso particolare:

```text
h(n) = 0
```

### Euristica ammissibile

Un'euristica è ammissibile quando non sovrastima mai il costo reale rimanente.
La distanza in linea d'aria è inferiore o uguale a un percorso stradale normale,
a condizione che i costi delle strade non siano artificialmente inferiori alla
loro distanza geometrica.

Per questo il loader rifiuta una strada che dichiara, per esempio, `500 m` fra
punti distanti `1100 m` in linea d'aria.

Questa non è una regola universale del prodotto. È un'invariante didattica del
formato v0.

## Ricostruzione del percorso

Durante la ricerca salviamo:

```text
previous[next] = current
```

Quando raggiungiamo la destinazione, percorriamo i predecessori al contrario:

```text
E <- D <- C <- A
```

Infine invertiamo la lista:

```text
A -> C -> D -> E
```

Se la catena dei predecessori è incompleta, l'implementazione segnala un errore
interno invece di produrre un percorso parziale ambiguo.

## Determinismo

Un algoritmo può trovare più percorsi dello stesso costo. Un test
byte-for-byte, però, richiede una regola stabile. Il Lab usa tre criteri di
ordinamento:

1. priorità totale minore;
2. costo percorso minore;
3. identificatore nodo lessicograficamente minore.

Questo permette a Java e Rust di scegliere nello stesso modo in caso di parità.
Nel routing di produzione dovremo distinguere:

- equivalenza semantica: due percorsi entrambi validi;
- identità esatta: stessa geometria e stesse manovre;
- preferenza prodotto: uno dei percorsi è migliore per il DNA di viaggio.

Il confronto byte-for-byte è adatto a questo scenario piccolo, non a ogni
confronto futuro fra motori reali.

## Il report osservabile

Le due CLI emettono una sola riga:

```json
{"scenario":"reference-network-v0","algorithm":"dijkstra","origin":"A","destination":"E","path":["A","C","D","E"],"total_cost_m":3600}
```

L'ordine dei campi è fisso. Il report è:

- facile da leggere;
- facile da confrontare con `diff`;
- abbastanza strutturato per futuri strumenti;
- volutamente ristretto.

Non è l'API pubblica `RoutePlan` di Travel DNA. Non contiene:

- geometria completa;
- legs;
- manovre;
- durata;
- provenance del provider;
- capability;
- restrizioni;
- traffico;
- error model di produzione.

Chiamarlo “contratto canonico definitivo” sarebbe un errore architetturale.

## Percorso Java

```text
ReferenceRoutingCli.main()
-> ReferenceFixtureParser.parse()
-> RoadGraph.addNode()/addBidirectionalRoad()
-> DijkstraRouter o AStarRouter
-> AbstractBestFirstRouter.route()
-> reconstructPath()
-> RouteReport.canonicalLine()
```

### Scelte Java

- `record` per value object piccoli;
- `LinkedHashMap` per conservare un ordine leggibile;
- `PriorityQueue` con comparator esplicito;
- `Math.addExact` per non ignorare overflow dei costi;
- `List.copyOf` per risultati immutabili;
- nessuna dipendenza da JUnit nel bootstrap.

L'assenza iniziale di JUnit non è una raccomandazione generale. Serve a rendere
eseguibile la prima slice con il solo JDK 21. Quando il build JVM sarà
stabilizzato, i test confluiranno in un framework standard.

## Percorso Rust

```text
main()
-> parse_fixture()
-> RoadGraph::add_node()/add_bidirectional_road()
-> route()
-> reconstruct_path()
-> canonical_report()
```

### Scelte Rust

- `Result<T, String>` per il primo error model didattico;
- `BTreeMap` per ordine deterministico;
- `BinaryHeap` con `Ord` invertito per ottenere una min-heap;
- `checked_add` per intercettare overflow;
- nessun `unsafe`;
- nessuna crate esterna;
- test unitari nello stesso crate.

Un futuro core di produzione userà error type più strutturati, benchmark e API
pensate per FFI. Il Lab non anticipa quel confine.

## Comandi

### Controllare l'ambiente

```bash
sh tools/tdna doctor
```

Il comando mostra Java, Python e Rust disponibili. Non installa tool di sistema.

### Testare Java

```bash
sh tools/tdna check-java
```

Esegue:

```text
javac --release 21 -Xlint:all -Werror
-> ReferenceRoutingTestSuite
-> report Dijkstra
-> report A*
```

### Testare Rust

```bash
sh tools/tdna check-rust
```

Esegue:

```text
cargo fmt --check
-> cargo test
-> report Dijkstra
-> report A*
```

### Confrontare i linguaggi

```bash
sh tools/tdna check-contract
```

Per ogni algoritmo:

```text
Java report
-> diff
Rust report
```

Qualunque differenza nel report fa fallire il controllo.

### Eseguire il laboratorio

```bash
sh tools/tdna lab reference-routing dijkstra
sh tools/tdna lab reference-routing astar
```

Se Rust non è installato, il comando mostra comunque il risultato Java e
segnala esplicitamente che il confronto Rust è stato saltato. La CI, invece,
richiede entrambe le toolchain.

## Strategia di test

La slice contiene quattro famiglie di prova in entrambi i linguaggi:

1. validazione coordinate e distanza;
2. parsing fixture e percorso atteso;
3. grafo disconnesso;
4. fixture malformata.

Il contract test aggiunge una quinta proprietà:

```text
stesso scenario + stesso algoritmo
-> stesso report osservabile Java/Rust
```

La CI esegue anche il link checker della documentazione. Codice e libro devono
quindi evolvere insieme.

## Cosa questo laboratorio non dimostra

Non dimostra che:

- l'algoritmo scala all'Italia;
- le coordinate siano già convertite in grafo OSM;
- il costo rappresenti tempo di viaggio;
- A* sia sempre più veloce di Dijkstra;
- Java o Rust siano già scelti per ogni componente;
- esista map matching;
- esistano manovre o voce;
- il codice sia pronto per Android o iOS;
- il report sia una API stabile.

Il valore della slice è più preciso:

> Il repository possiede ora un primo comportamento eseguibile, deterministico,
> documentato e confrontabile fra due linguaggi senza dipendere da provider.

## Perché non abbiamo introdotto subito Gradle

Il primo laboratorio usa `javac`, Cargo e uno script sottile. La scelta riduce il
numero di variabili durante il bootstrap:

- nessun plugin Gradle;
- nessun repository di dipendenze JVM;
- nessun framework di test;
- nessun SDK mobile;
- nessuna generazione di codice.

Non è una decisione contro Gradle. La futura codebase Kotlin Multiplatform e
Android richiederà Gradle e un wrapper versionato. Quel passaggio deve avvenire
quando esiste il primo contratto KMP reale, non per rendere più complesso un
laboratorio che non ne ha bisogno.

## Esercizi

### Esercizio 1 — cambiare un costo

Ridurre il costo `A B` e prevedere prima di eseguire quale percorso verrà scelto.
Controllare che la distanza geometrica resti compatibile con l'euristica.

### Esercizio 2 — aggiungere un nodo

Aggiungere `F`, collegarlo al grafo e modificare la query. Aggiornare anche la
ground truth.

### Esercizio 3 — creare una parità

Costruire due percorsi dello stesso costo e osservare il tie-break lessicografico.
Spiegare perché il determinismo è utile nei test ma non sostituisce i criteri di
prodotto.

### Esercizio 4 — rendere l'euristica non ammissibile

Provare a dichiarare una strada più corta della distanza diretta. Il loader deve
rifiutare la fixture. Non eliminare il controllo: spiegare quale garanzia
proteggerebbe.

### Esercizio 5 — contare i nodi visitati

Aggiungere una metrica interna a Java e Rust senza inserirla nel report di
contratto. Confrontare Dijkstra e A* su un grafo più grande.

### Esercizio 6 — introdurre una strada direzionale

Progettare una nuova direttiva `oneway`. Prima di implementarla, definire:

- compatibilità del formato;
- errori;
- test;
- aggiornamento documentale;
- impatto sul report.

## Errori comuni

- confondere distanza geometrica e costo;
- usare un costo negativo con Dijkstra;
- non scartare voci obsolete della coda;
- considerare la prima route trovata sempre ottima senza verificare la priorità;
- usare `HashMap` e dipendere accidentalmente dall'ordine di iterazione;
- confrontare floating point come se fossero identificatori;
- chiamare il modello del Lab “API canonica”;
- modificare fixture senza aggiornare ground truth;
- correggere solo Java o solo Rust;
- accettare un test verde senza aggiornare il capitolo.

## Collegamenti

- [Routing e navigazione](24-routing-e-navigazione.md)
- [Strategia di test](30-strategia-test.md)
- [GPS replay e fixture](31-gps-replay-e-fixture.md)
- [Mappa del codice e degli stati](40-mappa-codice-e-stati.md)
- [Travel DNA Lab](42-traveldna-lab-roadmap.md)
- [Scenario eseguibile](lab/scenarios/reference-routing-java-rust.md)
- [Milestone](50-registro-milestone.md)
