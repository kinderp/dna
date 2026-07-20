# Tracciabilità della progettazione iniziale

Questo documento dimostra che i temi emersi nella progettazione iniziale non sono
rimasti soltanto nella conversazione. Ogni riga collega l'idea alla decisione
corrente e ai documenti stabili.

## Mappa cronologica

| Tema emerso | Decisione consolidata | Documenti principali | Stato |
| --- | --- | --- | --- |
| Scambio automatico o esplicito di informazioni tra utenti vicini | Il motore generale resta una visione futura; la prima verticale è Travel DNA. | `01`, `10`, `27` | Deciso |
| Viaggi, supermercato, scuola, musica e acquisti di gruppo | Non costruire tutte le verticali insieme. Travel è il primo bounded context di prodotto; gli altri restano futuri. | `01`, `12`, `50` | Deciso |
| App simile a Waze con utenti sulla mappa | La mappa mostra presenza approssimata, direzione e aggregati, non coordinate esatte. | `01`, `27`, `33` | Deciso |
| Valori delle famiglie italiane in viaggio e delle guide Touring | L'atmosfera e i valori editoriali guidano il design; contenuti e marchi Touring richiedono accordo. | `01`, `36` | Deciso |
| Rendere l'app necessaria come diario | Il diario è il valore autonomo e precede lo scambio sociale. | `01`, `26` | Deciso |
| Riepilogo serale con fotografie e pensieri | La Pagina del giorno è una bozza modificabile con foto, testo, voce e privacy per momento. | `26`, scenario diario | Deciso |
| OpenStreetMap | OSM è la base dati geografica; tile, geocoding e offline richiedono infrastruttura o provider conformi. | `23`, `36` | Deciso |
| Navigatore open source | Baseline: MapLibre per rendering, Valhalla per routing, Ferrostar per guidance, tutti dietro adapter. | `22`, `24`, ADR 0004 | Proposto/accettato per prototipo |
| Possibilità di sostituire ogni componente | Architettura esagonale, contratti canonici, capability, registry, contract test e shadow mode. | `20`, `22`, ADR 0002 | Deciso |
| Creare librerie open source proprie | Possedere contratti, replay, normalizzazione e guidance progressivamente; non riscrivere subito MapLibre. | `51` | Deciso come strategia |
| Utenti potrebbero preferire Waze o Google Maps | Navigazione esterna è modalità di prima classe; Travel DNA resta Companion. | `25`, ADR 0003 | Deciso |
| Split screen Waze, Travel DNA e Spotify | Travel DNA non controlla il layout globale dell'auto; usa superfici consentite da Android Auto/CarPlay. | `25` | Vincolo documentato |
| Integrazione come plugin dentro Waze o Maps | Non è una normale API pubblica; partnership o integrazioni profonde restano ultima alternativa. | `25`, `52` | Deciso |
| Chat attiva durante la guida con navigatore esterno | La chat resta connessa; conducente usa voce/notifiche, passeggero usa UI completa. | `25`, `27`, `33` | Deciso |
| Navigatore interno deve essere molto reattivo | UI nativa, mappa persistente, stato granulare, hot path isolati e performance budget. | `28`, `32`, ADR 0001/0006 | Deciso |
| Linguaggi e librerie | Kotlin/KMP per logica condivisa, UI native, Rust per core deterministico, Java per backend/reference/lab. | `28`, `52` | Baseline; backend da confermare |
| DDD, TDD, Unified Process | DDD strategico, architettura esagonale, UP leggero risk-driven, TDD e vertical slice. | `00`, `10`, `30`, `50` | Deciso |
| Progetto didattico per studenti | Documentazione italiana, commenti inglesi, percorsi di lettura, Lab, tracepoint e benchmark. | `README`, `03`, `41`, `42` | Deciso |
| Adattare il metodo Alfred | Ereditare micro-step, tracciabilità, documentazione e scenari; adattare review e commenti ai linguaggi mobili. | `00`, `04`, `42` | Deciso |
| Documentare tutta l'architettura e tutte le alternative | L'architettura è divisa in viste, technology matrix, ADR e documenti specialistici. | `20`–`36`, `52`, ADR | In corso continuo |
| Valutare LoRa | Nessuna decisione anticipata; preparare uno spike separato. | `54` | Aperto |

## Regola di manutenzione

Quando una nuova discussione produce una decisione:

1. aggiungere o aggiornare una riga;
2. aggiornare i documenti specialistici;
3. creare o modificare un ADR se la decisione è architetturale;
4. segnare chiaramente `deciso`, `proposto`, `aperto` o `superato`;
5. non lasciare la spiegazione soltanto nella chat.

## Perché non conserviamo soltanto il transcript

Un transcript mostra l'ordine della conversazione, ma mescola ipotesi,
correzioni e decisioni. La documentazione stabile riorganizza il materiale per
concetto, esplicita i non-obiettivi e collega le scelte a test e architettura.

Il transcript può restare una fonte storica privata; questo documento è la mappa
pubblica e mantenibile delle decisioni.
