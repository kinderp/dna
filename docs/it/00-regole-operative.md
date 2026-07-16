# Regole operative di Travel DNA

Questo documento è la costituzione pratica del progetto. Deve restare più breve
e stabile dei manuali specialistici. Le procedure dettagliate vivono nei
capitoli collegati.

## 1. Obiettivo del metodo

Travel DNA è contemporaneamente:

- un prodotto mobile;
- un sistema di navigazione e cartografia;
- un servizio social contestuale;
- un archivio personale di viaggi;
- un progetto open source progressivo;
- una codebase didattica per studenti.

Il metodo deve quindi produrre non soltanto codice funzionante, ma anche
spiegazioni, prove, misure e decisioni ricostruibili.

## 2. Fonti di verità

```text
GitHub Discussion  -> ragionamento ancora aperto
ADR                -> decisione architetturale specifica
Documentazione     -> spiegazione consolidata corrente
Issue              -> lavoro da svolgere
Pull request       -> modifica concreta e verificabile
Test               -> evidenza eseguibile del comportamento
Benchmark          -> evidenza misurata del costo
Field audit        -> comportamento osservato in condizioni reali
Git                -> storia cronologica
```

Una Discussion non è un contratto. Quando una scelta viene presa, deve entrare
nell'ADR e nei documenti stabili interessati.

## 3. Bootstrap di una sessione

Prima di modificare codice o contratti:

1. leggere `AGENTS.md`;
2. leggere questo file;
3. leggere `03-guida-lettura-documentazione.md`;
4. leggere `documentation-status.md`;
5. leggere `50-registro-milestone.md` e la roadmap della milestone corrente;
6. scegliere i documenti del componente interessato;
7. aprire il codice e i test reali, quando esistono.

Non leggere tutta la documentazione senza criterio: mescolare visione futura,
contratto corrente e note storiche porta a implementare troppo.

## 4. Scheda obbligatoria del task

Prima di un passo non banale, rispondere:

| Domanda | Perché serve |
| --- | --- |
| Quale caso d'uso utente cambia? | Evita refactor senza valore osservabile. |
| Quale bounded context possiede la regola? | Evita logica nel modulo sbagliato. |
| Quali piattaforme sono coinvolte? | Android, iOS, backend e auto hanno lifecycle diversi. |
| Quale contratto canonico può cambiare? | Protegge la sostituibilità dei provider. |
| Quale dipendenza esterna è coinvolta? | Identifica adapter e rischio supply-chain. |
| Tocca un percorso caldo? | Determina benchmark e review prestazionale. |
| Quali dati o permessi tratta? | Determina privacy, sicurezza e store review. |
| Quali test o replay servono? | Rende il passo verificabile. |
| Quali documenti vanno aggiornati? | Mantiene la codebase studiabile. |
| È scope corrente o roadmap? | Impedisce espansione incontrollata. |

## 5. Micro-step e vertical slice

Un micro-step è un comportamento coerente, reviewabile e reversibile. Non è
necessariamente un singolo file.

Esempio valido:

```text
Visualizzare una RoutePlan canonica su Android usando FakeRoutePlanner e
MapLibreAdapter, con test del contratto e scenario Lab aggiornato.
```

Esempio troppo grande:

```text
Implementare navigatore, diario, chat, presenza e mappe offline.
```

Esempio troppo frammentato:

```text
PR 1 data class vuota; PR 2 metodo vuoto; PR 3 import; PR 4 primo uso.
```

Ogni slice deve produrre una prova osservabile: test, replay, screenshot di test,
benchmark, contratto o scenario Lab.

## 6. Regole architetturali

### 6.1 Contratti Travel DNA prima dei provider

Il dominio e i casi d'uso dipendono da interfacce e modelli Travel DNA. Non
importano tipi MapLibre, Valhalla, Ferrostar, Google Maps, Waze o Sygic.

### 6.2 Adapter piccoli e dichiarati

Un adapter traduce:

```text
modello del provider <-> modello canonico Travel DNA
```

Non deve diventare il luogo in cui vive la logica di prodotto.

### 6.3 Plugin selezionati fuori dal loop critico

Il registry sceglie il provider all'avvio o al cambio controllato della sessione.
Il loop GPS usa riferimenti diretti già risolti.

