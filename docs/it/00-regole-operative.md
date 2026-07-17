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

Il metodo deve produrre non soltanto codice funzionante, ma anche spiegazioni,
prove, misure e decisioni ricostruibili.

## 2. Fonti di verità

```text
GitHub Discussion  -> ragionamento ancora aperto
ADR                -> decisione architetturale specifica
Documentazione     -> spiegazione consolidata corrente
Issue              -> lavoro da svolgere
Pull request       -> modifica concreta e verificabile
PR review ledger   -> evidenza dei round sullo stesso head
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
5. leggere `50-registro-milestone.md` e la roadmap corrente;
6. scegliere i documenti del componente interessato;
7. aprire codice e test reali, quando esistono.

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
Visualizzare una RoutePlan canonica usando FakeMapRenderer, con contract test e
scenario Lab aggiornato.
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

Dominio e casi d'uso dipendono da interfacce e modelli Travel DNA. Non importano
tipi MapLibre, Valhalla, Ferrostar, Google Maps, Waze o Sygic.

### 6.2 Adapter piccoli e dichiarati

Un adapter traduce:

```text
modello del provider <-> modello canonico Travel DNA
```

Non deve diventare il luogo della logica di prodotto.

### 6.3 Plugin selezionati fuori dal loop critico

Il registry sceglie il provider durante la composizione o un cambio controllato.
Il loop GPS usa riferimenti diretti già risolti.

### 6.4 Navigatori esterni come modalità di prima classe

Travel DNA deve mantenere diario, chat, presenza e suggerimenti quando Waze,
Google Maps, Sygic o un altro navigatore è in primo piano.

### 6.5 Degradazione esplicita

Se un provider manca, il sistema dichiara cosa continua a funzionare, cosa perde
precisione e quale fallback usa. Non finge capacità che non possiede.

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

Una PR che aggiunge costo indica baseline, costo atteso, beneficio e benchmark
eseguito o pianificato.

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

Ogni funzione che usa posizione, foto, voce, identità o messaggi dichiara:

- dato raccolto;
- precisione;
- sorgente;
- scopo;
- durata;
- visibilità;
- cancellazione;
- eventuale condivisione;
- abuso possibile;
- comportamento alla revoca del consenso.

La chat resta attiva durante la guida, ma la modalità conducente usa voce,
notifiche e azioni brevi. L'interfaccia completa è per passeggero o veicolo
fermo.

## 10. Documentazione obbligatoria

Una modifica non banale aggiorna almeno uno tra:

- documento architetturale;
- contratto API;
- ADR;
- scenario Lab;
- mappa del codice e degli stati;
- strategia test;
- report benchmark;
- threat model;
- guida contributori.

Se una spiegazione importante nasce in chat o review, non resta soltanto lì.

## 11. GitHub e review

Usare:

```text
Milestone
-> issue madre
-> issue figlie
-> draft PR
-> CI verde
-> review round
-> eventuali fix e reset
-> due review round consecutivi senza finding
-> PR ready
-> merge del maintainer
-> chiusura issue
```

Livelli di rischio:

- `R0`: testo o modifica meccanica;
- `R1`: comportamento locale non sensibile;
- `R2`: navigazione, background, provider, sincronizzazione;
- `R3`: posizione, chat in auto, privacy, sicurezza, FFI o schema pubblico.

Il requisito dei due round consecutivi vale per **ogni** PR. Cambia la profondità,
non il numero minimo.

### 11.1 Review round

Un review round è una nuova lettura end-to-end del contenuto corrente. Il ledger
della PR registra:

- SHA del substantive head;
- livello di rischio e focus;
- file e contratti controllati;
- finding oppure `no new findings`;
- test e CI osservati;
- numero di round puliti consecutivi.

I due round hanno focus differenti o complementari. Possono essere svolti dallo
stesso reviewer solo come passaggi realmente separati e documentati.

### 11.2 Finding, fix e reset

Un finding è un problema che richiede una modifica sostanziale a codice, test,
contratto, documentazione stabile, fixture, workflow o scope.

```text
finding
-> fix
-> test o prova di regressione
-> CI sul nuovo head
-> clean-review counter = 0
```

Dopo il fix servono due nuovi round consecutivi senza finding. Un round precedente
al fix non può essere riutilizzato.

### 11.3 Substantive head

Invalidano la sequenza le modifiche a:

- codice;
- test;
- contratti;
- documentazione stabile;
- fixture;
- build e CI;
- report tecnico della slice.

Non la invalidano da sole:

- descrizione della PR;
- review submission o commento;
- label o milestone;
- rerun della stessa CI sul medesimo commit.

I due round puliti devono riferirsi allo stesso substantive head SHA.

### 11.4 Ledger PR e report storico

Il ledger autorevole per ready e merge è la timeline delle review GitHub insieme
alla descrizione della PR. Può registrare gli esiti dopo i round senza cambiare
lo SHA.

Il report Markdown committato prima dei round finali contiene:

- issue e PR;
- finding e fix già avvenuti;
- substantive head previsto;
- piano e focus dei round;
- link al ledger della PR.

Non viene modificato dopo i round soltanto per copiarne gli esiti, perché il nuovo
commit invaliderebbe le review. Dopo il merge, una successiva PR documentale può
riconciliare il report con CI finale, round puliti e merge commit.

### 11.5 Gate di ready, merge e chiusura

Una PR non può essere marcata ready, chiusa come completata o mergiata finché:

1. tutti i finding sono risolti o trasformati in non-obiettivi approvati;
2. la CI richiesta è verde sul substantive head corrente;
3. esistono due round consecutivi senza finding sullo stesso head;
4. il ledger PR contiene evidenza dei round e il report contiene link, finding
   history e review plan;
5. documentazione, issue e milestone sono coerenti con lo stato pre-merge.

L'issue collegata viene chiusa dal merge o subito dopo, non soltanto perché
l'implementazione è pronta. La procedura dettagliata vive in
[06-review-e-merge.md](06-review-e-merge.md).

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
- documentazione e tracciabilità sono aggiornate;
- la CI richiesta è verde sul substantive head corrente;
- due review round consecutivi sullo stesso head non hanno prodotto finding;
- il ledger PR e il report storico rispettano i ruoli definiti sopra;
- issue e milestone sono pronte a essere aggiornate dal merge.
