# Protocollo del Pilot 1 Android su strada

## Stato

`planned protocol — non autorizza ancora prove su strada`

Questo documento definisce come verrà condotto il primo pilot stradale quando APK,
permessi, foreground service, recorder e navigatori esterni avranno superato i
propri gate. Pilot 0 non usa questo protocollo perché non accede alla posizione
reale.

Il Pilot 1 può includere una sottoprova **Android Auto POI companion** soltanto dopo
una slice automotive readiness dedicata. La sottoprova non dichiara TDNA come
navigatore turn-by-turn e non autorizza la categoria Navigation.

## Principio di sicurezza

Il conducente guida. Non legge report, non naviga menu, non risponde a domande e
non prende appunti.

Durante i test interattivi deve esserci:

```text
conducente
+ passeggero-osservatore
```

Se non c'è un osservatore, l'app viene configurata prima della partenza e ogni
interazione viene effettuata soltanto dopo essersi fermati in sicurezza. Una head
unit non elimina questa regola: i test di nuove azioni, template o failure mode
vengono eseguiti dal passeggero o a veicolo fermo.

## Obiettivi

Il Pilot 1 deve raccogliere evidenza su:

- installazione e avvio;
- permission UX;
- start/pausa/ripresa/fine;
- foreground service e notifica;
- lifecycle e process recreation;
- handoff a navigatore esterno;
- ritorno a Travel DNA;
- recorder locale bounded;
- perdita/ripresa rete;
- batteria e temperatura osservabili;
- qualità della diagnostica;
- comprensibilità e carico cognitivo.

Se la sottoprova POI è abilitata, raccoglie anche evidenza su:

- scoperta e apertura della car app;
- lista breve di tappe o POI;
- selezione di una destinazione;
- handoff al navigatore esterno;
- touch/rotary e modalità giorno/notte;
- stop o degraded mode coerente;
- assenza di interazioni complesse durante la guida.

Non valuta ancora:

- precisione di un navigatore TDNA;
- categoria Android Auto Navigation;
- guida vocale TDNA;
- traffico;
- qualità universale del map matching;
- social discovery;
- chat libera durante la guida;
- Android Automotive OS production package;
- sicurezza di una release pubblica.

## Ruoli

### Test lead

- sceglie build, dispositivi e scenario;
- verifica che protocollo e policy Android for Cars siano aggiornati;
- riceve report redatti;
- decide stop o rollback.

### Conducente

- non usa l'interfaccia mentre il veicolo è in movimento;
- può interrompere il test in qualunque momento;
- segnala problemi percepiti solo quando è sicuro farlo.

### Osservatore

- controlla la checklist;
- registra timestamp relativi, non coordinate precise;
- acquisisce screenshot soltanto se consentito e sicuro;
- non distrae il conducente;
- esegue le interazioni consentite.

### Reviewer dati/privacy

- verifica redazione dell'export;
- controlla retention e cancellazione;
- impedisce il caricamento di tracce personali nel repository.

## Prerequisiti tecnici generali

- build identificata da commit SHA;
- CI verde e due review pulite;
- APK firmata per il canale interno;
- manifest permission audit;
- foreground-service type verificato;
- notifica persistente verificata;
- data inventory aggiornato;
- storage limit testato;
- export diagnostico redatto;
- kill/restart test su emulatore e dispositivo;
- stop button raggiungibile a veicolo fermo;
- issue per ogni failure noto e degraded mode.

## Prerequisiti Android Auto POI

La sottoprova è esclusa se manca uno dei seguenti gate:

- ADR-0011 e capitolo 61 ricontrollati;
- Car App Library runtime implementato in una slice separata;
- categoria `androidx.car.app.category.POI` coerente con il prodotto testato;
- nessuna categoria Navigation dichiarata;
- Desktop Head Unit testato prima del veicolo;
- template e azioni ridotti approvati;
- permessi e configurazione primaria completabili sul telefono;
- nessun feed, chat completa, foto o testo libero sulla car surface;
- screenshot e log automotive redatti;
- head unit e versione Android Auto registrate nella matrice;
- stop condition automotive definita.

Il DHU verde è prerequisito, non prova su veicolo.

## Prerequisiti fisici

- telefono fissato in posizione stabile;
- schermo non ostruisce la visuale;
- cavo/alimentazione sicuri;
- batteria iniziale registrata;
- nessun dispositivo libero nell'abitacolo;
- percorso breve e conosciuto;
- condizioni meteo compatibili;
- area di sosta disponibile;
- passeggero-osservatore per scenari interattivi;
- connessione Android Auto dichiarata, se usata.

## Matrice minima

La matrice mobile dovrebbe includere, se disponibili:

```text
un dispositivo vicino al minSdk
un dispositivo Android intermedio
un dispositivo recente sul target corrente
```

Registra:

- produttore/modello generalizzato;
- versione Android e patch level;
- memoria disponibile;
- modalità risparmio energetico;
- permesso approximate/precise;
- versione del navigatore esterno;
- tipo di connessione.

Per la sottoprova Android Auto registra anche:

- versione Android Auto;
- modalità USB o wireless;
- head unit o veicolo generalizzati;
- input touch/rotary disponibili;
- dimensione/aspect ratio indicativi;
- day/night mode osservata.

Non pubblicare seriale, account, numero di telefono, VIN, targa o identificatori
advertising.

## Scenari mobile

### S1 — installazione e consenso

1. installare la build;
2. verificare che nessun permesso sia concesso automaticamente;
3. avviare un viaggio;
4. leggere il rationale da fermi;
5. negare il permesso;
6. verificare degraded mode e assenza di crash;
7. concedere foreground location;
8. verificare indicatore e notifica.

### S2 — viaggio breve nominale

