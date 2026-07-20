# Scenario: compose a daily page with photos and thoughts

id: `lab.journal.daily-page.v0`
status: `stable-doc`
scenario kind: `journey-journal-media`

## Learning goal

Seguire eventi di viaggio, soste e fotografie fino a una pagina modificabile,
senza pubblicazione automatica.

## Fixture

Viaggio sintetico:

- partenza 08:30;
- sosta 11:45;
- cinque foto tra 11:42 e 12:10;
- arrivo campeggio 17:20;
- foto di copertina;
- pensiero manuale.

## Trigger

Comando `CloseTripDay` o apertura della bozza serale.

## Expected evidence

- due stop/visit candidate;
- foto candidate associate;
- `DailyPage` auto draft;
- utente rimuove una foto;
- aggiunge pensiero;
- rigenerazione preserva edit;
- conferma pagina privata.

## Tracepoints

```text
JOURNEY_EVENT_APPENDED
STOP_CANDIDATE_DETECTED
MEDIA_ASSOCIATION_PROPOSED
DAILY_PAGE_COMPOSED
USER_EDIT_PRESERVED
```

## Module path target

```text
JourneyEventStore
-> StopDetectionPolicy
-> MediaObservationPort
-> MediaAssociationPolicy
-> DailyPageComposer
-> DailyPageRepository
-> DailyPageEditor
```

## State changes

```text
NOT_CREATED -> AUTO_DRAFT -> USER_EDITED -> CONFIRMED
```

## Privacy properties

- no media uploaded for composition;
- EXIF not shared;
- page private;
- deletion respected;
- manual edit owner wins.

## Missing tests

- timezone;
- midnight;
- photo without location;
- cloud-only asset;
- stop mistaken for traffic;
- offline restore;
- long trip performance.

## Non-goals

- public social post;
- automatic review;
- Touring content;
- print export.
