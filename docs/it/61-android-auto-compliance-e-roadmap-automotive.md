# Android Auto compliance e roadmap automotive di Travel DNA

## Stato

`decision-backed by ADR-0011; runtime implementation not started`

Questo capitolo definisce come Travel DNA può arrivare ad Android Auto senza
confondere l'app mobile con l'esperienza sul display dell'auto. Le regole Android
for Cars sono state ricontrollate il 19 luglio 2026; devono essere verificate di
nuovo prima di implementare, testare o pubblicare una car app.

## Obiettivi

Al termine dovresti saper spiegare:

1. perché Android Auto non è una modalità landscape della UI Compose;
2. quali categorie Android for Cars sono realistiche per Travel DNA;
3. perché il Pilot 1 è POI-first e non Navigation-first;
4. come la v0.3 prepara uno snapshot condiviso senza dipendenze automotive;
5. quali funzioni restano sul telefono;
6. come differiscono Android emulator, DHU, Automotive OS emulator e veicolo reale;
7. quali gate servono prima di dichiarare conformità o pubblicare su Google Play.

## Regola fondamentale

```text
app Android installata e verificata
!= app Android Auto
!= app Android Automotive OS
!= navigatore turn-by-turn conforme
!= prova su strada
```

Android Auto usa un host che rende template e applica limiti di interazione. Travel
DNA fornisce stato, contenuto e azioni compatibili; non controlla liberamente la
composizione grafica della head unit.

## Categorie applicabili

Le categorie supportate cambiano nel tempo. Al controllo del 19 luglio 2026,
Navigation e Point of Interest sono supportate sia su Android Auto sia su Android
Automotive OS.

### Point of Interest

Categoria manifest futura:

```text
androidx.car.app.category.POI
```

È coerente con il primo valore automotive di TDNA:

- prossima tappa;
- luoghi salvati;
- aree di sosta, parcheggi o punti utili;
- informazioni brevi su un luogo;
- scelta della destinazione;
- handoff a un navigatore esterno;
- azioni rapide e bounded.

La categoria POI non trasforma TDNA nell'autorità delle manovre.

### Navigation

Categoria manifest futura:

```text
androidx.car.app.category.NAVIGATION
```

È riservata alla fase in cui TDNA possiede una vera navigazione turn-by-turn. Una
mappa, una posizione o un replay non bastano. Servono almeno:

- percorso e guidance canonici;
- manovra corrente e successiva;
- distanza, durata ed ETA;
- map matching runtime;
- missed exit e reroute;
- start/stop della navigazione;
- perdita e riacquisizione del focus;
- guida vocale con audio focus appropriato;
- navigation intents;
- simulazione `AUTO_DRIVE` richiesta per la review;
- template, cluster e quality checklist applicabili.

## Decisione di prodotto

```text
Pilot 1
    Android Auto POI companion
    + navigatore esterno come autorità delle manovre

Navigation Beta successiva
    TDNA turn-by-turn
    + categoria Navigation
```

Questa scelta massimizza il valore iniziale senza dichiarare capacità non ancora
possedute.

## Tre superfici, non una UI universale

```text
Telefono
    esperienza completa
    viaggio, diario, social, chat, configurazione, simulazione

Android Auto
    esperienza driver-safe e templated
    tappe, POI, stato viaggio, azioni brevi

Android Automotive OS
    applicazione installata nel veicolo
    packaging e test separati
```

Sul display auto non devono comparire feed sociale, chat libera, gallerie, profili
completi, diario da modificare, shopping complesso, account management, testo
libero o configurazione articolata dell'itinerario.

Queste funzioni restano sul telefono o sono disponibili soltanto quando la policy e
lo stato parked lo consentono.

## Architettura target

```text
shared domain
├── location replay
├── map matching
├── route progress
├── missed exit/reroute
└── driver journey contracts
        |
        ├── mobile Compose mapper
        ├── Android Auto mapper
        └── Automotive OS mapper
```

Il futuro adapter Car App Library vive nel lato Android:

```text
features/android-auto
├── TravelDnaCarAppService
├── TravelDnaCarSession
├── PoiScreen
├── PoiTemplateMapper
├── NavigationScreen             futuro
├── NavigationTemplateMapper     futuro
└── CarTripStateAdapter
```

I nomi sono candidati di roadmap, non file già esistenti.

## Contratto driver-safe della v0.3

La v0.3 deve rendere replay e route progress interattivi nell'app mobile e produrre
uno stato riutilizzabile. Nome provvisorio:

```kotlin
DriverJourneySnapshot
```

Campi candidati:

```text
state
selected scenario
destination summary
current sample outcome
current/next step
distance to next step
distance remaining
duration remaining
ETA when available
progress
rerouting/degraded state
```

