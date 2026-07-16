# Navigatori esterni e superfici automotive

## Decisione di prodotto

Travel DNA non presume che l'utente abbandoni un navigatore affidabile. La
modalità Companion con navigatore esterno è una funzione primaria.

```text
Navigatore esterno
  -> svolte, traffico, corsie, ricalcolo

Travel DNA
  -> diario, guida, presenza, DNA, chat, memoria e suggerimenti
```

Il navigatore interno resta una possibilità strategica e deve guadagnarsi la
scelta attraverso fluidità, affidabilità e valore turistico.

## Tre modalità

### 1. Handoff a navigatore esterno

Travel DNA apre:

- Waze;
- Google Maps;
- Sygic o altri navigatori disponibili;
- selettore di sistema.

Capacità possibili:

- destinazione;
- origine opzionale;
- waypoint, quando supportati;
- preferenze limitate;
- avvio navigazione;
- fallback a browser/store.

L'adapter verifica disponibilità e dichiara le capability reali.

### 2. Navigazione incorporata tramite SDK

Provider futuri:

- Google Navigation SDK;
- Sygic Maps SDK;
- altri SDK commerciali.

L'utente resta dentro Travel DNA, ma routing/guidance possono essere forniti dal
provider. Costi, licenze, termini, telemetria e lock-in devono essere documentati.

### 3. Navigazione open source

MapLibre + Valhalla + Ferrostar, con futura sostituzione progressiva dei componenti
strategici.

## Waze

I Waze Deep Link possono aprire l'app o il web e avviare ricerca/navigazione con
parametri supportati.

Vantaggi:

- integrazione semplice;
- mantiene l'esperienza Waze;
- nessun motore Waze incorporato;
- ottimo fallback iniziale.

Limiti:

- non inseriamo Travel DNA dentro la UI di Waze;
- non riceviamo normalmente route, manovre o ETA dettagliati;
- supporto waypoint e interoperabilità sono limitati;
- un'integrazione più profonda richiede programmi/partnership specifici.

Quindi un `WazeHandoffPlugin` implementa soltanto ciò che la documentazione
pubblica permette.

## Google Maps

Maps URLs offrono sintassi cross-platform per ricerca e indicazioni; le opzioni
native possono avviare l'app e, sui dispositivi mobili, la navigazione.

Vantaggi:

- ampia disponibilità;
- URL universali;
- fallback web;
- possibilità separata di Navigation SDK incorporato.

Limiti:

- un URL non fornisce un feed di route di ritorno;
- limite di lunghezza e differenze di piattaforma;
- non possiamo sovrapporre UI Travel DNA dentro Google Maps;
- l'SDK incorporato introduce costo e condizioni commerciali.

## Sygic

Sygic può essere valutato in due forme:

- handoff all'app esterna, se supportato dal contratto disponibile;
- SDK incorporato con routing, guidance e funzioni offline.

Vantaggi potenziali:

- esperienza di navigazione matura;
- funzioni offline;
- API orientate all'integrazione.

Rischi:

- licenza e costo;
- dipendenza dal provider;
- dimensione SDK;
- mapping nel modello canonico;
- compatibilità Android/iOS da testare.

## Percorso ombra

Quando il navigatore esterno è autorità, Travel DNA calcola una route indicativa.

Usi:

- corridor dei POI;
- prossime aree di servizio;
- compagni nella stessa direzione;
- suggerimenti di guida;
- diario e classificazione delle soste.

Non usa la route ombra per dire al conducente dove svoltare.

### Confidenza

```text
HIGH
  posizione coerente, destinazione nota, route plausibile

MEDIUM
  piccole deviazioni o alternative parallele

LOW
  percorso esterno probabilmente diverso

UNKNOWN
  dati insufficienti
```

Policy:

- `HIGH`: suggerimenti temporali precisi;
- `MEDIUM`: suggerimenti con margine;
- `LOW`: solo vicinanza geografica;
- `UNKNOWN`: sospendere anticipazioni di percorso.

## Avvio sessione Companion

