# Routing e navigazione: come si costruisce un navigatore

## Obiettivo didattico

Un navigatore non è una singola funzione `navigate(origin, destination)`. È una
pipeline di dati, algoritmi, stato temporale, rendering e interazione. Questo
capitolo parte dai concetti di base.

## 1. Dalla mappa al grafo

Una strada può essere rappresentata come archi collegati da nodi.

```text
Nodo A ---- arco 1 ---- Nodo B ---- arco 2 ---- Nodo C
```

Un arco contiene proprietà:

- lunghezza;
- direzione;
- classe stradale;
- velocità stimata;
- pedaggio;
- accesso;
- restrizioni;
- superficie;
- numero di corsie;
- turn restrictions;
- tempo o validità condizionale.

Il grafo non è uguale alla grafica della mappa. Serve all'algoritmo.

## 2. Cost function

Il percorso più corto in metri non è sempre il migliore. Il router minimizza un
costo.

Esempio semplificato:

```text
cost = travel_time
     + toll_penalty
     + unpaved_penalty
     + difficult_turn_penalty
     + user_preference_penalty
```

Travel DNA aggiunge un ranking turistico, inizialmente a valle del router:

```text
candidate routes from provider
-> score scenic value
-> score family stops
-> score campsite availability
-> score DNA compatibility
-> present alternatives
```

Non modificare subito il cuore di Valhalla: prima dimostrare il valore con
post-processing spiegabile.

## 3. Dijkstra e A*

### Dijkstra

Esplora il grafo partendo dall'origine e trova il costo minimo. È corretto con
costi non negativi, ma può esplorare molto.

### A*

Aggiunge un'euristica `h(n)` che stima il costo dal nodo alla destinazione:

```text
f(n) = g(n) + h(n)
```

- `g(n)`: costo già percorso;
- `h(n)`: stima residua.

Se l'euristica è ammissibile, A* conserva l'ottimalità e riduce l'esplorazione.

La reference implementation Java serve a insegnare questi concetti su grafi
piccoli. Valhalla usa strutture e algoritmi molto più evoluti.

## 4. Valhalla

Valhalla è il routing engine iniziale. Offre:

- route;
- alternative;
- matrici;
- isocrone;
- map matching;
- elevation;
- costing dinamico;
- manovre e narrativa;
- supporto a dati di traffico esterni.

Moduli concettuali importanti:

- Mjolnir: costruzione tile del grafo;
- Loki: correlazione input-grafo;
- Thor: path finding;
- Sif: costing;
- Odin: manovre e narrativa;
- Meili: map matching;
- Tyr: servizio API.

### Vantaggi

- open source e self-hostable;
- ricco di funzioni;
- OSM;
- separazione interna istruttiva;
- personalizzabile;
- utilizzabile online o in scenari offline.

### Costi e rischi

- costruzione e aggiornamento delle graph tile;
- infrastruttura e memoria;
- configurazione complessa;
- dati live traffic non inclusi automaticamente;
- risposte da normalizzare;
- offline mobile da studiare separatamente.

Travel DNA chiama un proprio routing gateway, non il demo server pubblico.

## 5. Route canonica

Una route contiene:

```text
geometry
legs
maneuvers
distance
duration
road metadata
warnings
provenance
capabilities
```

La geometria può essere una polyline compressa o una sequenza. Internamente serve
un formato efficiente; la UI non deve ricrearla continuamente.

## 6. Posizione GPS

Un campione tipico contiene:

- latitudine/longitudine;
- timestamp;
- accuratezza orizzontale;
- velocità;
- bearing;
- altitude opzionale;
- fonte;
- mock/simulation metadata quando disponibile.

Il GPS è rumoroso. In galleria può mancare; in città può riflettersi tra edifici.
Non usare il punto come verità assoluta.

## 7. Filtering

Obiettivi:

- rifiutare campioni impossibili;
- ridurre jitter;
- mantenere risposta rapida;
- non nascondere deviazioni reali.

Tecniche candidate:

- soglie su accuratezza e velocità;
- smoothing;
- alpha-beta filter;
- Kalman filter;
- fusione con bearing e sensori OS;
- dead reckoning fornito dalla piattaforma.

Ogni tecnica ha trade-off latenza/stabilità. Deve essere testata su fixture.

## 8. Map matching

Domanda:

> Su quale strada o segmento è probabilmente il veicolo?

Input:

- campioni recenti;
- grafo;
- direzione;
- velocità;
- route attiva;
- accuratezza.

Output:

- posizione proiettata;
- segmento candidato;
- offset;
- confidenza;
- alternative.

Non confondere map matching con snap grafico. Lo snap della freccia è solo la
presentazione del risultato.

## 9. Route progress

Dato un segmento matched:

- indice lungo la route;
- frazione del segmento;
- distanza percorsa;
- distanza residua;
- leg e step;
- prossima manovra;
- ETA.

Invariante:

> Il progresso normale non deve oscillare avanti e indietro per rumore minimo.

Ma non deve impedire inversioni o deviazioni reali. Servono state machine e
soglie temporali.

## 10. Maneuver selection

Le manovre vengono prodotte dal route provider e il runtime seleziona quella
corrente e successiva.

Problemi:

- distanza di annuncio diversa per velocità;
- uscite ravvicinate;
- corsie;
- roundabout;
- nomi mancanti;
- tunnel;
- localizzazione linguistica;
- annunci già pronunciati.

La guidance mantiene stato per evitare ripetizioni inutili.

## 11. Voice guidance

Pipeline:

```text
maneuver event
-> localized instruction model
-> speech prompt scheduling
-> platform TTS
-> audio focus coordination
```

Separare testo visuale e testo parlato. Il parlato può richiedere forma più
naturale e timing diverso.

La chat vocale usa una coda o priorità diversa. Una manovra critica deve poter
interrompere o rinviare un messaggio sociale.

## 12. Off-route detection

Non basta una distanza fissa dalla linea.

Segnali:

- distanza dalla route;
- direzione incompatibile;
- persistenza nel tempo;
- accuratezza GPS;
- strada matched non appartenente alla route;
- possibilità di rampe parallele;
- progresso bloccato.

State machine candidata:

```text
ON_ROUTE
-> SUSPECTED_OFF_ROUTE
-> CONFIRMED_OFF_ROUTE
-> REROUTING
-> ON_NEW_ROUTE
```

Un campione cattivo non deve avviare ricalcolo.

## 13. Rerouting

Regole:

- un solo reroute attivo;
- cancellazione del precedente se la posizione cambia molto;
- route corrente mantenuta finché arriva un risultato valido;
- backoff in caso di provider indisponibile;
- UI con stato esplicito;
- applicazione atomica della nuova route;
- session snapshot persistibile.

## 14. Ferrostar

Ferrostar è un SDK di navigazione, non un routing engine o basemap. Offre un core
Rust, binding e UI componibili per iOS/Android, route provider estendibili,
progress e voice guidance.

### Vantaggi

- vendor-neutral;
- moderno;
- Rust core;
- Kotlin/Swift;
- UI componibile;
- pronto per prototipi di produzione mobile;
- possibilità di sostituire layer.

### Rischi

- etichetta beta;
- nessuna garanzia completa di stabilità API prima di 1.0;
- non sostituisce tile, ricerca e routing;
- integrazione e comportamento vanno verificati con replay;
- Travel DNA deve mantenere un adapter sopra il suo core.

## 15. MapLibre e la vista di guida

MapLibre disegna:

- mappa;
- route;
- traffico quando disponibile;
- puck;
- POI;
- compagni di strada;
- tracce DNA.

Il navigation runtime non manipola direttamente layer. Pubblica snapshot e delta.

## 16. Camera

La camera è una state machine propria:

- follow mode;
- overview;
- maneuver emphasis;
- user pan;
- recenter;
- route preview;
- north-up/heading-up.

Un tap o pan dell'utente sospende temporaneamente l'auto-follow. La comparsa di un
POI non deve causare zoom improvviso.

## 17. Traffico

Valhalla può consumare velocità e incidenti, ma Travel DNA deve procurare i dati.
Fasi possibili:

1. nessun live traffic, solo costi base;
2. provider commerciale;
3. dati pubblici/partner;
4. campioni anonimi utenti;
5. velocità storiche;
6. segnalazioni e confidenza.

Senza densità, non promettere equivalenza con Waze.

## 18. Routing turistico

Prima versione:

```text
provider generates alternatives
-> Travel DNA scores them
```

Feature:

- deviazione massima;
- valore panoramico;
- borghi;
- soste famiglia;
- aree camper;
- pause ogni N minuti;
- preferenze DNA;
- contenuti guida;
- stagionalità e apertura.

Spiegabilità:

```text
“+12 minuti: include un borgo e una sosta adatta ai bambini”
```

## 19. Offline

Livelli:

- route già calcolata e mappa cache;
- regione cartografica offline;
- search locale;
- graph routing locale;
- guidance locale;
- sincronizzazione successiva.

Non chiamare “offline navigation” una modalità che perde il ricalcolo senza
dichiararlo.

## 20. Prestazioni

Il loop di avanzamento deve:

- ricevere input compatto;
- evitare I/O;
- evitare allocazioni evitabili;
- non convertire geometrie complete;
- produrre snapshot piccolo;
- essere deterministico e benchmarkabile.

La mappa:

- mantiene instance e style;
- usa layer, non migliaia di view;
- aggiorna delta;
- seleziona pochi POI;
- usa profili per dispositivi meno potenti.

## 21. Test

- unit su geometria e state machine;
- property su coordinate e monotonicità;
- contract su provider;
- replay GPS;
- golden snapshot;
- UI test;
- benchmark;
- soak;
- field drive.

## 22. Primo laboratorio

Implementare:

1. piccolo grafo Java;
2. A*;
3. route canonica;
4. fixture GPS sintetica;
5. core Rust o fake guidance;
6. missed exit;
7. reroute;
8. snapshot osservabili;
9. confronto e benchmark.

## Errori comuni

- trattare il GPS come esatto;
- avviare reroute su un solo campione;
- ricostruire la route ogni frame;
- mescolare TTS chat e manovre senza priorità;
- dipendere dai tipi del provider;
- testare soltanto su strada;
- promettere traffico senza sorgente dati;
- iniziare dall'FFI prima del core deterministico.