Lo snapshot deve essere immutabile, provider-neutral, bounded e trasformabile in
evidenza deterministica. Non deve importare:

```text
androidx.car.app
android.location
androidx.compose
MapLibre
Google Maps
NavigationTemplate
```

## Perché il replay prepara AUTO_DRIVE

Una futura Navigation app deve poter simulare la navigazione quando l'host richiama
`NavigationManagerCallback.onAutoDriveEnabled()`.

```text
AUTO_DRIVE
-> fixture sintetica versionata
-> replay deterministico
-> map matching
-> route progress
-> DriverJourneySnapshot
-> mapper Car App Library
-> Trip/Step/Destination update
```

La modalità AUTO_DRIVE futura usa dati sintetici, produce un esito ripetibile e
resta distinta da evidenza su veicolo e strada. Non autorizza la categoria
Navigation prima dei relativi gate.

## Roadmap aggiornata

### Pilot 0 v0.3 — replay/progress mobile e contratto condiviso

- start, pausa, step, reset e velocità;
- fixture sintetica nella schermata Demo;
- campione accettato/rifiutato;
- map matching e route progress dai moduli shared;
- timeline diagnostica bounded;
- lifecycle documentato;
- `DriverJourneySnapshot` o contratto equivalente;
- nessuna dipendenza Car App Library;
- nessun GPS o permesso reale.

### Pilot 0 v0.4 — missed exit e degraded state

- deviazione sintetica;
- conferma missed-exit;
- reroute deterministico;
- loading/error/degraded state;
- messaggi brevi compatibili con una futura car experience;
- nessuna guida reale.

### Pilot 0 v0.5 — navigatori esterni

- capability model;
- handoff a navigatore esterno;
- fallback app non disponibile;
- ritorno a Travel DNA;
- nessun feed di route inventato dal provider esterno.

### Automotive readiness spike

Slice separata, successiva ai contratti driver-safe:

- Car App Library solo nel modulo Android dedicato;
- `CarAppService` e `Session` minimi;
- categoria POI soltanto nel prototipo;
- `PlaceListMapTemplate` come baseline iniziale;
- mappa resa dall'host, senza introdurre MapLibre nello spike;
- permesso manifest `androidx.car.app.MAP_TEMPLATES` nel prototipo;
- contenuto sintetico, limiti lista e metadati distanza verificati;
- connessione DHU locale;
- day/night, touch e rotary smoke;
- nessuna pubblicazione e nessuna categoria Navigation.

### Pilot 1 — POI companion controllato

- viaggio configurato sul telefono;
- posizione foreground e recorder bounded sul telefono;
- lista breve di tappe/POI sul display auto;
- destinazione selezionabile;
- handoff a navigatore esterno;
- stop/pause essenziali;
- nessuna chat completa;
- nessuna configurazione complessa;
- nessuna autorità turn-by-turn TDNA.

### Internal Navigation Beta

Entra soltanto dopo evidence gate dedicati:

- route e guidance runtime;
- `NavigationManager` lifecycle;
- `Trip`, `Step`, `Destination` e travel estimates;
- `NavigationTemplate` o template correnti ammessi;
- `onAutoDriveEnabled()` alimentato dal replay;
- audio guidance;
- navigation intents;
- cluster support quando richiesto;
- DHU matrix;
- test su veicolo e field audit;
- quality review checklist e distribuzione separata.

## Template e mappa

### Baseline POI

La prima scelta è `PlaceListMapTemplate` perché mostra una lista di luoghi insieme
a una mappa resa dall'host. Questo consente di testare categoria, contenuto e
lifecycle senza aggiungere un SDK cartografico.

Il prototipo deve dichiarare:

```xml
<uses-permission android:name="androidx.car.app.MAP_TEMPLATES" />
```

Le righe devono rispettare content limit, numero di linee, marker e distanza
richiesti dal template corrente.

### Fase con mappa fornita dall'app

`MapWithContentTemplate` entra soltanto quando TDNA possiede un renderer di mappa e
ha scelto una versione Car API compatibile. Richiede anch'esso il permesso template
appropriato e non è necessario per il primo spike POI.

### Navigation futura

```text
NavigationTemplate
MapWithContentTemplate, quando applicabile
```

Template deprecati non devono essere introdotti copiando esempi vecchi.

## Permessi e configurazione

La configurazione principale resta sul telefono:

```text
telefono
-> privacy e consenso
-> permessi
-> selezione viaggio
-> preferenze

Android Auto
-> esperienza già pronta
-> azioni brevi
-> richiesta CarContext che presenta il consenso sul telefono, quando necessaria
```

Il pilot preferisce completare setup e consenso prima della partenza. L'utente non
deve essere indotto a guardare il telefono durante la guida.

## Audio e voce

