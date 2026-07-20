# Navigatori esterni e superfici automotive

## Stato

`foundation complete for provider boundaries; automotive direction accepted by ADR-0011`

Travel DNA non presume che l'utente abbandoni un navigatore affidabile. La modalità
Companion con navigatore esterno è una funzione primaria. Le superfici automotive
sono renderer separati e devono rispettare categorie, template e quality policy
della piattaforma.

## Decisione di prodotto

```text
Navigatore esterno
    svolte, traffico, corsie, ricalcolo

Travel DNA mobile
    viaggio, diario, guida, presenza, DNA, chat e memoria

Travel DNA Android Auto POI
    tappe, luoghi utili, stato breve e handoff

Travel DNA Navigation futura
    turn-by-turn soltanto dopo guidance e car quality gates
```

Il navigatore interno resta una possibilità strategica e deve guadagnarsi la
scelta attraverso affidabilità, evidenza e valore turistico.

## Tre modalità di navigazione

### 1. Handoff a navigatore esterno

Travel DNA può aprire:

- Waze;
- Google Maps;
- Sygic o altri navigatori disponibili;
- selettore di sistema.

Capability candidate:

- destinazione;
- origine opzionale;
- waypoint quando supportati;
- travel mode;
- preferenze limitate;
- avvio navigazione;
- fallback a browser o store;
- return link quando realmente disponibile.

L'adapter verifica disponibilità e dichiara soltanto capability osservabili.

### 2. Navigazione incorporata tramite SDK

Provider futuri possono includere Google Navigation SDK, Sygic Maps SDK o altri
SDK commerciali. L'utente resta dentro Travel DNA, ma routing e guidance dipendono
dal provider.

Ogni spike documenta:

- licenza e costo;
- termini e telemetria;
- dimensione SDK;
- lock-in;
- supporto offline;
- mapping nel modello canonico;
- compatibilità Android/iOS e automotive.

### 3. Navigazione open source

Baseline strategica:

```text
MapLibre
+ Valhalla
+ Ferrostar
+ componenti TDNA sostituibili
```

Questa direzione non entra nel Pilot 0 e non è necessaria per il primo spike
Android Auto POI, che usa una mappa resa dall'host.

## Provider esterni

### Waze

I deep link pubblici possono aprire app o web e avviare ricerca/navigazione con i
parametri supportati.

Vantaggi:

- integrazione semplice;
- esperienza Waze conservata;
- nessun motore Waze incorporato;
- buon fallback iniziale.

Limiti:

- TDNA non entra nella UI di Waze;
- nessun feed affidabile di route, manovre o ETA dettagliati;
- waypoint e interoperabilità limitati;
- integrazioni profonde richiedono programmi specifici.

### Google Maps

Maps URLs offrono sintassi cross-platform per ricerca e indicazioni. Le opzioni
native possono avviare l'app e, sui dispositivi mobili, la navigazione.

Limiti:

- un URL non restituisce la route a TDNA;
- lunghezza e comportamento differiscono per piattaforma;
- TDNA non sovrappone UI dentro Maps;
- Navigation SDK è un prodotto distinto con condizioni proprie.

### Sygic

Può essere valutato come handoff esterno o SDK incorporato. Le capacità vengono
dichiarate dopo verifica del contratto disponibile, non dedotte dal marketing.

## Percorso ombra

Quando il navigatore esterno è autorità, TDNA può calcolare una route indicativa
per:

- corridor dei POI;
- aree di servizio;
- suggerimenti turistici;
- diario e classificazione delle soste;
- compagni nella stessa direzione.

Non usa la route ombra per dare manovre al conducente.

### Confidenza

```text
HIGH
    posizione coerente, destinazione nota, route plausibile

MEDIUM
    alternative parallele o piccole deviazioni

LOW
    percorso esterno probabilmente diverso

UNKNOWN
    dati insufficienti
```

Policy:

- `HIGH`: suggerimenti temporali più precisi;
- `MEDIUM`: margine dichiarato;
- `LOW`: sola vicinanza geografica;
- `UNKNOWN`: sospensione delle anticipazioni di percorso.

## Avvio sessione Companion

```text
utente configura viaggio
-> TDNA salva TripSession
-> attiva recorder secondo permessi
-> calcola route ombra
-> apre navigatore esterno
-> mantiene stato locale bounded
-> ritorno a TDNA quando disponibile e sicuro
```

Servizi e processi devono essere avviati in stati consentiti dalla versione Android
corrente. Il comportamento reale viene verificato per OS e dispositivo.

## Conducente, passeggero e chat

### Conducente

- nessuna chat completa;
- eventuale lettura/risposta vocale solo in categorie e policy ammesse;
- quick action bounded;
- salva per dopo;
- silenzia;
- modalità conservativa quando il ruolo è sconosciuto.

### Passeggero

Sul telefono può usare chat completa, foto, Cartoline DNA, gruppi e configurazione
viaggio. La car surface non diventa una scorciatoia per proiettare queste funzioni.

## Android Auto

La decisione normativa è in:

- [ADR-0011](../adr/0011-android-auto-poi-first-and-car-surfaces.md)
- [capitolo 61](61-android-auto-compliance-e-roadmap-automotive.md)

Android Auto usa un host che rende template. TDNA non controlla un layout arbitrario
“Waze + Travel DNA + Spotify”.

