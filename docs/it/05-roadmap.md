# Roadmap della piattaforma DNA

## 1. Strategia

DNA non deve attendere il completamento di TDNA. I due progetti devono avanzare in parallelo condividendo contratti stabili e casi d'uso reali.

Regola:

> TDNA resta il primo utilizzatore del navigatore; DNA formalizza ed estrae solo le capacità realmente trasversali.

La roadmap privilegia prototipi end-to-end e differisce hardware, microservizi e marketplace completo finché non sono necessari.

La strategia di prodotto adotta inoltre:

```text
un ecosistema DNA
+ una app mobile modulare
+ esperienze separate per Android Auto e Android Automotive OS
```

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
- architettura multi-esperienza mobile/auto;
- scelta di licenza e governance.

### Criterio di uscita

Le principali responsabilità sono assegnate a moduli precisi e nessun verticale dipende direttamente da BLE, NFC, Wi-Fi, LoRa o da una specifica superficie UI.

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

## 7. Fase 4 — App mobile DNA e design system

### Obiettivo

Costruire una shell mobile unica, modulare e riconoscibile.

### Deliverable

- `:app-mobile`;
- `:design-system`;
- `DomainSwitcher` visibile e accessibile;
- Home/Oggi, Mappa, Attività e Profilo;
- feature module Travel e Shopping;
- accenti visivi di dominio;
- layer cartografici comuni;
- stato condiviso tra domini;
- deep link interni e notifiche;
- possibilità di escludere un dominio da una build.

### Regola UX

Lo swipe può essere una scorciatoia tra lenti compatibili, ma non l'unico modo per cambiare dominio.

## 8. Fase 5 — Android Auto Navigation v1

### Obiettivo

Esporre una esperienza di guida minima e conforme, inizialmente nella categoria `NAVIGATION`.

### Deliverable

- modulo `:car-experience`;
- `CarAppService`, `Session` e schermate template;
- percorso attivo;
- ricerca e avvio navigazione;
- stato condiviso telefono → auto;
- azioni vocali e intenti di navigazione richiesti;
- test DHU;
- checklist Car App Quality;
- nessuna funzione estranea alla categoria dichiarata.

### Criterio di uscita

La navigazione può essere avviata sul telefono e continuata in Android Auto senza duplicare il dominio o riusare UI mobile.

## 9. Fase 6 — Android Automotive OS

### Deliverable

- modulo `:app-automotive`;
- build e manifest dedicati;
- riuso del modulo `:car-experience`;
- persistenza e offline appropriati al veicolo;
- test su emulatore Automotive;
- decisione su package e listing condivisi o separati.

## 10. Fase 7 — POI e primo caso cross-domain in auto

Aggiungere `POI` soltanto quando esiste una esperienza completa e verificabile.

Primo caso consigliato:

```text
TravelRouteActivated
+ ShoppingOpportunityDetected
→ proposta di aggiungere una fermata conveniente
```

In auto si mostra solo l'azione breve:

> Aggiungere la fermata al percorso?

Il confronto dettagliato, il checkout e la gestione del gruppo restano sul telefono.

## 11. Fase 8 — Adapter Android di prossimità

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

## 12. Fase 9 — LoRa sperimentale

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

## 13. Fase 10 — Shopping Price Radar

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

## 14. Fase 11 — Basket Optimizer

- confronto del paniere;
- costo di percorso e tempo;
- uno o più punti vendita;
- sostituzioni economiche e qualitative;
- budget ricevuto dal servizio contabile;
- spiegazione della strategia;
- navigazione verso i negozi.

Questa fase valida il riuso del navigatore TDNA fuori dal travel.

## 15. Fase 12 — Contributor e gruppi d'acquisto

- missioni informative pagate;
- verifica di prezzi e disponibilità;
- Intent Room;
- soglie di gruppo;
- offerte dei commercianti;
- prenotazioni e impegni;
- regole di rimborso;
- reputazione contestuale.

## 16. Fase 13 — Pilot di consegna equa

Solo dopo aver validato dati, domanda e gruppi:

- pochi rider;
- area ristretta;
- compenso minimo;
- attesa e difficoltà remunerate;
- sostituzioni autorizzate;
- tracciamento proporzionato;
- ottimizzazione multi-stop;
- assicurazione, inquadramento e procedure di contestazione.

## 17. Fase 14 — Ecosistema locale

- integrazione con gestionali;
- cataloghi dei commercianti;
- pizzerie e ristorazione;
- abbigliamento e altri negozi;
- servizi locali;
- campagne commerciali trasparenti;
- smart locker e sensori opzionali;
- gateway comunitari solo dove producono valore.

## 18. Backlog tecnico iniziale

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

### Epic F — Surface architecture

- design system;
- app mobile shell;
- DomainSwitcher;
- car-experience;
- app Automotive;
- state handoff;
- voice e notifiche;
- feature flags per build.

## 19. Decisioni da non anticipare

Rimandare finché non esiste evidenza:

- microservizi;
- blockchain;
- token economici;
- protocollo LoRa proprietario completo;
- knowledge graph globale;
- ranking reputazionale unico;
- marketplace nazionale;
- gestione bancaria interna;
- AI generativa come requisito centrale;
- APK separato per ogni dominio;
- dichiarazione contemporanea di tutte le categorie Android for Cars.

## 20. Metriche di piattaforma

- tempo da intento a compatibilità utile;
- percentuale di compatibilità accettate;
- motivazioni comprese dagli utenti;
- condivisioni revocate correttamente;
- trace scadute eliminate;
- successo del rendezvous per adapter;
- consumo energetico;
- falsi match e segnalazioni di abuso;
- GeoRoom che producono un'azione strutturata;
- valore generato da almeno due verticali;
- tempo necessario per passare da telefono ad auto;
- percentuale di azioni Car completate senza ritorno al telefono;
- numero di feature non conformi bloccate prima della release.

## 21. Prossima implementazione consigliata

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

Subito dopo il vertical slice, il prossimo passo applicativo è la shell mobile modulare con design system condiviso; Android Auto entra come superficie separata inizialmente limitata alla navigazione.
