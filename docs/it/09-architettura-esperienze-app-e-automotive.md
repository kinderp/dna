# Architettura delle esperienze DNA: mobile, Android Auto e Automotive

## 1. Problema

DNA comprenderà domini differenti — Travel, Shopping, Social, Economy, Commons e futuri servizi — che devono apparire parte dello stesso ecosistema senza produrre una super-app confusa.

Contemporaneamente TDNA sta introducendo la navigazione su:

1. smartphone e tablet;
2. Android Auto, proiettato dal telefono;
3. Android Automotive OS, installato nell'auto.

Queste superfici condividono dati, casi d'uso e identità di prodotto, ma hanno vincoli di sicurezza, input, dimensioni e distribuzione diversi. Non devono quindi usare una UI universale.

## 2. Decisione di prodotto

DNA adotta il principio:

```text
un solo ecosistema
+ un solo linguaggio visivo
+ più esperienze specializzate
```

Non si cerca l'identità pixel-per-pixel. Si condividono:

- nome e tono del prodotto;
- simboli e terminologia;
- palette e tipografia compatibili;
- modello di luoghi, intenti, room e attività;
- stato dell'utente;
- casi d'uso applicativi;
- regole di privacy e consenso.

Ogni superficie compone questi elementi secondo il proprio contesto.

## 3. App mobile: un'unica app DNA modulare

La scelta iniziale raccomandata è una sola applicazione mobile pubblica, chiamata DNA, contenente più domini come moduli funzionali.

Vantaggi:

- una sola identità e un solo onboarding;
- una sola mappa territoriale;
- condivisione naturale di GeoRoom, notifiche e profilo;
- passaggi Travel → Shopping → Social senza deep link tra APK;
- minore attrito per l'utente;
- possibilità di mostrare correlazioni cross-domain;
- una sola sessione e un solo sistema di consenso.

Questa scelta non implica un monolite. Il progetto deve essere multimodulo e consentire in futuro di produrre applicazioni verticali separate dalla stessa base.

## 4. Navigazione mobile: domini come “lenti”, non come app impilate

Lo swipe orizzontale tra domini può essere disponibile come scorciatoia, ma non deve essere il solo meccanismo di navigazione perché:

- è poco visibile;
- entra in conflitto con gesti della mappa e pager interni;
- è meno accessibile;
- rende difficile capire dove ci si trova;
- non scala quando i domini aumentano.

La struttura proposta è:

```text
DNA Shell
├── Home / Oggi
├── Mappa
├── Attività
├── Profilo
└── Domain Switcher
    ├── Travel
    ├── Shopping
    ├── Social
    ├── Economy
    └── altri domini abilitati
```

Il `Domain Switcher` può apparire:

- nella top app bar;
- come pannello richiamabile;
- nella mappa come selettore di layer;
- come scorciatoia persistente nelle schermate principali.

Lo swipe può cambiare lente solo nelle schermate predisposte e deve sempre avere un equivalente visibile e accessibile.

## 5. La mappa come superficie comune

La mappa è il principale elemento trasversale:

- Travel mostra itinerari, tappe e mobilità;
- Shopping mostra negozi, prezzi, gruppi, ritiri e consegne;
- Social mostra GeoRoom, comunità ed eventi;
- Economy può mostrare solo informazioni territoriali autorizzate e non sensibili;
- Commons mostra attività e conversazioni associate a punti, aree e tratte.

I domini non devono creare mappe indipendenti. Contribuiscono invece layer e azioni a una `DNA Map Surface` comune.

## 6. Identità grafica

DNA usa un design system comune, ma permette accenti di dominio.

Elementi condivisi:

- tipografia;
- forme, spaziature e motion;
- componenti di consenso e privacy;
- schede di luogo, intento e room;
- icone semantiche comuni;
- stati di affidabilità e verifica.

Elementi differenziabili:

- colore accent del dominio;
- icona o glyph;
- visualizzazione dei dati specifici;
- densità e priorità delle azioni.

Regola:

```text
stesso linguaggio, non stessa schermata
```

## 7. Android Auto: esperienza per compiti di guida

Android Auto non deve mostrare l'intera super-app e non deve presentare un selettore Travel / Shopping / Social / Economy come sul telefono.

La superficie in auto deve essere organizzata per compiti consentiti e rilevanti durante la guida:

```text
Drive Experience
├── Naviga
├── Percorso attivo
├── Luoghi vicini
├── Fermate e ritiri
├── segnalazioni essenziali
└── comunicazioni brevi consentite
```

La provenienza dal dominio resta interna:

- `Naviga` e `Percorso attivo` usano Travel;
- `Luoghi vicini`, negozi e punti di ritiro usano Shopping/POI;
- brevi comunicazioni possono usare Commons/Social solo quando rientrano nelle categorie e nei template ammessi;
- Economy non espone un'app finanziaria in auto, ma può fornire vincoli o suggerimenti a un caso d'uso consentito, per esempio il costo di una deviazione o un limite di spesa già elaborato sul telefono.