La futura guida vocale Navigation deve richiedere audio focus, usare
`USAGE_ASSISTANCE_NAVIGATION_GUIDANCE`, adottare un focus transitorio compatibile
con il ducking e fermarsi quando l'host termina la navigazione. Chat o opportunità
sociali non possono usare il canale come falsi prompt di guida.

## Testing

### Android emulator corrente

Dimostra app mobile, Compose e instrumentation. Non mostra Android Auto.

### Desktop Head Unit

Il DHU emula una head unit Android Auto su Windows, macOS o Linux. La futura suite
verifica apertura, template, touch, rotary, giorno/notte, aspect ratio, focus quando
applicabile, screenshot e log bounded.

### Android Automotive OS emulator

Serve per l'app installata nel sistema del veicolo. Non sostituisce il DHU e
richiede manifest, package e distribuzione dedicati.

### Veicolo reale

Verifica compatibilità con head unit, connessione e comportamento reale. Non
sostituisce il field test di batteria, usabilità e sicurezza.

## Quality gate

Prima di una submission car:

- categoria realmente supportata e contenuto coerente;
- stato ripristinato quanto più possibile dopo rilancio;
- caricamento entro il limite corrente, oggi 10 secondi per le categorie
  car-optimized applicabili;
- contrasto e tema chiaro/scuro quando personalizzati;
- screenshot reale, non alterato e ad alta risoluzione;
- task flow brevi;
- nessuna funzione non consentita durante la guida;
- checklist categoria-specifica;
- DHU e, quando applicabile, Automotive OS;
- review manuale Google Play nel piano di release.

La quality policy è esterna e può cambiare. I numeri presenti sono una fotografia
del 19 luglio 2026, non un contratto permanente.

## Packaging e distribuzione

Per i primi spike è consigliato evitare di bloccare l'APK di produzione con la
review automotive. La scelta fra application ID, product flavor o modulo separato
viene presa nella slice automotive readiness.

```text
mobile production app
    nessuna categoria car finché non pronta

automotive prototype/internal flavor
    categoria POI + MAP_TEMPLATES
    distribuzione interna

Automotive OS app
    artifact separato
```

## Rischi e contromisure

| Rischio | Contromisura |
| --- | --- |
| UI mobile proiettata sulla car | Renderer Car App Library separato. |
| Categoria Navigation dichiarata presto | POI-first e gate espliciti. |
| Android types nel core | Snapshot provider-neutral e architecture check. |
| SDK mappa anticipato | `PlaceListMapTemplate` host-rendered nello spike. |
| Feature social distraenti | Allowlist delle funzioni auto. |
| Permission flow sulla head unit | Setup sul telefono e `CarContext` quando necessario. |
| Test emulatore scambiato per car evidence | DHU/AAOS/vehicle evidence separata. |
| Policy Google cambia | Recheck datato prima di ogni slice/release. |
| Review car blocca release mobile | Prototipo/flavor/package separato da decidere. |
| AUTO_DRIVE scambiato per strada | `road_evidence=false` e field gate separato. |

## Criteri per aprire la slice automotive readiness

- v0.3 produce snapshot deterministico e testato;
- v0.4 produce reroute/degraded state testato;
- POI use case ridotto e approvato;
- nessuna PR precedente aperta;
- policy Android for Cars ricontrollata;
- versione Car App Library definita;
- DHU disponibile;
- scope esclude Navigation e Play submission.

## Fonti ufficiali

- App Library: <https://developer.android.com/training/cars/apps/library>
- POI: <https://developer.android.com/training/cars/apps/poi>
- Navigation: <https://developer.android.com/training/cars/apps/navigation>
- Permessi: <https://developer.android.com/training/cars/apps/library/request-permissions>
- Android Auto: <https://developer.android.com/training/cars/platforms/android-auto>
- Desktop Head Unit: <https://developer.android.com/training/cars/testing/dhu>
- Android Automotive OS: <https://developer.android.com/training/cars/apps/automotive-os>
- Car app quality: <https://developer.android.com/docs/quality-guidelines/car-app-quality>

## Collegamenti interni

- [ADR-0011](../adr/0011-android-auto-poi-first-and-car-surfaces.md)
- [Roadmap Android-first](55-roadmap-android-first-e-pilot.md)
- [Navigatori esterni e automotive](25-navigatori-esterni-e-automotive.md)
- [Protocollo Pilot 1](57-protocollo-pilot-stradale-android.md)
- [Replay deterministico](46-location-sample-e-replay-deterministico.md)
- [Route progress](47-posizione-matched-e-route-progress.md)
- [Missed exit e reroute](49-off-route-missed-exit-e-reroute.md)
- [Privacy e sicurezza durante la guida](33-privacy-security-driving-safety.md)
