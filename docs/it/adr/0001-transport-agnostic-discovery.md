# ADR-0001 — Discovery indipendente dal trasporto

- **Stato:** Proposta
- **Data:** 2026-07-19
- **Decision owner:** DNA Platform

## Contesto

DNA deve poter rilevare persone, luoghi, gruppi e intenti compatibili in contesti differenti:

- online tramite Internet;
- in prossimità tramite Bluetooth LE;
- con interazione volontaria tramite NFC;
- su rete locale tramite Wi-Fi Aware o Wi-Fi Direct;
- a lungo raggio o senza copertura tramite LoRa;
- attraverso futuri mezzi non ancora selezionati.

Le tecnologie hanno portata, banda, consumo, sicurezza, affidabilità e disponibilità hardware molto diverse. Inserire riferimenti diretti a LoRa, BLE o Wi-Fi nel dominio renderebbe i verticali dipendenti da una scelta tecnica e impedirebbe handover e fallback.

## Decisione

La piattaforma separa:

1. **Discovery** — rilevare una possibilità;
2. **Compatibility** — valutare se la possibilità è rilevante;
3. **Rendezvous** — concordare come proseguire;
4. **Data exchange** — trasferire i contenuti autorizzati.

I verticali e il dominio dipendono da una porta `CommunicationTransport`. Ogni tecnologia viene implementata da un adapter che dichiara esplicitamente le proprie capability.

Il `TransportOrchestrator` seleziona uno o più adapter usando una `TransportPolicy` basata su:

- consenso;
- capability richieste;
- hardware e permessi disponibili;
- dimensione del payload;
- costo energetico e monetario;
- privacy;
- urgenza;
- affidabilità;
- possibilità di fallback.

La stessa `DNATrace` può essere incapsulata in envelope diversi e trasportata su più mezzi. La deduplicazione usa identificatori indipendenti dall'adapter.

## Conseguenze positive

- LoRa resta opzionale.
- BLE, NFC e Wi-Fi possono essere introdotti progressivamente.
- Il dominio non cambia quando cambia la tecnologia.
- Discovery e trasferimento completo possono usare canali differenti.
- È possibile simulare trasporti prima dell'hardware.
- Le differenze tra i mezzi sono modellate tramite capability invece di essere nascoste.
- Fallback e multi-path diventano responsabilità esplicite.

## Conseguenze negative

- aumenta il numero di contratti e oggetti infrastrutturali;
- l'orchestrazione deve gestire errori e stati eterogenei;
- alcuni adapter non potranno implementare tutte le operazioni;
- test e osservabilità devono coprire combinazioni di trasporti;
- l'astrazione può diventare troppo generica se non guidata da casi reali.

## Vincoli

- nessuna entità di dominio contiene dettagli specifici del trasporto;
- le capability non garantiscono sicurezza: ogni envelope applicativo deve gestire autenticità, TTL e replay;
- il consenso può vietare determinati adapter;
- il payload deve rispettare il limite dell'adapter selezionato;
- un adapter può rifiutare una richiesta non compatibile;
- LoRa non viene usato per chat complete, immagini o cataloghi nel primo ciclo;
- NFC viene trattato principalmente come gesto di rendezvous o attestazione di prossimità.

## Alternative considerate

### LoRa come protocollo principale

Scartata perché richiede hardware aggiuntivo, ha banda limitata e non è disponibile nativamente nella maggior parte degli smartphone.

### BLE come unico mezzo di prossimità

Scartata perché non copre tutti i casi di lungo raggio, rete locale ricca o interazione intenzionale NFC.

### Solo Internet

Scartata come architettura definitiva perché impedisce casi offline, resilienti e di prossimità, ma Internet sarà il primo adapter funzionante del pilot.

### Astrazione uniforme senza capability

Scartata perché nasconderebbe differenze essenziali e produrrebbe errori a runtime o assunzioni false.

## Piano di validazione

1. Implementare `InternetTransportAdapter`.
2. Implementare `MockTransportAdapter` con limiti configurabili.
3. Eseguire lo stesso vertical slice sui due adapter.
4. Aggiungere BLE senza modificare il dominio.
5. Aggiungere NFC per rendezvous esplicito.
6. Aggiungere Wi-Fi per handover locale.
7. Sperimentare LoRa tramite dispositivo companion.

## Criteri per accettare l'ADR

- almeno un caso Travel e uno Shopping usano la stessa porta;
- un test cambia adapter tramite configurazione o policy;
- nessun modello di dominio contiene dettagli radio;
- scadenza, deduplicazione e replay sono testati;
- il fallback non richiede modifiche al verticale.

## Riesame

Riesaminare dopo il primo pilot BLE/NFC e dopo il laboratorio LoRa. L'ADR può essere modificata se l'astrazione impedisce funzionalità reali o se emergono capability non rappresentabili.