L'utente vede compiti di guida, non la struttura organizzativa del backend.

## 8. Categorie Android for Cars

Il manifest può dichiarare una o più categorie supportate dal `CarAppService`, ma ogni categoria dichiarata deve corrispondere a funzionalità reali e rispettare i relativi requisiti di qualità.

Strategia incrementale:

1. iniziare con `NAVIGATION` per TDNA;
2. aggiungere `POI` solo quando esiste un'esperienza completa e verificabile di ricerca luoghi;
3. valutare `MESSAGING` o `CALLING` esclusivamente per vere funzionalità di comunicazione conformi;
4. non tentare di pubblicare Economy o Shopping completo come categorie automobilistiche non previste;
5. non dichiarare categorie solo per ottenere accesso a template o permessi.

Le operazioni non adatte alla guida — checkout, gestione budget, lunghe chat, configurazione profilo, confronto dettagliato dei prodotti — rimangono sul telefono o in modalità parked quando esplicitamente supportata.

## 9. Android Auto e Android Automotive OS sono build diverse

La codebase può condividere quasi tutta la logica Car App Library, ma Android Automotive OS richiede un modulo e una build dedicati.

Struttura proposta:

```text
:app-mobile
:app-automotive
:car-experience
:design-system
:core:identity
:core:exchange
:core:geo
:core:commons
:feature:travel
:feature:shopping
:feature:social
:feature:economy
```

- `:app-mobile` produce l'app smartphone e include il supporto Android Auto proiettato;
- `:app-automotive` produce l'APK dedicato ad Android Automotive OS;
- `:car-experience` contiene `CarAppService`, `Session`, `Screen` e presentazione Car condivisibili;
- i moduli core e feature espongono casi d'uso, non UI specifiche della piattaforma.

## 10. Un APK o più APK di dominio

Decisione iniziale:

```text
una app mobile DNA
+ una build Automotive separata
+ moduli di dominio indipendenti
```

La codebase deve però permettere in futuro:

- un'app DNA completa;
- un'app Travel dedicata;
- un'app Shopping dedicata;
- build white-label o verticali;
- moduli installabili o abilitabili selettivamente.

La separazione in più app va considerata quando emergono motivi concreti:

- peso dell'app;
- pubblici molto diversi;
- requisiti legali o di sicurezza;
- release indipendenti;
- necessità di isolamento forte;
- posizionamento commerciale distinto.

Non va anticipata solo per timore della complessità della UI.

## 11. Confine tra dominio e superficie

I domini espongono casi d'uso e stato:

```text
FindRoute
FindNearbyPlaces
JoinGeoRoom
CreateShoppingIntent
GetBudgetConstraint
AcceptSharedTransfer
```

Le superfici decidono come presentarli:

```text
Mobile Compose UI
Android Auto Car App templates
Android Automotive OS templates
Voice / notification surfaces
```

Nessun dominio deve conoscere `Activity`, `Composable`, `Screen`, template Android Auto o dimensioni del display.

## 12. Flusso di esempio

L'utente crea sul telefono una lista della spesa e avvia un viaggio.

Alfred o il motore di matching rileva un negozio conveniente lungo il percorso.

Sul telefono DNA mostra:

- confronto dettagliato dei prezzi;
- storico;
- alternative;
- possibilità di acquisto di gruppo.

In Android Auto mostra soltanto:

> Fermata conveniente a 4 minuti dal percorso. Aggiungere come tappa?

La stessa opportunità usa lo stesso caso d'uso e gli stessi dati, ma due presentazioni appropriate.

## 13. Regole di accettazione

L'architettura è corretta quando:

1. Travel, Shopping e Social condividono il design system senza dipendere dalle UI reciproche;
2. un dominio può essere escluso da una build senza modificare gli altri;
3. il car module non dipende da schermate Compose mobile;
4. Android Auto mostra soltanto attività conformi alle categorie dichiarate;
5. lo stato passa coerentemente da telefono ad auto;
6. una stessa azione può avere una presentazione mobile ricca e una car presentation minima;
7. la mappa aggrega layer provenienti da più domini;
8. lo swipe non è l'unico modo per cambiare dominio;
9. l'utente percepisce DNA come un prodotto unico, non come app incollate.

## 14. Fonti tecniche principali

- Android Developers — Android for Cars App Library e categorie supportate: <https://developer.android.com/training/cars/apps/library/set-up-project>
- Android Developers — Car app quality: <https://developer.android.com/docs/quality-guidelines/car-app-quality>
- Android Developers — Supporto Android Automotive OS: <https://developer.android.com/training/cars/apps/automotive-os>
- Android Developers — Modularizzazione: <https://developer.android.com/topic/modularization>
- Android Developers — Pattern di modularizzazione: <https://developer.android.com/topic/modularization/patterns>
