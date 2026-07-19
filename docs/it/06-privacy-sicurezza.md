# Privacy e sicurezza

## 1. Obiettivo

DNA tratta dati che possono rivelare interessi, abitudini, spostamenti, acquisti, relazioni e presenza fisica. Privacy e sicurezza non possono essere aggiunte dopo il matching o dopo l'introduzione delle chat geografiche.

Il principio guida è:

> Il sistema deve poter rilevare una compatibilità senza rivelare più informazioni di quelle necessarie a decidere se proseguire.

## 2. Classificazione dei dati

### Livello 0 — Pubblico

- informazioni ufficiali di un luogo;
- topic pubblici;
- offerte pubblicate da un commerciante;
- dati aggregati non riconducibili a singoli.

### Livello 1 — Contestuale pseudonimo

- DNA Trace;
- intento approssimato;
- appartenenza temporanea a una GeoRoom;
- contributo pubblico con pseudonimo.

### Livello 2 — Condiviso con consenso

- DNA Fragment;
- dettagli di un viaggio o di un acquisto condivisi con un gruppo;
- messaggi privati;
- dati necessari a una consegna.

### Livello 3 — Sensibile operativo

- posizione precisa;
- indirizzo di consegna;
- contatti;
- dati di pagamento o riferimenti transazionali;
- collegamento tra identità reale e pseudonimi;
- chiavi e credenziali.

## 3. Minacce principali

### Tracciamento persistente

Un osservatore raccoglie beacon BLE o LoRa e segue lo stesso dispositivo nel tempo.

Contromisure:

- identificatori rotanti;
- durata breve;
- randomizzazione degli intervalli;
- niente identificatori hardware nel payload;
- separazione tra device identity e radio identity;
- possibilità di disattivare discovery in background.

### Re-identificazione

Combinazioni rare di interessi, luogo e orario possono identificare una persona anche senza nome.

Contromisure:

- categorie più ampie nelle trace;
- soglie minime per dati aggregati;
- riduzione della precisione geografica;
- soppressione di combinazioni troppo rare;
- dettagli solo dopo consenso.

### Replay

Un attaccante ritrasmette una trace valida in un luogo o momento diverso.

Contromisure:

- TTL breve;
- nonce o contatore;
- binding opzionale al contesto;
- cache dei messaggi già osservati;
- autenticatore;
- verifica temporale tollerante ma limitata.

### Sybil e falsi gruppi

Un soggetto crea molte identità per manipolare domanda, reputazione, prezzi o conversazioni.

Contromisure:

- livelli di identità verificata;
- rate limit;
- costi progressivi per azioni ad alto impatto;
- attestazioni di dispositivo;
- analisi dei pattern;
- separazione tra anonimato pubblico e responsabilità interna.

### Stalking e molestie

La mappa o il matching vengono usati per individuare o contattare persone senza consenso.

Contromisure:

- niente marker individuali pubblici;
- aggregazione per area;
- opt-in per ogni dominio;
- blocco e segnalazione;
- limiti ai contatti;
- nessuna chat privata automatica;
- precisione della posizione crescente solo durante una transazione autorizzata.

### Manipolazione di prezzi e disponibilità

Utenti o commercianti pubblicano dati falsi per influenzare acquisti o reputazione.

Contromisure:

- provenance;
- prove fotografiche o scontrini;
- scadenza;
- conferme indipendenti;
- diritto di replica;
- conflitti d'interesse dichiarati;
- distinzione tra sponsorizzato e organico.

### Abuso del lavoro tramite piattaforma

Algoritmi opachi penalizzano rider o contributor.

Contromisure:

- compenso e condizioni prima dell'accettazione;
- spiegazione delle assegnazioni;
- audit;
- contestazione umana;
- nessuna penalizzazione nascosta per il rifiuto;
- metriche sul compenso effettivo.

## 4. Consenso

Ogni `ConsentGrant` deve includere:

```text
consentId
subjectId
fragmentId
purpose
recipientsOrAudience
geoScope
validFrom
expiresAt
allowedOperations
allowedTransports
revocationPolicy
createdAt
proofOfConsent
```

Il consenso non è valido se usa finalità generiche come “migliorare il servizio” per autorizzare discovery, marketing e condivisione con terzi.

## 5. Minimizzazione delle trace

