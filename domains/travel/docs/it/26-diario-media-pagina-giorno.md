# Diario, media e Pagina del giorno

## Obiettivo

Il diario rende Travel DNA utile anche senza community. Deve costruire una bozza
senza appropriarsi dei ricordi dell'utente o pubblicarli automaticamente.

## Principio

> Automazione nella proposta, controllo umano nella memoria e nella condivisione.

## Pipeline

```text
TripSession events
-> raw JourneyEventStore
-> stop and visit candidates
-> media observations
-> timeline projection
-> DailyPage draft
-> user edits
-> confirmed page
-> optional DnaCard projection
```

## Journey event

Eventi candidati:

```text
TripStarted
LocationSampleRecorded or LocationSegmentRecorded
StopCandidateDetected
StopConfirmed
PlaceVisitProposed
PlaceVisitConfirmed
PhotoDiscovered
PhotoAttached
ThoughtAdded
VoiceNoteAdded
GreetingExchanged
DnaCardReceived
DayClosed
```

Non salvare ogni GPS per sempre nel diario sincronizzato. Si possono usare livelli:

- buffer operativo ad alta risoluzione;
- segmento compresso locale;
- eventi narrativi;
- route finale semplificata;
- dati condivisi approssimati.

## Rilevamento sosta

Una sosta candidata combina:

- velocità bassa;
- durata;
- raggio spaziale;
- accuratezza;
- contesto stradale;
- uso del telefono;
- luogo vicino.

Non tutte le soste sono momenti:

- coda;
- semaforo;
- rifornimento breve;
- perdita GPS;
- traghetto;
- parcheggio prima di una visita.

Il sistema propone; l'utente conferma o ignora.

## Place visit

Una visita non è soltanto “GPS dentro un POI”. Servono segnali e confidenza:

```text
stop overlaps place
photos nearby
manual save/check-in
navigation destination
meaningful duration
user confirmation
```

Il diario può mostrare “forse hai visitato” senza trasformarlo in fatto pubblico.

## Media observation

Quando autorizzato, il media adapter osserva:

- asset ID locale;
- timestamp;
- coordinate opzionali;
- tipo;
- dimensioni;
- preferito/modificato;
- disponibilità locale/cloud.

Non copia immediatamente il file completo. Usa riferimenti e genera thumbnail in
background.

## Associazione automatica delle foto

Score possibile:

```text
score = temporal proximity
      + spatial proximity
      + stop overlap
      + manual place selection
      + album/trip participant evidence
```

La UI deve mostrare perché una foto è stata proposta e permettere spostamento.

## Pensieri

Tipi:

- testo libero;
- nota breve;
- momento preferito;
- consiglio pratico;
- emozione/tag;
- nota vocale;
- trascrizione modificabile.

Il pensiero personale non diventa recensione. La trasformazione in consiglio è
un'azione distinta.

## Pagina del giorno

Esempio:

```text
Martedì 14 luglio — Firenze -> Orbetello
328 km · 4 soste · 26 fotografie

08:30 Partenza
  pensiero
  foto

11:45 Pitigliano
  luogo
  5 foto
  consiglio privato

17:20 Campeggio
  copertina
  nota vocale
```

Stato:

```text
AUTO_DRAFT
USER_EDITED
CONFIRMED
ARCHIVED
```

## Rigenerazione senza perdita

Separare:

```text
Derived content
  ordine proposto, distanza, tappe candidate

User-owned edits
  testo, selezione foto, titolo, cancellazioni, visibilità
```

Una nuova proiezione aggiorna solo la parte derivata compatibile. Le modifiche
manuali hanno precedenza.

## Privacy per momento

```text
PRIVATE
TRIP_PARTICIPANTS
SELECTED_CONTACTS
DNA_CARD_SOURCE_ONLY
PUBLIC_FUTURE
```

La visibilità del diario e della Cartolina sono separate.

## Da momento a Cartolina DNA

Passi:

1. selezione esplicita;
2. preview dei campi;
3. rimozione EXIF non necessario;
4. generalizzazione posizione;
5. eventuale pseudonimo;
6. audience e scadenza;
7. publish;
8. revoca.

Il testo può essere riscritto per non esporre dettagli personali.

## Storage locale

Dati strutturati nel DB; file nel media storage della piattaforma o area app.
Conservare:

- riferimenti;
- hash dove necessario;
- stato upload;
- thumbnail;
- editing metadata;
- retention.

Non duplicare originali senza motivo.

## Sincronizzazione

Operazioni idempotenti:

```text
CreateMoment
AttachMedia
AddThought
SetVisibility
ConfirmDailyPage
DeleteMoment
```

Usare versioni o operation IDs. Conflitti:

- testo modificato su due dispositivi;
- foto rimossa;
- pagina rigenerata;
- partecipante modifica momento condiviso.

La prima versione può limitare l'editing a un owner per ridurre complessità.

## Export

Futuro:

- PDF/album;
- mappa animata;
- GPX semplificato;
- archivio personale;
- stampa;
- backup interoperabile.

L'export deve dichiarare coordinate e metadati inclusi.

## Test

- stop candidate vs coda;
- foto prima/dopo la sosta;
- timezone e cambio giorno;
- viaggio attraverso mezzanotte;
- foto senza coordinate;
- media cloud non scaricato;
- rigenerazione preserva pensieri;
- cancellazione e revoca;
- offline;
- viaggio lungo e memoria bounded.

## Errori comuni

- diario come semplice dump GPS;
- pubblicazione automatica;
- EXIF non rimosso;
- inferenza presentata come fatto;
- rigenerazione che sovrascrive edit;
- originale foto copiato inutilmente;
- timeline che dipende dalla rete.
