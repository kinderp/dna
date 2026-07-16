# Use case principali

Gli use case descrivono comportamenti completi, non schermate isolate. Ogni
vertical slice deve indicare quale use case protegge.

## UC-01: pianificare un viaggio

**Attore:** organizzatore del viaggio.
**Obiettivo:** creare un itinerario con destinazione, tappe, stile e partecipanti.

Flusso:

1. crea il viaggio;
2. indica mezzo, partecipanti e preferenze;
3. cerca luoghi e destinazioni;
4. confronta route candidate;
5. salva tappe e opportunità;
6. sceglie navigatore esterno o interno;
7. scarica eventuali dati offline disponibili.

Risultato minimo:

- viaggio salvato localmente;
- route o destinazione disponibile;
- nessun blocco se il server non risponde dopo il salvataggio locale.

## UC-02: viaggiare con navigatore esterno

**Attore:** conducente o organizzatore.
**Obiettivo:** usare Waze/Google Maps/Sygic mantenendo Travel DNA attivo.

Flusso:

1. preme `Parti` in Travel DNA;
2. la sessione avvia registrazione, presenza e chat secondo consenso;
3. Travel DNA apre il navigatore esterno;
4. mantiene un percorso ombra con confidenza;
5. registra eventi del viaggio;
6. riceve messaggi e consigli;
7. torna in primo piano a una sosta.

Criteri:

- il navigatore esterno non perde focus per aggiornamenti ordinari;
- la chat viene consegnata tramite superfici sicure;
- il diario continua compatibilmente con le regole OS;
- quando la confidenza scende, i suggerimenti temporali precisi vengono sospesi.

## UC-03: usare il navigatore Travel DNA

**Obiettivo:** seguire indicazioni con una UI rapida e contenuti turistici.

Flusso:

1. route calcolata;
2. mappa persistente;
3. guidance avviata;
4. posizione aggiornata;
5. manovra e voce pubblicate;
6. opportunità lungo il percorso selezionate;
7. deviazione rilevata;
8. route sostitutiva applicata in modo atomico.

Criteri:

- nessun accesso di rete nel loop steady-state;
- la chat non blocca la guidance;
- la route precedente resta valida fino alla sostituzione;
- la UI aggiorna soltanto i componenti necessari.

## UC-04: ricevere e rispondere a una chat durante la guida

**Attori:** conducente e altro viaggiatore.

Flusso:

1. arriva un messaggio al server;
2. il dispositivo riceve push o aggiornamento live;
3. il messaggio viene salvato localmente;
4. la driving policy decide la superficie;
5. il conducente ascolta;
6. detta una risposta;
7. il sistema conferma quando necessario;
8. il messaggio viene accodato e inviato;
9. il navigatore rimane in primo piano.

Criteri:

- nessuna UI testuale complessa obbligatoria;
- risposte offline vengono messe in coda;
- contenuto e mittente sono moderabili e bloccabili;
- il passeggero può usare la UI completa sul proprio dispositivo.

## UC-05: chiedere un consiglio alla strada

1. l'utente crea una domanda predefinita o vocale;
2. il sistema la converte in una richiesta strutturata;
3. seleziona destinatari compatibili nel corridoio;
4. applica rate limit e privacy;
5. raccoglie risposte;
6. sintetizza il risultato;
7. permette di aprire una conversazione con consenso.

La richiesta scade automaticamente e non deve diventare spam persistente.

## UC-06: costruire la Pagina del giorno

1. il recorder produce eventi e soste candidate;
2. il journal propone momenti;
3. il media context trova foto compatibili;
4. la sera viene generata una bozza;
5. l'utente aggiunge o modifica foto, pensieri e voce;
6. sceglie copertina e visibilità;
7. conferma la pagina.

Criteri:

- le modifiche manuali non vengono sovrascritte;
- l'utente può eliminare una sosta o una foto;
- la bozza funziona offline;
- nessun contenuto è condiviso per default.

## UC-07: trasformare un momento in Cartolina DNA

1. seleziona un momento o accetta una proposta;
2. vede esattamente i campi che usciranno dal diario;
3. rimuove posizione precisa e metadati non necessari;
4. seleziona pubblico e durata;
5. pubblica;
6. può revocare o eliminare.

## UC-08: salutare un compagno di strada

1. la mappa mostra presenza approssimata;
2. l'utente invia `Saluta`;
3. il destinatario riceve senza posizione esatta;
4. se ricambia, può aprirsi un incontro;
5. nessuna relazione permanente è obbligatoria.

## UC-09: usare Travel DNA in una sosta

- esplorare guida e POI;
- leggere chat completa;
- collegare fotografie;
- correggere il diario;
- chiedere informazioni locali;
- inviare una destinazione al navigatore scelto.

## UC-10: completare il viaggio

L'app produce:

- mappa del percorso;
- pagine del giorno;
- album;
- chilometri e tappe;
- consigli ricevuti;
- Cartoline condivise;
- incontri salvati;
- export o stampa futura.

## Use case fuori dall'MVP

- profili autonomi per minori;
- pagamenti e prenotazioni complete;
- spesa comunitaria;
- condivisione school/study DNA;
- messaggistica anonima senza account verificabile;
- traffico crowdsourced nazionale affidabile;
- LoRa in produzione;
- plugin dentro l'interfaccia proprietaria di Waze o Google Maps.