```text
1. utente preme Parti
2. Travel DNA salva TripSession
3. attiva recorder secondo permessi
4. registra messaging/push
5. pubblica presenza secondo consenso
6. calcola route ombra
7. apre navigatore esterno
```

Il servizio di background necessario va avviato mentre l'app è in uno stato
consentito dalla piattaforma. Il comportamento reale deve essere verificato per
versione OS.

## Chat attiva

La chat non viene sospesa. Cambia superficie.

### Conducente

- lettura vocale;
- risposta vocale;
- quick reaction;
- salva per dopo;
- silenzia;
- domanda strutturata;
- nessuna lista lunga.

### Passeggero

- chat completa;
- foto e Cartoline DNA;
- ricerca compagni;
- gruppi e Carovana;
- gestione della guida.

### Stato sconosciuto

Adottare la modalità più conservativa. Non dedurre con certezza chi tiene il
telefono. Un viaggio condiviso con ruoli espliciti è preferibile.

## Android Auto

Android Auto usa un host che rende template e notifiche. Travel DNA non controlla
liberamente un layout “Waze + Travel DNA + Spotify”.

Superfici possibili:

### Messaging notifications

Una vera conversazione può usare notifiche `MessagingStyle`, azioni di risposta
e mark-as-read. Android Auto legge e raccoglie risposte vocali.

### POI app

Travel DNA può mostrare luoghi e opportunità con template approvati e inoltrare
la destinazione a un navigatore.

### Navigation app

Se il navigatore interno raggiunge i requisiti, può dichiararsi app di
navigazione e usare template dedicati.

### Widget e nuove superfici

Vanno trattati come progressive enhancement e verificati per disponibilità e
policy correnti. Non devono essere necessari al prodotto base.

## CarPlay

Possibili superfici:

- messaging/VoIP con SiriKit intents;
- navigation app con entitlement;
- widget e Live Activity;
- categorie approvate come parcheggio o fueling quando applicabili.

Travel DNA deve richiedere entitlement quando necessario e non assumere che una
UI arbitraria sia accettata.

## Priorità audio

Il sistema deve coordinare:

```text
critical navigation prompt
road safety alert
selected conversation
normal message
travel opportunity
```

Una manovra imminente può rinviare la lettura di un messaggio. La musica può
essere attenuata secondo policy e preferenze.

## Notifiche senza sistema auto

Con il telefono:

- il navigatore resta in primo piano;
- Travel DNA usa notification channel/conversation notification;
- assistente o TTS può leggere;
- reply action invia in background;
- una risposta non inviata resta in coda.

## Stato di processo e consegna

Non basarsi su un WebSocket sempre vivo. Modello:

```text
server is source of pending delivery
push wakes/notifies
local sync fetches durable message
foreground websocket reduces latency when available
```

## ExternalNavigationProvider

```kotlin
interface ExternalNavigationProvider {
    val descriptor: ProviderDescriptor
    suspend fun isAvailable(): Boolean
    suspend fun launch(request: NavigationHandoffRequest): HandoffResult
}
```

Capability:

```text
DESTINATION
ORIGIN
WAYPOINTS
TRAVEL_MODE
AVOID_TOLLS
RETURN_LINK
```

## CarSurfaceProvider

```kotlin
interface CarSurfaceProvider {
    val descriptor: ProviderDescriptor
    suspend fun publish(snapshot: CarCompanionSnapshot)
    fun actions(): Flow<CarAction>
}
```

## Test essenziali

- handoff URL encoding;
- app non installata;
- waypoint non supportati;
- ritorno da navigatore;
- Waze in primo piano + push chat;
- voice reply;
- perdita rete;
- processo ucciso;
- ruolo passeggero;
- Android Auto Desktop Head Unit;
- CarPlay simulator;
- nessun focus steal.

## Errori comuni

- promettere split-screen controllato dall'app;
- chiamare deep link “integrazione SDK”;
- assumere feed di ritorno dal navigatore;
- aprire Travel DNA sopra una manovra critica;
- disattivare la chat invece di adattarla;
- mostrare UI completa al conducente;
- dipendere dal WebSocket per consegna affidabile.
