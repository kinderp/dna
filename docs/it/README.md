# Documentazione didattica di Travel DNA

Questa cartella contiene la spiegazione in italiano del prodotto e della
codebase. I commenti nel codice restano in inglese e più sintetici.

## Indice ragionato

| Capitolo | Cosa spiega | Chi lo legge | Quando |
| --- | --- | --- | --- |
| [00 - Regole operative](00-regole-operative.md) | Metodo, scope, hot path, PR seriale, test e gate review. | Tutti i contributori. | Prima di lavorare. |
| [01 - Visione](01-visione-prodotto.md) | Problema, atmosfera, diario, guida e socialità. | Tutti. | Per capire il prodotto. |
| [02 - Glossario](02-glossario.md) | Termini di prodotto, dominio, mappe e navigazione. | Tutti. | Quando un termine non è chiaro. |
| [03 - Guida alla lettura](03-guida-lettura-documentazione.md) | Percorsi per studente, mobile, Java, Rust e Kotlin. | Tutti. | Quando non sai cosa leggere. |
| [04 - Come contribuire](04-come-contribuire.md) | Branch da `main`, una PR, test, review e merge. | Nuovi contributori. | Prima della prima PR. |
| [05 - Tracciabilità](05-tracciabilita-conversazione.md) | Origine delle decisioni iniziali. | Maintainer e studenti. | Per ricostruire il ragionamento. |
| [06 - Review e merge](06-review-e-merge.md) | Flusso seriale, due round, reset e expected-head. | Reviewer e agenti. | Prima di chiudere una PR. |
| [10 - DDD](10-ddd-bounded-context.md) | Domini, confini e linguaggio condiviso. | Architettura. | Prima di introdurre moduli. |
| [11 - Use case](11-use-case-principali.md) | Flussi utente completi e criteri di valore. | Prodotto, design e test. | Prima di una slice. |
| [12 - Stato funzionalità](12-stato-funzionalita.md) | Implementato, pianificato e non-obiettivi. | Tutti. | Prima di promettere una feature. |
| [20 - Architettura](20-architettura-generale.md) | Contesto, componenti, runtime e deployment. | Tecnici. | Prima del codice. |
| [21 - Repository](21-struttura-repository.md) | Monorepo, moduli e responsabilità. | Contributori. | Prima di creare moduli. |
| [22 - Plugin e provider](22-architettura-plugin-provider.md) | Porte, adapter, capability e contract test. | Architettura. | Prima di integrare librerie. |
| [23 - OSM](23-openstreetmap-e-cartografia.md) | Dati OSM, tile, POI, ricerca e licenze. | Mappe e backend. | Prima dei servizi OSM. |
| [24 - Routing e navigazione](24-routing-e-navigazione.md) | Dal grafo al turn-by-turn. | Navigation team. | Per capire un navigatore. |
| [25 - Navigatori esterni](25-navigatori-esterni-e-automotive.md) | Waze, Maps, Android Auto e CarPlay. | Mobile/automotive. | Prima dell'auto. |
| [26 - Diario e media](26-diario-media-pagina-giorno.md) | Timeline, soste, foto e pensieri. | Journey. | Prima del diario. |
| [27 - Presenza e chat](27-presenza-chat-dna.md) | Compagni, consenso e scambio DNA. | Social/backend. | Prima di prossimità. |
| [28 - Linguaggi e GUI](28-stack-linguaggi-e-gui.md) | Kotlin, Java, Swift, Rust e UI native. | Tecnici. | Prima dello stack. |
| [29 - Backend](29-backend-dati-sync.md) | Local-first, sync, messaggi e presenza. | Backend/mobile data. | Prima di API/DB. |
| [30 - Test](30-strategia-test.md) | Unit, contract, replay, UI e field test. | Contributori. | Prima dei test. |
| [31 - GPS replay](31-gps-replay-e-fixture.md) | Fixture deterministiche e simulazione. | Navigation team. | Prima degli algoritmi GPS. |
| [32 - Prestazioni](32-performance-budget.md) | Hot path, budget e benchmark. | Tecnici. | Prima di codice critico. |
| [33 - Privacy e sicurezza](33-privacy-security-driving-safety.md) | Dati, minacce, guida e moderazione. | Tutti. | Prima di funzioni sensibili. |
| [34 - Debugging](34-debugging-e-strumenti.md) | Strumenti JVM, Rust, mobile e rete. | Studenti. | Quando qualcosa non funziona. |
| [35 - Qualità](35-qualita-prodotto-software.md) | Correttezza, robustezza, energia e maturità. | Tutti. | Per valutare una feature. |
| [36 - Licenze](36-licenze-dati-supply-chain.md) | ODbL, SBOM e supply chain. | Maintainer. | Prima delle dipendenze. |
| [40 - Mappa codice](40-mappa-codice-e-stati.md) | Percorsi reali, ownership e stati. | Studenti/reviewer. | Per orientarsi. |
| [41 - Tracepoint](41-tracepoint-model-v0.md) | Nomi logici stabili per gli stage. | Lab e test. | Prima degli scenari. |
| [42 - Lab roadmap](42-traveldna-lab-roadmap.md) | Scenari didattici riproducibili. | Docenti/studenti. | Per i laboratori. |
| [43 - Routing Java/Rust](43-reference-routing-java-rust.md) | Grafo, Dijkstra, A* e confronto. | Studenti Java/Rust. | Primo Lab. |
| [44 - Contratti routing](44-contratti-routing-e-fake-provider.md) | KMP, porta, invarianti e fake provider. | Kotlin/architettura. | Dopo 43. |
| [45 - MapScene](45-map-scene-e-fake-renderer.md) | Scena, delta, projector e fake renderer. | Kotlin/mappe. | Dopo 44. |
| [46 - LocationSample](46-location-sample-e-replay-deterministico.md) | Tempo monotono, gate, replay e benchmark. | Navigation runtime. | Dopo 45. |
| [47 - Route progress](47-posizione-matched-e-route-progress.md) | Posizione matched, leg, manovra, arrival e delta. | Navigation runtime. | Dopo 46, prima di off-route. |
| [50 - Milestone](50-registro-milestone.md) | Evoluzione e dipendenze. | Maintainer. | Durante la pianificazione. |
| [51 - Librerie](51-roadmap-librerie-open-source.md) | Possesso, sostituzione e upstream. | Architettura. | Prima di riscrivere. |
| [52 - Tecnologie](52-matrice-tecnologie-decisioni.md) | Alternative, pro, contro e stato. | Tutti. | Prima di riaprire scelte. |
| [53 - Riferimenti](53-riferimenti-tecnici.md) | Fonti ufficiali. | Tutti. | Per approfondire. |
| [54 - LoRa](54-spike-lora-roadmap.md) | Domande LoRa/LoRaWAN future. | Ricerca. | Dopo discussione dedicata. |

## Scrittura dei capitoli

Ogni capitolo include, quando utile: obiettivi, prerequisiti, problema,
decisione, trade-off, percorso nel codice, ownership, invarianti, test,
benchmark, errori comuni, esercizi e non-obiettivi.

## Stato

Il percorso implementation-backed comprende cinque Lab, dai capitoli 43 a 47.
Per lo stato vivo consultare [documentation-status.md](documentation-status.md),
[development-status.md](../project/development-status.md) e l'indice dei
[report giornalieri](../project/daily/README.md).
