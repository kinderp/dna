# Roadmap della piattaforma DNA

## 1. Strategia

DNA non deve attendere il completamento di TDNA. I due progetti devono avanzare in parallelo condividendo contratti stabili e casi d'uso reali.

Regola:

> TDNA resta il primo utilizzatore del navigatore; DNA formalizza ed estrae solo le capacità realmente trasversali.

La roadmap privilegia prototipi end-to-end e differisce hardware, microservizi e marketplace completo finché non sono necessari.

## 2. Obiettivo del primo ciclo

Validare il flusso comune:

```text
DNA Profile
→ DNA Fragment
→ DNA Trace
→ Discovery
→ Compatibility
→ Consent
→ Rendezvous
→ GeoRoom
→ azione Travel o Shopping
```

con adapter Internet e simulato, senza dipendenza da LoRa o altri dispositivi esterni.

## 3. Fase 0 — Fondazione documentale e decisioni

### Deliverable

- visione e glossario;
- bounded context;
- modello iniziale di `DNAFragment`, `DNATrace`, `DNAIntent` e `GeoAnchor`;
- contratto transport-agnostic;
- modello GeoRoom e Subscription;
- threat model iniziale;
- ADR principali;
- scelta di licenza e governance.

### Criterio di uscita

Le principali responsabilità sono assegnate a moduli precisi e nessun verticale dipende direttamente da BLE, NFC, Wi-Fi o LoRa.

## 4. Fase 1 — Vertical slice online

### Obiettivo

Costruire il primo percorso funzionante usando Internet come trasporto.

### Funzioni

- profilo locale minimale;
- creazione di fragment e trace;
- intenti Travel e Shopping;
- matching semplice e spiegabile;
- consenso esplicito;
- GeoRoom associata a punto, area o tratta;
- topic e sottoscrizione;
- visualizzazione sulla mappa;
- TTL e revoca.

### Casi dimostrativi

Travel:

> Due utenti hanno una tratta e una finestra temporale compatibili; il sistema propone una Intent Room.

Shopping:

> Più utenti nella stessa area sono interessati allo stesso prodotto; il sistema propone un gruppo d'acquisto.

Social:

> Interessi musicali compatibili suggeriscono una GeoRoom pubblica, senza rivelare identità o preferenze dettagliate prima del consenso.

## 5. Fase 2 — Simulatore e orchestrazione dei trasporti

### Deliverable

- `CommunicationTransport`;
- `TransportCapabilities`;
- `TransportPolicy`;
- `TransportOrchestrator`;
- `MockTransportAdapter`;
- deduplicazione multi-adapter;
- test con perdita, duplicazione, latenza e partizioni;
- metriche di energia, payload e privacy simulate.

### Criterio di uscita

Lo stesso caso d'uso funziona senza modifiche di dominio passando da Internet a un adapter simulato a banda limitata.

## 6. Fase 3 — GeoChat e mappa v1

### Funzioni

- cluster a diversi livelli di zoom;
- Point, Area e Route Room;
- filtri per verticale e topic;
- messaggi temporanei;
- stati di verifica;
- sottoscrizioni a luogo, topic e condizione;
- trasformazione di chat in intento strutturato;
- moderazione minima;
- privacy cartografica e aggregazione.

### Integrazione TDNA

- chat di luogo;
- chat di tratta;
- transfer condiviso;
- informazioni territoriali con scadenza;
- primo contratto stabile con Geo & Navigation Core.

## 7. Fase 4 — Adapter Android di prossimità

Ordine raccomandato:

1. BLE discovery;
2. NFC rendezvous e check-in;
3. Wi-Fi locale per trasferimento più ricco;
4. fallback Internet.

### Pilot

- 2–4 telefoni Android;
- identificatori rotanti;
- trace a breve durata;
- consenso prima della condivisione;
- handover BLE → Internet;
- handover NFC → GeoRoom;
- test dei permessi e del comportamento in background.

## 8. Fase 5 — LoRa sperimentale

LoRa entra come adapter opzionale, non come requisito del pilot principale.

### Laboratorio