### Pilot 1: categoria POI

```text
androidx.car.app.category.POI
```

Use case candidato:

```text
viaggio configurato sul telefono
-> lista breve di tappe o luoghi utili
-> dettaglio bounded
-> scelta destinazione
-> handoff al navigatore esterno
```

Baseline del primo spike:

- `PlaceListMapTemplate`;
- mappa resa dall'host;
- permesso `androidx.car.app.MAP_TEMPLATES` nel prototipo;
- nessun SDK cartografico;
- Desktop Head Unit prima del veicolo;
- nessuna categoria Navigation.

### Navigation futura

```text
androidx.car.app.category.NAVIGATION
```

Entra soltanto dopo route/guidance runtime, map matching, progress, reroute,
`NavigationManager`, trip/step/destination, audio guidance, navigation intents,
`onAutoDriveEnabled()`, DHU matrix, vehicle test e car quality review.

La simulazione deterministica può alimentare AUTO_DRIVE, ma non è road evidence.

### Permessi

Il pilot preferisce completare consenso e configurazione sul telefono prima della
partenza. Quando una car app usa `CarContext.requestPermissions()`, il dialog di
Android Auto viene comunque presentato sul telefono. Nessun flusso deve indurre il
conducente a guardarlo mentre guida.

### Messaging

Una futura esperienza messaging deve appartenere a una categoria supportata e
rispettare i relativi template/notifiche. Non viene inclusa automaticamente nella
car app POI del Pilot 1.

## Android Automotive OS

Android Automotive OS esegue un'app installata nel veicolo. Può riusare dominio e
mapping, ma richiede:

- manifest e package automotive;
- emulator/device matrix dedicata;
- distribuzione e trusted source applicabili;
- lifecycle e permessi specifici;
- evidenza distinta dal DHU Android Auto.

Non si assume un unico artifact mobile/AAOS.

## CarPlay

CarPlay resta una milestone successiva. Possibili superfici dipendono da entitlement
e categorie Apple correnti. TDNA non assume che la strategia Android Auto possa
essere copiata senza un ADR e uno spike dedicati.

## Priorità audio

Ordine concettuale:

```text
critical navigation prompt
road safety alert
selected conversation
normal message
travel opportunity
```

La navigazione futura usa il canale audio dedicato solo per istruzioni di guidance.
Una manovra imminente può rinviare messaggi o opportunità. Nessun contenuto social
si presenta come prompt di navigazione.

## Stato di processo e consegna

Non basarsi su un WebSocket sempre vivo:

```text
server source of pending delivery
-> push wakes/notifies
-> local sync fetches durable message
-> foreground websocket reduces latency when available
```

Questo modello riguarda la consegna mobile/backend e non autorizza chat completa
sul display auto.

## Contratti provider

```kotlin
interface ExternalNavigationProvider {
    val descriptor: ProviderDescriptor
    suspend fun isAvailable(): Boolean
    suspend fun launch(request: NavigationHandoffRequest): HandoffResult
}
```

Capability candidate:

```text
DESTINATION
ORIGIN
WAYPOINTS
TRAVEL_MODE
AVOID_TOLLS
RETURN_LINK
```

Il futuro adapter automotive consuma uno snapshot driver-safe:

```kotlin
interface CarSurfaceProvider {
    val descriptor: ProviderDescriptor
    suspend fun publish(snapshot: DriverJourneySnapshot)
    fun actions(): Flow<CarAction>
}
```

Il nome e la firma sono candidati di roadmap. `DriverJourneySnapshot` non esiste
ancora e non deve dipendere da `androidx.car.app`.

## Test essenziali

### Navigatori esterni

- URL e intent encoding;
- app non installata;
- capability non supportata;
- waypoint rifiutati;
- ritorno dal navigatore;
- perdita rete;
- processo ucciso;
- nessun feed inventato.

### Android Auto POI

- categoria e manifest del prototipo;
- content limits e metadati distanza;
- mappa host-rendered;
- DHU touch/rotary/day/night;
- permission flow sul telefono;
- handoff;
- perdita connessione;
- nessun focus steal;
- nessun contenuto fuori categoria.

### Navigation futura

- start/stop/focus;
- AUTO_DRIVE;
- trip/step/destination;
- reroute;
- audio focus;
- navigation intents;
- cluster quando applicabile;
- car quality checklist.

## Errori comuni

- promettere split-screen controllato dall'app;
- chiamare un deep link “integrazione SDK”;
- assumere feed di ritorno dal navigatore;
- aprire TDNA sopra una manovra critica;
- mostrare UI completa al conducente;
- dichiarare Navigation perché esiste una mappa;
- usare il canale audio navigation per chat o suggerimenti;
- confondere DHU, AAOS emulator, veicolo e strada;
- dipendere dal WebSocket per consegna affidabile.

## Collegamenti

- [Android Auto compliance](61-android-auto-compliance-e-roadmap-automotive.md)
- [Roadmap Android-first](55-roadmap-android-first-e-pilot.md)
- [Protocollo Pilot 1](57-protocollo-pilot-stradale-android.md)
- [Privacy, sicurezza e guida](33-privacy-security-driving-safety.md)
- [ADR-0011](../adr/0011-android-auto-poi-first-and-car-surfaces.md)