### 6.4 Navigatori esterni come modalità di prima classe

Travel DNA deve mantenere diario, chat, presenza e suggerimenti quando Waze,
Google Maps, Sygic o un altro navigatore è in primo piano.

### 6.5 Degradazione esplicita

Se un provider manca, il sistema deve dichiarare che cosa continua a funzionare,
che cosa perde precisione e quale fallback usa. Non deve fingere capacità che non
possiede.

### 6.6 Local-first

La UI legge da stato locale. La rete sincronizza e arricchisce; non deve essere
necessaria per aprire ogni schermata o conservare il diario.

## 7. Percorsi caldi

I percorsi caldi iniziali sono:

```text
Navigation:
LocationSample -> filter -> map match -> progress -> maneuver -> snapshot

Rendering:
snapshot/delta -> persistent map -> HUD

Conversation delivery:
incoming message -> local store -> driving policy -> voice/notification

Journey recording:
location/event -> bounded append -> later diary projection
```

Nel percorso caldo non entrano senza prova:

- lookup del plugin registry;
- query di rete;
- accesso sincrono al database;
- parsing JSON ripetuto;
- ricostruzione completa della geometria;
- caricamento di fotografie;
- serializzazione diagnostica pesante;
- logging verboso;
- lock globali;
- copie attraversando più runtime.

Una PR che aggiunge costo deve indicare baseline, costo atteso, beneficio e
benchmark eseguito o pianificato.

## 8. Test e benchmark

Scegliere il test in base al contratto:

- unit test: regole pure;
- property test: invarianti geometriche e sequenze;
- state-machine test: sessioni e transizioni;
- contract test: provider intercambiabili;
- replay GPS: navigazione deterministica;
- integration test: DB, rete, librerie e piattaforma;
- UI test: interazioni e accessibilità;
- performance test: latenza, frame, memoria, energia;
- field audit: condizioni reali.

Un benchmark non sostituisce un test funzionale; un test funzionale non dimostra
prestazioni.

## 9. Privacy, sicurezza e guida

Ogni funzione che usa posizione, foto, voce, identità o messaggi deve dichiarare:

- dato raccolto;
- precisione;
- sorgente;
- scopo;
- durata;
- visibilità;
- cancellazione;
- eventuale condivisione;
- abuso possibile;
- comportamento quando il consenso viene revocato.

La chat resta attiva durante la guida, ma la modalità conducente usa voce,
notifiche e azioni brevi. L'interfaccia testuale completa è per passeggero o
veicolo fermo.

## 10. Documentazione obbligatoria

Una modifica non banale deve aggiornare almeno uno tra:

- documento architetturale;
- contratto API;
- ADR;
- scenario Lab;
- mappa del codice e degli stati;
- strategia test;
- report benchmark;
- threat model;
- guida contributori.

Se una spiegazione importante nasce in chat o review, non deve restare soltanto
lì.

## 11. GitHub e review

Usare:

```text
Milestone -> issue madre -> issue figlie -> draft PR -> review -> merge
```

Applicare label almeno per area e tipo. Esempi futuri:

```text
area:navigation  area:map  area:journey  area:conversation
area:android     area:ios  area:backend  area:docs
kind:design      kind:feature kind:bug kind:test kind:benchmark kind:audit
risk:r0          risk:r1      risk:r2  risk:r3
```

Livelli di rischio:

- `R0`: testo o modifica meccanica;
- `R1`: comportamento locale non sensibile;
- `R2`: navigazione, background, provider, sincronizzazione;
- `R3`: posizione, chat in auto, privacy, sicurezza, FFI o schema pubblico.

R2 e R3 richiedono review più profonda e prove mirate.

## 12. Definition of Done

Una slice è conclusa quando, per quanto applicabile:

- il caso d'uso funziona;
- il contratto è chiaro;
- i tipi del provider restano confinati;
- i test corretti passano;
- esiste un replay per la navigazione deterministica;
- il budget prestazionale è rispettato o documentato;
- cancellazione, retry e lifecycle sono definiti;
- il comportamento offline è noto;
- privacy e guida sicura sono verificate;
- documentazione e tracciabilità sono aggiornate.
