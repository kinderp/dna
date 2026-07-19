# Protocollo del Pilot 1 Android su strada

## Stato

`planned protocol — non autorizza ancora prove su strada`

Questo documento definisce come verrà condotto il primo pilot stradale quando
APK, permessi, foreground service e recorder avranno superato i propri gate.
Pilot 0 non usa questo protocollo perché non accede alla posizione reale.

## Principio di sicurezza

Il conducente guida. Non legge report, non naviga menu, non risponde a domande e
non prende appunti.

Durante i test interattivi deve esserci:

```text
conducente
+ passeggero-osservatore
```

Se non c'è un osservatore, l'app viene configurata prima della partenza e ogni
interazione viene effettuata soltanto dopo essersi fermati in sicurezza.

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

Non valuta ancora:

- precisione di un navigatore TDNA;
- traffico;
- qualità del map matching reale;
- social discovery;
- chat durante la guida;
- Android Auto;
- sicurezza di una release pubblica.

## Ruoli

### Test lead

- sceglie build, dispositivi e scenario;
- verifica che il protocollo sia aggiornato;
- riceve report redatti;
- decide stop o rollback.

### Conducente

- non usa l'interfaccia mentre il veicolo è in movimento;
- può interrompere il test in qualunque momento;
- segnala soltanto problemi percepiti quando è sicuro farlo.

### Osservatore

- controlla la checklist;
- registra timestamp relativi, non coordinate precise nel report condiviso;
- acquisisce screenshot soltanto se consentito e sicuro;
- non distrae il conducente;
- esegue interazioni consentite.

### Reviewer dati/privacy

- verifica redazione dell'export;
- controlla retention e cancellazione;
- impedisce il caricamento accidentale di tracce personali nel repository.

## Prerequisiti tecnici

- build identificata da commit SHA;
- CI verde e due review pulite;
- APK firmata per il canale interno scelto;
- manifest permission audit;
- foreground-service type verificato;
- notifica persistente verificata;
- data inventory aggiornato;
- storage limit testato;
- export diagnostico redatto;
- kill/restart test su emulatore e dispositivo;
- stop button sempre raggiungibile a veicolo fermo;
- issue per ogni failure noto e degraded mode.

## Prerequisiti fisici

- telefono fissato in posizione stabile;
- schermo non ostruisce la visuale;
- cavo/alimentazione sicuri;
- batteria iniziale registrata;
- nessun dispositivo libero nell'abitacolo;
- percorso breve e conosciuto;
- condizioni meteo compatibili;
- area di sosta disponibile;
- passeggero-osservatore per scenari interattivi.

## Matrice minima dispositivi

La prima matrice dovrebbe includere, se disponibili:

```text
un dispositivo vicino al minSdk
un dispositivo Android intermedio
un dispositivo recente sul target corrente
```

La matrice registra:

- produttore/modello;
- versione Android;
- patch level;
- memoria disponibile;
- modalità risparmio energetico;
- permesso approximate/precise;
- versione del navigatore esterno;
- tipo di connessione.

Non pubblicare seriale, account, numero di telefono o identificatori advertising.

## Scenari

### S1 — installazione e consenso

1. installare la build;
2. verificare che nessun permesso sia concesso automaticamente;
3. avviare un viaggio;
4. leggere il rationale da fermi;
5. negare il permesso;
6. verificare degraded mode e assenza di crash;
7. concedere foreground location;
8. verificare indicatore/notifica.

### S2 — viaggio breve nominale

1. batteria e ora relativa iniziali;
2. start session;
3. handoff al navigatore esterno;
4. viaggio 10–20 minuti;
5. ritorno a TDNA da fermi;
6. pausa/fine;
7. verificare timeline e spazio locale;
8. esportare diagnostica redatta.

### S3 — processo ricreato

1. sessione attiva;
2. sistema o test lead termina l'Activity, non il veicolo;
3. riaprire l'app quando sicuro;
4. verificare che la sessione non venga duplicata;
5. verificare notifica e stato;
6. concludere da fermi.

### S4 — rete assente

1. avviare con rete disponibile;
2. disabilitare rete da fermi o tramite osservatore;
3. proseguire percorso noto;
4. verificare recorder locale e messaggio degraded;
5. ripristinare rete;
6. verificare assenza di perdita o duplicazione non dichiarata.

### S5 — permesso revocato

1. mettere in pausa/fermare il veicolo;
2. revocare il permesso da impostazioni;
3. riaprire TDNA;
4. verificare che la sessione degradi o termini coerentemente;
5. nessun loop di richiesta aggressivo.

### S6 — batteria

Ripetere un percorso comparabile con:

- TDNA inattivo;
- TDNA sessione attiva;
- navigatore esterno + TDNA.

Il risultato è osservazionale. Non dedurre percentuali universali da uno o due
telefoni.

## Stop conditions

Interrompere immediatamente se:

- il conducente deve leggere o toccare l'app;
- il supporto si muove;
- il telefono si surriscalda in modo anomalo;
- la batteria scende rapidamente senza spiegazione;
- la notifica del servizio scompare mentre il recorder continua;
- la sessione non può essere terminata;
- l'app produce audio o overlay inattesi;
- vengono mostrati dati di un altro tester;
- l'export contiene coordinate precise o token non previsti;
- il test aumenta il rischio stradale.

## Dati raccolti

Consentiti nel report condiviso:

- build SHA;
- modello e versione Android generalizzati;
- scenario;
- durata relativa;
- stato batteria iniziale/finale;
- contatori eventi;
- error code redatti;
- screenshot senza dati personali;
- valutazione UX;
- issue riproducibile.

Da non committare:

- traccia GPS completa personale;
- indirizzo di casa/lavoro;
- token;
- account Google;
- numero di telefono;
- targa;
- volti o voci senza consenso;
- contenuto di notifiche di altre app;
- payload del navigatore esterno.

Le fixture di regressione vengono ricostruite sinteticamente a partire dal
failure, non copiate dalla traccia reale.

## Scheda di audit

```markdown
# Field audit

Build SHA:
Scenario:
Device class / Android version:
Observer:
Duration:

## Preconditions

## Expected

## Observed

## Safety events

## Lifecycle events

## Battery/thermal observation

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
- handoff dichiarato per ogni provider supportato;
- battery observations accettabili sulla matrice;
- accessibilità di base;
- guida tester aggiornata;
- decisione esplicita su Pilot 2.

## Collegamenti

- [Roadmap Android-first](55-roadmap-android-first-e-pilot.md)
- [Percorso di studio](56-percorso-studio-android-first.md)
- [Privacy, sicurezza e guida](33-privacy-security-driving-safety.md)
- [Strategia test](30-strategia-test.md)
- [Performance budget](32-performance-budget.md)
- [Navigatori esterni](25-navigatori-esterni-e-automotive.md)