Una `DNATrace` non deve contenere il DNA Fragment completo.

Checklist:

- il dato è necessario per il pre-matching?
- può essere sostituito con una categoria?
- può essere confrontato localmente?
- può essere aggiunto solo dopo il rendezvous?
- la precisione geografica è indispensabile?
- la durata è la più breve possibile?

## 6. Matching locale e remoto

### Locale

Vantaggi:

- meno metadati al server;
- funzionamento offline;
- controllo immediato.

Rischi:

- reverse engineering dell'app;
- esposizione di dizionari di interessi;
- capacità limitate del dispositivo.

### Remoto

Vantaggi:

- algoritmi più ricchi;
- gestione di gruppi e zone ampie;
- audit centralizzato.

Rischi:

- concentrazione dei dati;
- metadati di presenza;
- correlazione tra verticali.

Strategia iniziale: pre-matching minimale locale quando possibile, matching completo sul backend solo con trace pseudonime e retention limitata.

## 7. Sicurezza dei transport adapter

Ogni adapter deve dichiarare:

- autenticazione disponibile;
- cifratura disponibile;
- esposizione a sniffing;
- possibilità di spoofing;
- rischio di relay;
- metadati osservabili;
- supporto alla rotazione degli identificatori;
- limiti del payload;
- policy di retry.

Il dominio non deve assumere che il trasporto sia sicuro. L'envelope applicativo deve proteggere autenticità e replay anche quando il link sottostante offre già cifratura.

## 8. GeoChat

Rischi specifici:

- diffamazione;
- informazioni false;
- pubblicità occulta;
- molestie;
- esposizione di minori;
- contenuti urgenti scambiati per informazioni ufficiali.

Misure:

- classificazione del messaggio;
- stati di verifica;
- scadenza automatica per informazioni temporanee;
- moderatori locali;
- log delle modifiche;
- diritto di replica;
- segnalazione rapida;
- escalation per sicurezza;
- limitazioni specifiche per account minorenni.

## 9. Posizione

La posizione deve avere livelli di precisione:

```text
COUNTRY
REGION
CITY
ZONE
COARSE_POINT
PRECISE_POINT
LIVE_POSITION
```

Ogni caso d'uso dichiara il livello massimo necessario.

Esempi:

- gruppo d'acquisto: `ZONE`;
- chat di negozio: presenza verificata senza pubblicare il punto;
- rider assegnato: `PRECISE_POINT` solo durante la consegna;
- navigazione personale: posizione locale al dispositivo, salvo servizi esplicitamente attivati.

## 10. Retention e cancellazione

Classi raccomandate:

- trace: minuti o ore;
- rendezvous token: minuti;
- intenti non conclusi: fino alla scadenza più breve periodo antifrode;
- messaggi temporanei: in base al topic;
- consenso e revoca: retention di audit;
- transazioni: secondo obblighi contrattuali e fiscali;
- dati aggregati: solo dopo verifica di non re-identificabilità.

La cancellazione deve propagarsi a cache, indici e proiezioni, non solo al record principale.

## 11. Audit

Azioni da registrare:

- creazione e modifica di fragment;
- consenso;
- revoca;
- accesso a dati di livello 2 o 3;
- condivisione della posizione precisa;
- modifica di policy;
- azioni di moderazione;
- decisioni algoritmiche con impatto economico;
- assegnazione e compenso di missioni.

L'audit non deve diventare una seconda banca dati permanente di contenuti sensibili.

## 12. Sicurezza del pilot

Prima del pilot pubblico:

- threat modeling per ogni nuovo adapter;
- test anti-replay;
- rotazione degli identificatori;
- revoca dei dispositivi;
- limiti di frequenza;
- blocco e segnalazione;
- simulazione di Sybil;
- test di perdita e duplicazione;
- verifica della cancellazione;
- revisione legale su privacy, lavoro e commercio;
- procedura di risposta agli incidenti.

## 13. Criterio di blocco

Una funzionalità non deve essere rilasciata quando:

- richiede posizione più precisa del necessario;
- rende correlabili trace successive;
- apre contatti privati senza consenso;
- non permette revoca;
- non distingue contenuti verificati e non verificati;
- usa un adapter senza modellarne capacità e rischi;
- produce decisioni economiche non spiegabili o contestabili.