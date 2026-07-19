# ADR-0002 — Alfred come piano asincrono di osservazione e correlazione

- **Stato:** Proposta
- **Data:** 2026-07-19
- **Decision owner:** DNA Platform

## Contesto

DNA produrrà eventi provenienti da domini differenti: Travel, Shopping, Social, Economy, Commons, Geo e altri verticali. Alcuni casi utili richiedono correlazioni temporali, spaziali e causali tra eventi che non appartengono allo stesso bounded context.

Il progetto Alfred possiede già un Event Model, una Backend API, una visione di Observation Runtime, provenance, replay e una direzione esplicita verso un correlation/enrichment engine multi-sorgente. Tuttavia Alfred oggi resta focalizzato sul runtime Linux/filesystem e la sua roadmap corrente non deve essere destabilizzata da un'integrazione applicativa prematura.

## Decisione

DNA integra Alfred come **piano asincrono esterno di osservazione e correlazione**.

Il flusso iniziale è:

```text
DNA domain events
-> transactional outbox
-> DNA-Alfred Bridge
-> privacy/purpose filter
-> Alfred observation log
-> correlation pattern
-> derived observation
-> DNA application service
```

DNA resta la fonte autorevole per:

- identità;
- profili;
- consenso e revoca;
- stato dei bounded context;
- ordini, pagamenti e prenotazioni;
- decisioni applicative e azioni verso gli utenti.

Alfred è autorevole soltanto per:

- il proprio log di osservazioni ricevute;
- provenance, evidence e stato interno delle correlazioni;
- inferenze e projection chiaramente marcate come derivate.

L'integrazione usa delivery almeno una volta, deduplicazione per `eventId`, replay e output versionati. DNA non attende Alfred nel percorso sincrono iniziale.

## Vincoli

- nessun `DNAProfile` completo viene inviato ad Alfred;
- ogni pattern dichiara campi minimi, scopo e retention;
- gli identificatori sono pseudonimi o riferimenti opachi;
- un evento DNA già semantico non viene riclassificato come fatto raw;
- osservazione, inferenza, decisione e azione restano record distinti;
- Alfred non esegue direttamente azioni commerciali o sociali;
- le inferred observations includono pattern, versione, evidence, confidence e validità;
- il primo prototipo è esterno al runtime Alfred corrente;
- nessun plugin dinamico o DSL arbitraria nella prima fase;
- il failure di Alfred non interrompe i verticali DNA.

## Conseguenze positive

- riuso reale della visione Observation Runtime di Alfred;
- correlazione multi-dominio senza accoppiare i verticali;
- spiegabilità e audit delle opportunità proposte;
- replay deterministico e golden test;
- possibilità di integrare in futuro eventi di sistema, rete o agenti con eventi DNA;
- evoluzione graduale del modello Alfred guidata da casi reali;
- separazione tra stato operativo e stato analitico derivato;
- nessun impatto iniziale sul percorso caldo security.

## Conseguenze negative

- nuovo bridge, contratti e pipeline da mantenere;
- consistenza eventuale delle inferenze;
- necessità di gestire retention, revoca e pseudonimi tra sistemi;
- duplicazione controllata degli eventi tra outbox e observation log;
- maggiore complessità di test per correlazioni temporali;
- rischio di trasformare Alfred in una piattaforma troppo generale se i confini non vengono rispettati.

## Alternative considerate

### Correlazione dentro ogni verticale

Scartata come soluzione generale perché duplicherebbe finestre temporali, provenance, replay e logica di evidence. Resta valida per matching semplice e strettamente locale al dominio.

### Alfred come event bus centrale di DNA

Scartata perché confonderebbe trasporto, stato operativo e osservazione. Alfred deve poter essere spento senza fermare DNA.

### Database unico DNA + Alfred

Scartato perché aumenterebbe l'accoppiamento, mescolerebbe retention differenti e renderebbe più difficile la separazione dei dati personali.

### Chiamata sincrona ad Alfred per ogni use case

Scartata per latenza, disponibilità e rischio di rendere Alfred parte del percorso critico.

### Modifica immediata dell'Event Model C di Alfred

Scartata finché non esistono almeno due pattern reali, fixture e replay che dimostrino quali estensioni sono davvero necessarie.

### Knowledge graph come primo motore di correlazione

Rimandato. Può essere una projection futura, ma non è necessario per validare finestre temporali, state machine ed evidence.

## Piano di validazione

1. definire `DNAObservationEnvelope v0`;
2. creare fixture Travel e Shopping;
3. implementare replay JSONL offline;
4. verificare deduplicazione, TTL e revoca;
5. produrre `SharedTransferOpportunityDetected`;
6. produrre `GroupPurchaseOpportunityDetected`;
7. aggiungere un pattern Travel + Shopping;
8. misurare falsi match, latenza e dimensione degli eventi;
9. verificare che DNA funzioni con Alfred indisponibile;
10. riesaminare l'Event Model Alfred soltanto dopo questi risultati.

## Criteri di riesame

Riesaminare l'ADR quando:

- Alfred dispone di un correlation engine stabile;
- il bridge esterno duplica troppo codice già presente in Alfred;
- una correlazione deve diventare sincrona per ragioni misurate;
- emergono requisiti di enforcement e non solo di suggerimento;
- almeno tre domini usano pattern pack comuni;
- i costi di serializzazione o replay richiedono un protocollo binario;
- retention e revoca non risultano gestibili con l'attuale separazione.
