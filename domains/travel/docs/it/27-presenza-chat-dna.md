# Presenza, chat e scambio DNA

## Obiettivo

Creare socialità di viaggio senza trasformare Travel DNA in un radar di persone
o in una chat casuale incontrollata.

## Presenza stradale

Il telefono conosce la posizione precisa per funzioni autorizzate. Il server e
gli altri utenti non devono ricevere automaticamente lo stesso livello di
dettaglio.

Pipeline:

```text
LocationSample precise
-> local road context
-> privacy approximation
-> ephemeral presence signal
-> server aggregation/matching
-> approximate companion view
```

## Presence signal

Campi candidati:

```text
ephemeral id
road corridor/cell
direction bucket
time bucket
travel-mode category
broad destination optional
DNA compatibility sketch optional
contact permissions
expiry
```

Non includere:

- targa;
- coordinate esatte pubbliche;
- hotel preciso;
- casa;
- cognome;
- itinerario completo;
- minori identificabili.

## Tecniche di approssimazione

- segment cell tra svincoli;
- geohash/H3 a risoluzione controllata;
- ritardo temporale;
- jitter;
- aggregazione minima k;
- “minuti avanti” a bucket;
- pubblicazione solo in viaggio attivo.

La tecnica va scelta con threat model, non solo per UI.

## Expiry

Ogni segnale scade rapidamente. Il backend non deve mantenere una cronologia
consultabile degli incontri non salvati.

## Compagno di strada

La UI può mostrare:

```text
Famiglia emiliana
stessa direzione
5–10 minuti avanti
viaggio con bambini
accetta saluti e domande
```

Tutti i campi sono volontari o derivati in forma ampia.

## Saluto

State machine:

```text
NONE
-> SENT
-> RECEIVED
-> RECIPROCATED
-> EXPIRED or BLOCKED
```

Il saluto:

- non apre chat da solo;
- ha rate limit;
- può essere disattivato;
- non espone posizione più precisa;
- può scadere.

## Chat pseudonima

“Anonima” verso gli altri non significa anonima verso la piattaforma. L'account
deve essere moderabile e bloccabile.

Profilo minimo:

- pseudonimo;
- provincia del cuore opzionale;
- tipo di viaggio;
- interessi;
- affidabilità;
- nessun dato obbligatorio non necessario.

## Tipi di conversazione

### Incontro

Chat 1:1 dopo consenso.

### Domanda di strada

Richiesta temporanea a un gruppo compatibile. Scade e limita destinatari.

### Carovana

Gruppo temporaneo con scopo e durata.

### Luogo

Conversazione legata a un campeggio o POI, con moderazione e possibile ritardo
per non rivelare presenza live.

## Messaggio

Modello:

```text
message id
conversation id
sender pseudonymous id
client operation id
created time
server accepted time
content kind
body or attachment reference
safety metadata
moderation state
```

## Consegna

```text
foreground -> websocket/event stream
background -> push signal
always -> durable server state + local sync
```

Semantica iniziale consigliata: at-least-once transport con deduplicazione tramite
ID. Non promettere exactly-once end-to-end.

## Outgoing queue

Stati:

```text
DRAFT
QUEUED
SENDING
ACCEPTED
DELIVERED optional
FAILED_RETRYABLE
FAILED_PERMANENT
```

Un messaggio vocale dettato deve essere visibile come queued se non c'è rete.

## Driving interaction policy

Input:

- ruolo dichiarato;
- stato veicolo;
- Android Auto/CarPlay;
- preferenze;
- priorità conversazione;
- manovra imminente;
- quiet mode.

Capability conducente:

```text
RECEIVE
READ_ALOUD
VOICE_REPLY
QUICK_REACTION
SAVE_FOR_LATER
MUTE
STRUCTURED_ROAD_QUESTION
```

Capability passeggero:

```text
FULL_TEXT
MEDIA
PROFILE
GROUP_MANAGEMENT
SEARCH
```

## Sintesi di più risposte

Una RoadQuestion può ricevere molte risposte. La sintesi deve separare fatti e
opinioni:

```text
3 risposte:
- 2 segnalano parcheggio pieno
- 1 consiglia l'area successiva
```

Non inventare consenso. Conservare accesso alle risposte originali da fermi.

## DNA di viaggio

Il profilo completo resta privato. Il matching può usare:

- categorie consentite;
- embedding o sketch locali futuri;
- preferenze esplicite;
- contesto del viaggio;
- dati recenti.

Prima versione: regole trasparenti, non modello opaco.

## Cartolina DNA

Contenuto:

- luogo ampio;
- stile del viaggio;
- consiglio;
- data/freschezza;
- audience;
- foto opzionale ripulita;
- provenance visita;
- scadenza/revoca.

## Fiducia

Indicatori:

- visita verificata senza mostrare traccia;
- recente;
- confermato da più utenti;
- DNA simile;
- fonte editoriale;
- contenuto sponsorizzato distinto;
- segnalazioni.

Evitare un unico punteggio misterioso.

## Moderazione e abuso

Abuse stories:

- spam di saluti;
- domande ripetute a centinaia di persone;
- stalking attraverso pattern di presenza;
- identità offensive;
- phishing in chat;
- immagini inappropriate;
- coordinate private inviate;
- account multipli;
- molestie dopo blocco;
- falsificazione posizione.

Contromisure:

- rate limit;
- reputazione;
- cooldown;
- block graph;
- report;
- content scanning appropriato;
- link restrictions;
- age policy;
- detection di posizione anomala;
- retention ridotta;
- audit interno access-controlled.

## Minori

Per MVP:

- account adulti;
- i bambini possono essere una preferenza di viaggio generica;
- nessun profilo bambino;
- nessun contatto diretto con minori;
- niente bus scolastico come primo caso d'uso.

## Cifratura

Valutare separatamente:

- TLS in transito;
- encryption at rest;
- end-to-end encryption;
- moderazione e report;
- recupero account;
- multi-device.

Non dichiarare E2EE finché protocollo, threat model e metadata leakage non sono
documentati e testati.

## Test

- expiry presence;
- blocco prima del matching;
- saluto non ricambiato;
- dedup messaggio;
- offline queue;
- voice reply;
- rate limit;
- revoca Cartolina;
- spoof location;
- privacy cell edge;
- account delete;
- report workflow.
