# OpenStreetMap e cartografia

## Prima distinzione: dati, rendering e servizi

OpenStreetMap è soprattutto un database geografico collaborativo. Non è un SDK
unico che offre automaticamente:

- mappa renderizzata;
- navigazione;
- traffico;
- ricerca illimitata;
- tile offline;
- testi editoriali;
- fotografie;
- garanzia di servizio.

Separare sempre:

```text
OSM data
-> pipeline / provider tile
-> renderer MapLibre

OSM road graph
-> routing engine Valhalla

OSM names and places
-> search/geocoder
```

## Che cosa può contenere OSM

A seconda della qualità locale:

- strade, direzioni e classificazioni;
- svincoli e numeri di uscita;
- corsie e turn restrictions;
- limiti di velocità;
- aree di servizio e sosta;
- ristoranti, bar e negozi;
- hotel, campeggi e aree camper;
- musei, borghi, monumenti e attrazioni;
- spiagge, sentieri e punti panoramici;
- accessibilità, orari, siti e contatti.

La presenza di un campo non è uniforme. Il modello Travel DNA deve conservare
`unknown` invece di inventare valori.

## Modello canonico dei luoghi

OSM usa tag aperti:

```text
tourism=camp_site
amenity=restaurant
tourism=museum
natural=beach
```

L'adapter normalizza:

```text
PlaceCategory.CAMPSITE
PlaceCategory.RESTAURANT
PlaceCategory.MUSEUM
PlaceCategory.BEACH
```

Conservare anche:

- riferimenti esterni;
- tag originali necessari;
- timestamp/freschezza;
- provenienza;
- licenza;
- qualità/confidenza;
- eventuali override editoriali separati.

## Identità del luogo

Un luogo Travel DNA ha un ID interno stabile:

```text
TravelDnaPlaceId
  -> OSM element reference
  -> Wikidata reference
  -> partner reference
  -> editorial reference
```

L'ID OSM non deve essere la chiave primaria dell'intero prodotto perché:

- elementi possono cambiare o essere sostituiti;
- un luogo può avere più fonti;
- contenuti editoriali e utenti hanno lifecycle diversi.

## Tile raster e vettoriali

### Raster

Immagini già renderizzate. Semplici da mostrare ma meno personalizzabili.

### Vettoriali

Geometrie e attributi renderizzati dal client. Vantaggi:

- stile Travel DNA;
- rotazione e zoom nitidi;
- selezione di layer;
- etichette e simboli dinamici;
- uso offline quando licenza e provider lo consentono.

Costi:

- pipeline tile;
- stile;
- font e sprite;
- caching;
- hosting;
- test su GPU e dispositivi.

## Server pubblici OSM

I server standard `tile.openstreetmap.org` sono best-effort e non hanno SLA. La
policy vieta bulk download e uso offline e richiede identificazione, caching e
attribuzione. Travel DNA non deve costruire un prodotto commerciale dipendente
da quei server.

Strategia:

```text
prototipo piccolo
-> provider OSM-derived conforme
-> astrazione TileSource
-> self-hosting o provider sostituibile
```

## Nominatim pubblico

Il servizio pubblico ha capacità limitata, massimo assoluto di una richiesta al
secondo per applicazione, richiede identificazione e vieta l'autocomplete client.
Non è il backend di ricerca di produzione.

Alternative:

- provider gestito;
- istanza Nominatim propria;
- Pelias o altro motore;
- indice POI Travel DNA;
- ricerca locale offline per regioni installate.

Tutto dietro `PlaceSearchPort`.

## MapLibre Native

MapLibre Native è il renderer iniziale:

- core C++;
- rendering di tile vettoriali accelerato GPU;
- Android e iOS;
- stile MapLibre;
- layer personalizzati.

Non offre da solo routing, ricerca o traffico.

### Integrazione Android

La mappa resta una `MapView` nativa persistente. Compose disegna HUD e pannelli
sopra la view. Evitare di ricreare la view a ogni transizione.

### Integrazione iOS

MapLibre usa UIKit; SwiftUI ospita la view con wrapper. La mappa resta persistente
tra gli aggiornamenti di stato.

## Map scene Travel DNA

Il dominio non produce layer MapLibre. Produce una scena dichiarativa:

```text
camera
route overlay
completed route range
upcoming maneuver
places
road companions
DNA traces
selected item
```

L'adapter traduce in source e layer.

Aggiornamenti:

```text
installBaseScene(route geometry, style, static layers)
applyProgress(segment index, fraction)
applyPresenceDelta(add/update/remove)
applyPlaceSelection(id)
```

Non ricostruire l'intera GeoJSON per ogni campione.

## Stili

Travel DNA può avere:

- `ExploreStyle`: ricco di luoghi e contesto;
- `DriveStyle`: semplificato e leggibile;
- `NightStyle`;
- `OfflineFallbackStyle`;
- accessibilità ad alto contrasto.

Lo stile è configurazione versionata e testata. Un aggiornamento remoto di stile
non deve cambiare logica di dominio.

## POI lungo il percorso

Non mostrare tutto. Pipeline:

```text
route corridor
-> candidate places
-> practical filters
-> DNA/editorial ranking
-> diversity and density control
-> small presentation set
```

Un POI viene aggiornato quando cambia corridoio o contesto, non a ogni GPS.

## Contenuti editoriali e media

OSM fornisce struttura geografica, non la narrazione. Fonti possibili:

- contenuti originali;
- partner editoriali;
- Wikidata per identificatori e dati strutturati;
- Wikimedia Commons per media con licenza per file;
- fonti turistiche pubbliche compatibili.

Ogni contenuto conserva provenance e licenza.

## Offline

Offline richiede un pacchetto coerente:

```text
vector tiles
style
sprites/fonts
place index
routing graph
guide content
version metadata
license notices
```

Problemi:

- dimensione;
- aggiornamenti delta;
- spazio;
- compatibilità schema;
- scadenza;
- licenze;
- download interrotto;
- regioni sovrapposte.

L'offline non è soltanto un pulsante “scarica mappa”.

## Qualità dei dati

Travel DNA deve mostrare:

- fonte;
- data di aggiornamento;
- visita recente;
- conferme utenti;
- eventuale conflitto;
- possibilità di segnalare errore mappa.

Non confondere “presente in OSM” con “verificato editorialmente”.

## Licenza ODbL

L'uso pubblico dei dati richiede attribuzione. Database derivati e dati aggiunti
richiedono una progettazione legale accurata. Separare:

```text
OSM-derived geographic database
editorial content
user-generated content
private journey data
social graph
```

La separazione è tecnica e legale. Prima della produzione serve review
specialistica; la documentazione non sostituisce consulenza legale.

## Errori comuni

- usare il tile server pubblico come CDN;
- implementare autocomplete su Nominatim pubblico;
- chiamare OSM “API del navigatore”;
- salvare tag OSM come unico modello dominio;
- mostrare ogni POI durante la guida;
- dimenticare attribuzione;
- distribuire tile offline senza diritto;
- perdere provenance quando si uniscono fonti.
