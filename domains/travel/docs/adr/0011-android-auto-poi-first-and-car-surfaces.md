# ADR-0011 — Android Auto POI-first e superfici auto separate

- **Stato:** Accepted
- **Data:** 2026-07-19
- **Issue:** [#29](https://github.com/kinderp/tdna/issues/29)
- **Base verificata:** `6198bd0a6c5a89f0e32fa218015125b5d452703e`

## Contesto

Travel DNA possiede già contratti provider-neutral per routing, replay di posizione,
map matching, route progress e missed exit. L'app Android del Pilot 0 usa Jetpack
Compose ed è stata verificata su un emulatore Android, ma non dichiara supporto per
Android Auto.

Android Auto non proietta liberamente la UI del telefono. Le app che operano sul
display dell'auto devono appartenere a una categoria supportata e, per le categorie
template-based, usare Android for Cars App Library. L'host automotive controlla
layout, limiti di interazione e modalità di input per ridurre la distrazione.

Le categorie plausibili per Travel DNA sono:

```text
androidx.car.app.category.POI
androidx.car.app.category.NAVIGATION
```

Una dichiarazione `NAVIGATION` implica una vera esperienza turn-by-turn: gestione
della sessione di navigazione, trip/step/destination, ricalcolo, focus di
navigazione, guida audio, intent di navigazione e simulazione `AUTO_DRIVE` per la
review. Il Pilot 1, invece, prevede ancora navigatori esterni come autorità delle
manovre.

Policy e API sono state ricontrollate il 19 luglio 2026 sulle fonti ufficiali
Android. Devono essere ricontrollate prima di ogni spike, implementazione o invio a
Google Play.

## Decisione

### 1. Android Auto è una superficie separata

Travel DNA avrà almeno tre renderer distinti:

```text
shared TDNA domain
├── mobile Compose renderer
├── Android Auto Car App Library renderer
└── Android Automotive OS renderer/package
```

Il renderer mobile non viene riutilizzato come layout della head unit. Le schermate
sociali, diario, chat completa, profili, fotografie, configurazione complessa e
inserimento di testo restano sul telefono.

### 2. Pilot 1 adotta una strategia POI-first

La prima esperienza Android Auto candidata appartiene alla categoria `POI` e può
mostrare soltanto funzioni coerenti con tale categoria, per esempio:

- tappe salvate o prossime;
- parcheggi, aree di sosta e luoghi utili;
- dettagli brevi di un luogo;
- selezione di una destinazione;
- handoff a un navigatore esterno supportato;
- azioni brevi e driver-safe.

Non dichiara TDNA come autorità turn-by-turn e non usa il canale audio di navigazione
per contenuti generici.

### 3. La categoria Navigation è differita

Travel DNA potrà dichiarare `androidx.car.app.category.NAVIGATION` solo dopo che una
slice dedicata dimostrerà almeno:

- route canonica e guidance affidabili;
- posizione e map matching runtime;
- route progress, manovra corrente e ETA;
- missed-exit e reroute;
- lifecycle start/stop e perdita del focus;
- `Trip`, `Step`, `Destination` e travel estimates corretti;
- guida audio con usage/focus appropriati;
- navigation intents;
- `NavigationManagerCallback.onAutoDriveEnabled()`;
- test DHU e checklist car-app-quality applicabile;
- field audit separato dalla sola simulazione.

La disponibilità di una simulazione o di una mappa non è sufficiente per dichiarare
la categoria Navigation.

### 4. Il core espone uno snapshot driver-safe

Il Pilot 0 v0.3 introduce o prepara un contratto provider-neutral, denominato
provvisoriamente `DriverJourneySnapshot`, che contiene solo lo stato necessario a
renderer mobile e automotive:

```text
journey state
destination summary
current/next step
distance to next step
distance and duration remaining
ETA when available
progress
rerouting/degraded state
```

Il contratto non importa:

```text
androidx.car.app
Compose
Android Location
MapLibre
Google Maps
Car App templates
```

Gli adapter trasformano lo snapshot nei tipi della piattaforma. Il futuro adapter
Android Auto mapperà lo stato verso template e tipi Car App Library; il dominio non
conosce `NavigationTemplate`, `MapWithContentTemplate` o `NavigationManager`.

### 5. Replay deterministico come base AUTO_DRIVE

Il replay sintetico della v0.3 resta una funzione didattica e di test sul telefono,
ma deve poter alimentare lo stesso snapshot driver-safe. In una futura Navigation
beta potrà essere usato dall'implementazione di `onAutoDriveEnabled()` per simulare
in modo ripetibile una navigazione richiesta dall'host.

Questo riuso non autorizza anticipatamente la categoria Navigation.

### 6. Android Auto e Android Automotive OS restano distinti

Android Auto usa l'app installata sul telefono e una head unit host. Android
Automotive OS esegue un'app installata nel veicolo e richiede packaging, manifest,
distribuzione e test specifici.

Codice di dominio e mapping possono essere condivisi, ma non si assume un unico
artifact o una sola matrice di test.

### 7. Testing a livelli separati

```text
Android emulator
    app mobile, Compose e instrumentation

Desktop Head Unit (DHU)
    Android Auto, template, input, day/night, focus e display auto

Android Automotive OS emulator
    app installata nel sistema del veicolo

telefono + veicolo reale
    compatibilità e comportamento reale

field test
    batteria, guida, affidabilità e sicurezza operativa
```

Un livello verde non sostituisce quello successivo.

## Conseguenze positive

- Il Pilot 0 v0.3 prepara Android Auto senza aggiungere dipendenze automotive.
- Il Pilot 1 può offrire valore POI senza fingere di essere un navigatore.
- Il dominio resta riutilizzabile su telefono, Android Auto, Automotive OS e iOS.
- La futura simulazione `AUTO_DRIVE` riusa fixture e replay deterministici.
- UI e funzionalità distraenti restano fuori dalla superficie di guida.
- Le categorie dichiarate corrispondono alle capacità realmente implementate.

## Costi e conseguenze negative

- Serviranno renderer, test e packaging separati.
- Una car experience non può mostrare tutto il prodotto.
- DHU e checklist automotive aggiungono un nuovo gate di CI/manual testing.
- La categoria Navigation arriverà più tardi della prima integrazione automotive.
- Le policy Android for Cars sono esterne e devono essere ricontrollate nel tempo.

## Alternative considerate

### Proiettare la UI Compose del telefono

Rifiutata. Non corrisponde al modello Car App Library e non garantisce driver
safety, template compliance o input automotive.

### Dichiarare subito Navigation

Rifiutata. Il Pilot 1 usa un navigatore esterno come autorità delle manovre e non
possiede ancora tutti i contratti e lifecycle richiesti.

### Rinviare ogni decisione automotive

Rifiutata. Senza uno snapshot condiviso la v0.3 rischierebbe di legare replay e
progress esclusivamente alla UI mobile, causando rework successivo.

### Creare subito un modulo Car App Library

Rinviata. Prima serve il contratto driver-safe e una slice automotive readiness
separata, con scope e test propri.

## Piano conseguente

```text
Pilot 0 v0.3
    replay/progress interattivi + DriverJourneySnapshot

Pilot 0 v0.4
    missed-exit/reroute + degraded state driver-safe

Automotive readiness spike
    Car App Library, CarAppService, template statico, DHU locale

Pilot 1
    POI companion + handoff a navigatore esterno

Internal Navigation Beta
    Navigation category, guidance, AUTO_DRIVE, voice, cluster e review
```

## Guardrail

1. Nessuna dipendenza `androidx.car.app` nei moduli shared.
2. Nessuna categoria car nel manifest di produzione prima della relativa slice.
3. Nessuna chat libera, feed, foto o configurazione complessa sul display auto.
4. Permessi, login e configurazione primaria vengono completati sul telefono quando
   la head unit non può gestirli in modo sicuro.
5. Evidenza DHU, Automotive OS e veicolo reale viene registrata separatamente.
6. Lo stato `road_evidence` resta falso nelle simulazioni e nei test host.
7. Prima di Google Play si ricontrollano categorie, quality tiers e criteri correnti.

## Fonti ufficiali verificate

- Android for Cars App Library:
  <https://developer.android.com/training/cars/apps/library>
- Point of Interest apps:
  <https://developer.android.com/training/cars/apps/poi>
- Navigation apps e AUTO_DRIVE:
  <https://developer.android.com/training/cars/apps/navigation>
- Android Auto platform behavior:
  <https://developer.android.com/training/cars/platforms/android-auto>
- Desktop Head Unit:
  <https://developer.android.com/training/cars/testing/dhu>
- Car app quality:
  <https://developer.android.com/docs/quality-guidelines/car-app-quality>
- Android Automotive OS templated apps:
  <https://developer.android.com/training/cars/apps/automotive-os>

## Revisit conditions

Rivedere questo ADR se:

- Google modifica categorie o quality tiers applicabili;
- la categoria POI non copre il valore minimo del Pilot 1;
- un provider esterno offre un contratto automotive più profondo e verificabile;
- la Navigation beta soddisfa tutti i gate elencati;
- Android Automotive OS richiede una separazione architetturale maggiore;
- CarPlay impone un contratto condiviso incompatibile con lo snapshot scelto.
