# Documentazione didattica di Travel DNA

Questa cartella contiene la spiegazione in italiano del prodotto e della
codebase. I commenti nel codice restano in inglese e più sintetici.

## Indice ragionato

| Capitolo | Cosa spiega | Chi lo legge | Quando |
| --- | --- | --- | --- |
| [00 - Regole operative](00-regole-operative.md) | Metodo, scope, hot path, test, documentazione e review. | Tutti i contributori. | Prima di lavorare. |
| [01 - Visione del prodotto](01-visione-prodotto.md) | Problema, atmosfera, valore autonomo, diario, guida e socialità. | Tutti. | Per capire perché esiste Travel DNA. |
| [02 - Glossario](02-glossario.md) | Termini di prodotto, dominio, mappe e navigazione. | Tutti. | Quando un termine non è chiaro. |
| [03 - Guida alla lettura](03-guida-lettura-documentazione.md) | Percorsi per studente, Android, iOS, Java, Rust, navigazione e contributi. | Tutti. | Quando non sai cosa leggere. |
| [04 - Come contribuire](04-come-contribuire.md) | Fork, branch, issue, PR, test e review. | Nuovi contributori. | Prima della prima PR. |
| [05 - Tracciabilità della progettazione](05-tracciabilita-conversazione.md) | Come ogni tema della progettazione iniziale è stato consolidato nei documenti. | Maintainer e studenti. | Per ricostruire l'origine delle decisioni. |
| [10 - DDD e bounded context](10-ddd-bounded-context.md) | Domini, confini e linguaggio condiviso. | Architettura e dominio. | Prima di introdurre moduli o modelli. |
| [11 - Use case](11-use-case-principali.md) | Flussi utente completi e criteri di valore. | Prodotto, design, test. | Prima di una vertical slice. |
| [12 - Stato funzionalità](12-stato-funzionalita.md) | Cosa è deciso, proposto, futuro o non implementato. | Tutti. | Prima di promettere una feature. |
| [20 - Architettura generale](20-architettura-generale.md) | Viste di contesto, container, componenti, runtime e deployment. | Tutti i tecnici. | Prima di leggere o scrivere codice. |
| [21 - Struttura repository](21-struttura-repository.md) | Monorepo, moduli e responsabilità. | Contributori. | Prima di creare file o moduli. |
| [22 - Plugin e provider](22-architettura-plugin-provider.md) | Porte, adapter, capability, fallback, shadow e contract test. | Architettura. | Prima di integrare una libreria. |
| [23 - OSM e cartografia](23-openstreetmap-e-cartografia.md) | Dati OSM, tile, POI, ricerca, licenze e infrastruttura. | Mappe e backend. | Prima di usare servizi OSM. |
| [24 - Routing e navigazione](24-routing-e-navigazione.md) | Dal grafo stradale al turn-by-turn. | Studenti e navigation team. | Per capire come si costruisce un navigatore. |
| [25 - Navigatori esterni e auto](25-navigatori-esterni-e-automotive.md) | Waze, Google Maps, Sygic, Android Auto, CarPlay e percorso ombra. | Mobile e automotive. | Prima di lavorare sull'esperienza in auto. |
| [26 - Diario e media](26-diario-media-pagina-giorno.md) | Timeline, soste, foto, pensieri e Cartoline DNA. | Journey e media. | Prima di toccare il diario. |
| [27 - Presenza, chat e DNA](27-presenza-chat-dna.md) | Compagni di strada, chat, consenso e scambio. | Social e backend. | Prima di toccare prossimità o messaggi. |
| [28 - Linguaggi e GUI](28-stack-linguaggi-e-gui.md) | Kotlin, Java, Swift, Rust, UI native e FFI. | Tutti i tecnici. | Prima di scegliere un linguaggio o framework. |
| [29 - Backend e local-first](29-backend-dati-sync.md) | Dati locali, sincronizzazione, messaggi, presenza e servizi. | Backend/mobile data. | Prima di progettare API o DB. |
| [30 - Strategia test](30-strategia-test.md) | Piramide, contract, replay, UI, automotive e field test. | Tutti i contributori. | Prima di aggiungere un test. |
| [31 - GPS replay](31-gps-replay-e-fixture.md) | Fixture deterministiche e simulazione. | Navigation team. | Prima del primo algoritmo GPS. |
| [32 - Prestazioni](32-performance-budget.md) | Hot path, budget, benchmark e metodo di misura. | Tutti i tecnici. | Prima di toccare codice critico. |
| [33 - Privacy e sicurezza](33-privacy-security-driving-safety.md) | Dati, minacce, guida sicura e moderazione. | Tutti. | Prima di funzioni sensibili. |
| [34 - Debugging e strumenti](34-debugging-e-strumenti.md) | Strumenti per JVM, Rust, Android, iOS, mappe e rete. | Studenti e contributori. | Quando qualcosa non funziona. |
| [35 - Qualità software](35-qualita-prodotto-software.md) | Correttezza, robustezza, energia, usabilità e maturità. | Tutti. | Per valutare una feature. |
| [36 - Licenze e supply chain](36-licenze-dati-supply-chain.md) | ODbL, licenze librerie, SBOM e dipendenze. | Maintainer. | Prima di integrare dati o librerie. |
| [40 - Mappa codice e stati](40-mappa-codice-e-stati.md) | Percorsi logici e futuri call graph. | Studenti e reviewer. | Per orientarsi nella codebase. |
| [41 - Tracepoint Model v0](41-tracepoint-model-v0.md) | Nomi logici stabili per spiegare gli stage. | Lab e test. | Prima di creare scenari Lab. |
| [42 - Travel DNA Lab](42-traveldna-lab-roadmap.md) | Scenari didattici riproducibili. | Docenti e studenti. | Per laboratori e walkthrough. |
| [43 - Routing Java/Rust](43-reference-routing-java-rust.md) | Primo capitolo sostenuto da codice: fixture, Dijkstra, A*, determinismo e contract test. | Studenti Java/Rust e contributori. | Per eseguire il primo Lab e leggere codice reale. |
| [50 - Registro milestone](50-registro-milestone.md) | Evoluzione e dipendenze del progetto. | Maintainer. | Quando si pianifica. |
| [51 - Roadmap librerie open source](51-roadmap-librerie-open-source.md) | Cosa possedere, sostituire o contribuire upstream. | Architettura. | Prima di riscrivere una dipendenza. |
| [52 - Matrice tecnologie](52-matrice-tecnologie-decisioni.md) | Alternative, pro, contro e stato delle scelte. | Tutti. | Prima di riaprire una decisione. |
| [53 - Riferimenti tecnici](53-riferimenti-tecnici.md) | Fonti ufficiali verificate. | Tutti. | Per approfondire o aggiornare i documenti. |
| [54 - Spike LoRa](54-spike-lora-roadmap.md) | Domande per la futura valutazione LoRa/LoRaWAN. | Ricerca e architettura. | Dopo una discussione dedicata. |

## Come devono essere scritti i capitoli

Ogni capitolo dovrebbe contenere, quando utile:

- cosa imparerai;
- prerequisiti;
- problema da risolvere;
- concetti di base;
- decisione corrente;
- alternative e trade-off;
- percorso nel codice o architettura;
- errori comuni;
- test o esperimenti;
- non-obiettivi;
- domande di ripasso;
- documenti successivi.

## Stato

Vedere [documentation-status.md](documentation-status.md). Il codice non è ancora
implementato: molte mappe descrivono contratti e responsabilità target, non
funzioni già presenti.
