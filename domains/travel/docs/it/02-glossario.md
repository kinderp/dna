# Glossario di Travel DNA

Questo glossario definisce i termini prima che diventino classi, API o label.
Quando il codice introduce una parola nuova e stabile, aggiornare questo file.

## Prodotto e viaggio

**Viaggio (`Trip`)**
Esperienza pianificata o in corso, con partecipanti, tappe, preferenze e diario.

**Sessione di viaggio (`TripSession`)**
Periodo attivo in cui Travel DNA registra eventi autorizzati, mantiene presenza,
chat e contesto di percorso.

**Tappa (`Waypoint` o `PlannedStop`)**
Luogo pianificato nell'itinerario.

**Sosta (`DetectedStop`)**
Interruzione del movimento rilevata o dichiarata. Non equivale automaticamente a
una visita significativa.

**Momento (`JourneyMoment`)**
Unità narrativa del diario: un intervallo, un luogo, fotografie, pensieri e note.

**Pagina del giorno (`DailyPage`)**
Composizione modificabile dei momenti di una giornata.

**Taccuino (`Journal`)**
Insieme privato delle pagine e dei ricordi del viaggio.

**Opportunità di viaggio (`TravelOpportunity`)**
Luogo, sosta o esperienza pertinente al percorso e al DNA del viaggio.

## Socialità

**Compagno di strada (`RoadCompanion`)**
Utente rappresentato in forma approssimata nello stesso contesto di viaggio.

**Saluto (`Greeting`)**
Interazione leggera e non impegnativa che non apre da sola una chat.

**Domanda di strada (`RoadQuestion`)**
Richiesta temporanea e strutturata inviata a utenti compatibili in un corridoio.

**Carovana (`Caravan`)**
Gruppo temporaneo che condivide una parte del viaggio o una destinazione.

**DNA di viaggio (`TravelDnaProfile`)**
Preferenze private e contestuali del viaggio.

**Cartolina DNA (`DnaCard`)**
Frammento selezionato e condivisibile di esperienza.

**Traccia DNA (`DnaTrace`)**
Consiglio lasciato o ricevuto lungo un percorso o in un luogo.

**Presenza stradale (`RoadPresence`)**
Segnale breve, approssimato e limitato nel tempo che permette di scoprire
compatibilità senza pubblicare la posizione esatta.

## Cartografia

**OpenStreetMap (`OSM`)**
Database geografico collaborativo aperto. Non è sinonimo dei server gratuiti di
tile o ricerca gestiti dalla OpenStreetMap Foundation.

**Tile**
Porzione di mappa scaricata per un'area e un livello di zoom. Può essere raster o
vettoriale.

**Tile vettoriale**
Dati geometrici e attributi che il dispositivo rende secondo uno stile.

**Stile cartografico**
Regole che trasformano dati geografici in linee, simboli, colori ed etichette.

**POI (`Point of Interest`)**
Punto o area di interesse, per esempio ristorante, campeggio o museo.

**Geocoding**
Traduzione da testo o indirizzo a coordinate e luogo.

**Reverse geocoding**
Traduzione da coordinate a descrizione di luogo o indirizzo.

**Map matching**
Associazione di campioni GPS rumorosi alla strada o al percorso più plausibile.

## Routing e navigazione

**Grafo stradale**
Rappresentazione della rete con nodi, archi, direzioni, costi e restrizioni.

**Routing**
Calcolo di uno o più percorsi tra origine, tappe e destinazione.

**Costing**
Funzione che assegna un costo agli archi: tempo, distanza, pedaggi, preferenze,
accessibilità o valore turistico.

**Route (`RoutePlan`)**
Percorso canonico Travel DNA, indipendente dal provider.

**Leg**
Parte della route tra due tappe.

**Maneuver**
Istruzione logica, per esempio imboccare un'uscita o svoltare.

**Guidance**
Processo che trasforma posizione e route in progresso, manovre e voce.

**Route progress**
Posizione corrente lungo il percorso, distanza residua ed ETA.

**Off-route**
Stato in cui la posizione non è più compatibile con il percorso attivo secondo
soglie e tempo configurati.

**Rerouting**
Calcolo controllato di un percorso sostitutivo.

**Percorso ombra (`ShadowRoute`)**
Percorso stimato da Travel DNA mentre un navigatore esterno resta l'autorità.
Serve per contestualizzare guida, presenza e diario, non per dare istruzioni.

**Navigation snapshot**
Stato compatto pubblicato alla UI: manovra, distanza, ETA, confidenza e progresso.

## Architettura

**Dominio**
Regole e concetti che descrivono il problema del prodotto.

**Bounded context**
Confine in cui un modello e un vocabolario hanno significato coerente.

**Contratto canonico**
Tipo o interfaccia Travel DNA usata dal core senza dipendere dal provider.

**Porta (`Port`)**
Interfaccia richiesta o offerta dal core.

**Adapter**
Implementazione che collega una porta a una libreria, piattaforma o servizio.

**Provider**
Tecnologia concreta che offre una capacità: routing, mappe, ricerca o voce.

**Plugin**
Modulo selezionabile che implementa uno o più contratti dichiarando capability.
Nel mobile è normalmente incluso nella build; nel backend può essere un modulo o
servizio sostituibile a runtime.

**Capability**
Funzione dichiarata dal provider, per esempio routing offline o lane guidance.

**Composition root**
Punto unico che conosce implementazioni e costruisce l'applicazione.

**Shadow mode**
Esecuzione di un nuovo provider senza mostrarne il risultato all'utente, per
confrontarlo con quello primario.

**Branch by abstraction**
Sostituzione progressiva di un componente mantenendo un contratto comune.

**Local-first**
Approccio in cui l'app legge e scrive prima nello stato locale, sincronizzando con
il server in modo resiliente.

## Prestazioni

**Percorso caldo (`hot path`)**
Sequenza eseguita frequentemente e sensibile a latenza o consumo.

**Jank**
Fotogrammi visibilmente lenti o irregolari.

**Cold start**
Avvio dell'app senza processo già attivo.

**Warm start**
Ritorno a un processo o stato parzialmente inizializzato.

**Soak test**
Test lungo che cerca crescita memoria, degradazione e problemi di stabilità.

**Replay**
Riproduzione deterministica di una sequenza registrata o sintetica.

## Privacy e sicurezza

**Pseudonimo**
Identità visibile agli altri che non espone direttamente l'identità reale.

**Identificatore effimero**
Token che cambia nel tempo e non deve diventare una chiave pubblica permanente.

**Minimizzazione**
Raccolta della quantità e precisione minima necessaria.

**Retention**
Durata di conservazione di un dato.

**Consent snapshot**
Stato versionato del consenso applicabile a un'azione.

**Driving context**
Contesto stimato o dichiarato: conducente, passeggero, fermo o sconosciuto.

**Degraded mode**
Comportamento esplicito con capacità ridotte quando rete, GPS o provider non sono
affidabili.
