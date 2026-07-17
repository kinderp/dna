# Documentazione didattica di Travel DNA

Questa cartella contiene la spiegazione in italiano del prodotto e della
codebase. I commenti nel codice restano in inglese e più sintetici.

## Indice ragionato

| Capitolo | Cosa spiega | Chi lo legge | Quando |
| --- | --- | --- | --- |
| [00 - Regole operative](00-regole-operative.md) | Metodo, scope, hot path, flusso PR seriale, test e gate di review. | Tutti i contributori. | Prima di lavorare. |
| [01 - Visione del prodotto](01-visione-prodotto.md) | Problema, atmosfera, valore autonomo, diario, guida e socialità. | Tutti. | Per capire perché esiste Travel DNA. |
| [02 - Glossario](02-glossario.md) | Termini di prodotto, dominio, mappe e navigazione. | Tutti. | Quando un termine non è chiaro. |
| [03 - Guida alla lettura](03-guida-lettura-documentazione.md) | Percorsi per studente, mobile, Java, Rust, Kotlin e navigazione. | Tutti. | Quando non sai cosa leggere. |
| [04 - Come contribuire](04-come-contribuire.md) | Branch da `main`, una PR, test, review e merge. | Nuovi contributori. | Prima della prima PR. |
| [05 - Tracciabilità](05-tracciabilita-conversazione.md) | Origine delle decisioni iniziali. | Maintainer e studenti. | Per ricostruire il ragionamento. |
| [06 - Review e merge](06-review-e-merge.md) | Flusso seriale, due round puliti, reset, autorità e riallineamento. | Reviewer, maintainer e agenti. | Prima di chiudere o mergiare una PR. |
| [10 - DDD e bounded context](10-ddd-bounded-context.md) | Domini, confini e linguaggio condiviso. | Architettura e dominio. | Prima di introdurre moduli. |
| [11 - Use case](11-use-case-principali.md) | Flussi utente completi e criteri di valore. | Prodotto, design e test. | Prima di una vertical slice. |
| [12 - Stato funzionalità](12-stato-funzionalita.md) | Implementato, pianificato, futuro e non-obiettivo. | Tutti. | Prima di promettere una feature. |
| [20 - Architettura generale](20-architettura-generale.md) | Contesto, componenti, runtime e deployment. | Tecnici. | Prima di leggere o scrivere codice. |
| [21 - Struttura repository](21-struttura-repository.md) | Monorepo, moduli e responsabilità. | Contributori. | Prima di creare file o moduli. |
| [22 - Plugin e provider](22-architettura-plugin-provider.md) | Porte, adapter, capability, fallback e contract test. | Architettura. | Prima di integrare una libreria. |
| [23 - OSM e cartografia](23-openstreetmap-e-cartografia.md) | Dati OSM, tile, POI, ricerca e licenze. | Mappe e backend. | Prima di usare servizi OSM. |
| [24 - Routing e navigazione](24-routing-e-navigazione.md) | Dal grafo stradale al turn-by-turn. | Navigation team. | Per capire un navigatore. |
| [25 - Navigatori esterni e auto](25-navigatori-esterni-e-automotive.md) | Waze, Maps, Sygic, Android Auto e CarPlay. | Mobile e automotive. | Prima dell'esperienza in auto. |
| [26 - Diario e media](26-diario-media-pagina-giorno.md) | Timeline, soste, foto, pensieri e Cartoline DNA. | Journey e media. | Prima del diario. |
| [27 - Presenza, chat e DNA](27-presenza-chat-dna.md) | Compagni, chat, consenso e scambio. | Social e backend. | Prima di prossimità o messaggi. |
| [28 - Linguaggi e GUI](28-stack-linguaggi-e-gui.md) | Kotlin, Java, Swift, Rust, UI native e FFI. | Tecnici. | Prima di scegliere lo stack. |
| [29 - Backend e local-first](29-backend-dati-sync.md) | Dati locali, sync, messaggi e presenza. | Backend/mobile data. | Prima di API o DB. |
| [30 - Strategia test](30-strategia-test.md) | Unit, contract, replay, UI, automotive e field test. | Contributori. | Prima di aggiungere test. |
| [31 - GPS replay](31-gps-replay-e-fixture.md) | Fixture deterministiche e simulazione. | Navigation team. | Prima degli algoritmi GPS. |
| [32 - Prestazioni](32-performance-budget.md) | Hot path, budget e benchmark. | Tecnici. | Prima di codice critico. |
| [33 - Privacy e sicurezza](33-privacy-security-driving-safety.md) | Dati, minacce, guida e moderazione. | Tutti. | Prima di funzioni sensibili. |
| [34 - Debugging](34-debugging-e-strumenti.md) | Strumenti JVM, Rust, mobile, mappe e rete. | Studenti e contributori. | Quando qualcosa non funziona. |
| [35 - Qualità software](35-qualita-prodotto-software.md) | Correttezza, robustezza, energia e maturità. | Tutti. | Per valutare una feature. |
| [36 - Licenze](36-licenze-dati-supply-chain.md) | ODbL, librerie, SBOM e supply chain. | Maintainer. | Prima di integrare dipendenze. |
| [40 - Mappa codice e stati](40-mappa-codice-e-stati.md) | Percorsi reali e target. | Studenti e reviewer. | Per orientarsi nel codice. |
| [41 - Tracepoint Model](41-tracepoint-model-v0.md) | Nomi logici stabili per gli stage. | Lab e test. | Prima di creare scenari. |
| [42 - Travel DNA Lab](42-traveldna-lab-roadmap.md) | Scenari didattici riproducibili. | Docenti e studenti. | Per laboratori. |
| [43 - Routing Java/Rust](43-reference-routing-java-rust.md) | Grafo, Dijkstra, A*, fixture e confronto. | Studenti Java/Rust. | Primo Lab algoritmico. |
| [44 - Contratti routing e fake provider](44-contratti-routing-e-fake-provider.md) | KMP, porte, modelli canonici, capability, invarianti e testkit. | Studenti Kotlin e architettura. | Dopo il capitolo 43. |
| [45 - MapScene e fake renderer](45-map-scene-e-fake-renderer.md) | Scena dichiarativa, delta, renderer port, projector, fake e hot path. | Studenti Kotlin, mappe e architettura. | Dopo il capitolo 44. |
| [46 - LocationSample e replay](46-location-sample-e-replay-deterministico.md) | Tempo monotono, ordering gate, clock virtuale, fixture, replay e benchmark. | Studenti Kotlin e navigation runtime. | Dopo MapScene, prima di map matching. |
| [50 - Registro milestone](50-registro-milestone.md) | Evoluzione e dipendenze. | Maintainer. | Durante la pianificazione. |
| [51 - Roadmap librerie](51-roadmap-librerie-open-source.md) | Possesso, sostituzione e upstream. | Architettura. | Prima di riscrivere. |
| [52 - Matrice tecnologie](52-matrice-tecnologie-decisioni.md) | Alternative, pro, contro e stato. | Tutti. | Prima di riaprire una scelta. |
| [53 - Riferimenti](53-riferimenti-tecnici.md) | Fonti ufficiali. | Tutti. | Per approfondire. |
| [54 - Spike LoRa](54-spike-lora-roadmap.md) | Domande LoRa/LoRaWAN future. | Ricerca. | Dopo una discussione dedicata. |

## Come devono essere scritti i capitoli

Ogni capitolo dovrebbe contenere, quando utile:

- cosa imparerai e prerequisiti;
- problema, decisione e trade-off;
- percorso nel codice o architettura;
- invarianti, test ed esperimenti;
- errori comuni e non-obiettivi;
- esercizi e documenti successivi.

## Stato

Vedere [documentation-status.md](documentation-status.md). `main` contiene tre
Lab eseguibili; la slice corrente aggiunge il quarto Lab su `LocationSample`,
tempo monotono e replay deterministico e resta in draft fino a CI finale e due
review pulite sullo stesso SHA.