- 4–8 nodi companion;
- collegamento BLE telefono-nodo;
- trace o rendezvous compatti;
- un gateway opzionale;
- simulazione di rete assente;
- sincronizzazione successiva via Wi-Fi o 5G;
- misure di portata, latenza, perdita e consumo;
- verifica di duty cycle e vincoli locali.

### Criterio di successo

Due utenti rilevano una compatibilità tramite LoRa e completano lo scambio autorizzato tramite un altro canale senza che il dominio sappia quale adapter è stato usato.

## 9. Fase 6 — Shopping Price Radar

### Pilot locale

- 3–5 supermercati;
- 300–1.000 prodotti;
- 20–50 famiglie;
- scontrini e scansioni;
- storico prezzi;
- prezzo unitario;
- alert;
- chat dei negozi;
- affidabilità e scadenza delle osservazioni.

Nessun rider e nessuna consegna in questa fase.

## 10. Fase 7 — Basket Optimizer

- confronto del paniere;
- costo di percorso e tempo;
- uno o più punti vendita;
- sostituzioni economiche e qualitative;
- budget ricevuto dal servizio contabile;
- spiegazione della strategia;
- navigazione verso i negozi.

Questa fase valida il riuso del navigatore TDNA fuori dal travel.

## 11. Fase 8 — Contributor e gruppi d'acquisto

- missioni informative pagate;
- verifica di prezzi e disponibilità;
- Intent Room;
- soglie di gruppo;
- offerte dei commercianti;
- prenotazioni e impegni;
- regole di rimborso;
- reputazione contestuale.

## 12. Fase 9 — Pilot di consegna equa

Solo dopo aver validato dati, domanda e gruppi:

- pochi rider;
- area ristretta;
- compenso minimo;
- attesa e difficoltà remunerate;
- sostituzioni autorizzate;
- tracciamento proporzionato;
- ottimizzazione multi-stop;
- assicurazione, inquadramento e procedure di contestazione.

## 13. Fase 10 — Ecosistema locale

- integrazione con gestionali;
- cataloghi dei commercianti;
- pizzerie e ristorazione;
- abbigliamento e altri negozi;
- servizi locali;
- campagne commerciali trasparenti;
- smart locker e sensori opzionali;
- gateway comunitari solo dove producono valore.

## 14. Backlog tecnico iniziale

### Epic A — Domain core

- schemi versionati;
- validazione;
- eventi;
- consenso;
- TTL;
- revoca;
- audit.

### Epic B — Discovery

- transport contract;
- orchestrator;
- mock adapter;
- Internet adapter;
- BLE;
- NFC;
- Wi-Fi;
- LoRa sperimentale.

### Epic C — Commons

- GeoRoom;
- topic;
- messaggi;
- sottoscrizioni;
- gruppi;
- moderazione;
- conversione chat-intento.

### Epic D — Geo

- ancore;
- clustering;
- geocodifica;
- routing;
- multi-stop;
- cost model.

### Epic E — Verticali

- Travel compatibility;
- shared transfer;
- Price Radar;
- Basket Optimizer;
- group purchase.

## 15. Decisioni da non anticipare

Rimandare finché non esiste evidenza:

- microservizi;
- blockchain;
- token economici;
- protocollo LoRa proprietario completo;
- knowledge graph globale;
- ranking reputazionale unico;
- marketplace nazionale;
- gestione bancaria interna;
- AI generativa come requisito centrale.

## 16. Metriche di piattaforma

- tempo da intento a compatibilità utile;
- percentuale di compatibilità accettate;
- motivazioni comprese dagli utenti;
- condivisioni revocate correttamente;
- trace scadute eliminate;
- successo del rendezvous per adapter;
- consumo energetico;
- falsi match e segnalazioni di abuso;
- GeoRoom che producono un'azione strutturata;
- valore generato da almeno due verticali.

## 17. Prossima implementazione consigliata

La prima sprint di codice deve costruire un **vertical slice online** e un `MockTransportAdapter`, non l'hardware LoRa.

Deliverable della sprint:

1. schemi v0.1;
2. due profili simulati;
3. trace temporanee;
4. matching spiegabile;
5. consenso;
6. GeoRoom su una mappa di prova;
7. intento Travel e intento Shopping;
8. test di scadenza, revoca, duplicazione e perdita;
9. documentazione API;
10. demo riproducibile.