1. registrare batteria e ora relativa iniziali;
2. avviare la sessione;
3. eseguire handoff al navigatore esterno;
4. viaggiare 10–20 minuti;
5. tornare a TDNA da fermi;
6. pausa/fine;
7. verificare timeline e spazio locale;
8. esportare diagnostica redatta.

### S3 — processo ricreato

1. sessione attiva;
2. sistema o test lead termina l'Activity, non il veicolo;
3. riaprire l'app quando sicuro;
4. verificare assenza di sessione duplicata;
5. verificare notifica e stato;
6. concludere da fermi.

### S4 — rete assente

1. avviare con rete disponibile;
2. disabilitare rete da fermi o tramite osservatore;
3. proseguire percorso noto;
4. verificare recorder e messaggio degraded;
5. ripristinare rete;
6. verificare assenza di perdita o duplicazione non dichiarata.

### S5 — permesso revocato

1. mettere in pausa e fermare il veicolo;
2. revocare il permesso;
3. riaprire TDNA;
4. verificare che la sessione degradi o termini coerentemente;
5. verificare assenza di loop aggressivo.

### S6 — batteria

Ripetere un percorso comparabile con:

- TDNA inattivo;
- TDNA sessione attiva;
- navigatore esterno + TDNA;
- Android Auto POI + navigatore esterno, solo se la sottoprova è abilitata.

Il risultato è osservazionale. Non dedurre percentuali universali da pochi telefoni
o veicoli.

## Scenari Android Auto POI

### A1 — connessione e apertura

1. configurare viaggio e permessi sul telefono da fermi;
2. collegare Android Auto;
3. aprire TDNA POI;
4. verificare caricamento entro il limite policy corrente;
5. verificare lista breve, titolo e stato coerenti;
6. nessun contenuto social o dato personale inatteso.

### A2 — selezione tappa e handoff

1. osservatore apre la lista POI;
2. seleziona una tappa dichiarata;
3. verifica dettaglio breve;
4. avvia handoff al navigatore esterno;
5. verifica che il navigatore esterno sia l'autorità delle manovre;
6. verifica ritorno/degraded mode da fermi.

### A3 — day/night e input

1. provare day e night mode quando disponibili;
2. verificare touch;
3. verificare rotary quando disponibile;
4. verificare che il task flow resti breve;
5. nessuna azione critica dipende da testo libero.

### A4 — perdita connessione

1. interrompere la connessione in modo controllato;
2. verificare assenza di crash o sessione fantasma;
3. verificare che recorder e navigatore esterno seguano i propri contratti;
4. riconnettere da fermi;
5. verificare stato coerente.

## Stop conditions

Interrompere immediatamente se:

- il conducente deve leggere o toccare l'app;
- il supporto si muove;
- telefono o head unit si surriscaldano in modo anomalo;
- la batteria scende rapidamente senza spiegazione;
- la notifica scompare mentre il recorder continua;
- la sessione non può essere terminata;
- l'app produce audio o overlay inattesi;
- vengono mostrati dati di un altro tester;
- l'export contiene coordinate precise o token non previsti;
- la car surface mostra chat completa, foto, feed o configurazione complessa;
- TDNA sembra fornire manovre pur essendo in categoria POI;
- il test aumenta il rischio stradale.

## Dati raccolti

Consentiti nel report condiviso:

- build SHA;
- device/head-unit class generalizzata;
- versione Android e Android Auto generalizzata;
- scenario;
- durata relativa;
- batteria iniziale/finale;
- contatori eventi;
- error code redatti;
- screenshot senza dati personali;
- valutazione UX;
- issue riproducibile.

Da non committare:

- traccia GPS personale completa;
- indirizzo di casa/lavoro;
- token o account Google;
- numero di telefono;
- VIN o targa;
- volti o voci senza consenso;
- notifiche di altre app;
- payload privati del navigatore esterno.

Le fixture di regressione vengono ricostruite sinteticamente, non copiate dalla
traccia reale.

## Scheda di audit

```markdown
# Field audit

Build SHA:
Scenario:
Device class / Android version:
Android Auto / head unit class, if applicable:
Observer:
Duration:

## Preconditions

## Expected

## Observed

## Safety events

## Lifecycle events

## Battery/thermal observation

## Android Auto POI observation

## Data/privacy review

## Reproduction with synthetic fixture

## Issue links

## Outcome
PASS / PASS WITH LIMITS / STOP / ROLLBACK
```

## Gate di uscita Pilot 1

- nessun finding safety aperto;
- nessun dato preciso caricato di default;
- lifecycle nominale e process recreation coerenti;
- storage bounded;
- stop/cancellazione affidabili;
- handoff dichiarato per ogni provider;
- battery observations accettabili sulla matrice;
- accessibilità di base;
- guida tester aggiornata;
- decisione esplicita su Pilot 2.

Se Android Auto POI è incluso:

- DHU e veicolo dichiarati;
- categoria POI e contenuto coerenti;
- nessuna categoria Navigation;
- nessuna interazione complessa richiesta al conducente;
- perdita connessione e ritorno coerenti;
- quality checklist corrente riesaminata;
- report automotive separato da road reliability.

## Collegamenti

- [Roadmap Android-first](55-roadmap-android-first-e-pilot.md)
- [Android Auto compliance](61-android-auto-compliance-e-roadmap-automotive.md)
- [ADR-0011](../adr/0011-android-auto-poi-first-and-car-surfaces.md)
- [Percorso di studio](56-percorso-studio-android-first.md)
- [Privacy, sicurezza e guida](33-privacy-security-driving-safety.md)
- [Strategia test](30-strategia-test.md)
- [Performance budget](32-performance-budget.md)
- [Navigatori esterni](25-navigatori-esterni-e-automotive.md)
