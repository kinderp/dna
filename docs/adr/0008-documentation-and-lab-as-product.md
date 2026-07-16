# ADR-0008 — Documentazione e Travel DNA Lab come output di prodotto

- Status: Accepted
- Date: 2026-07-16

## Contesto

Il progetto deve essere comprensibile a studenti e contributori che non conoscono
app mobile, cartografia o navigatori. Commenti isolati e API reference non
spiegano il sistema nel tempo.

## Decisione

Ogni milestone significativa produce, oltre al codice:

- capitolo didattico o aggiornamento;
- mappa architetturale o percorso del codice;
- scenario Lab quando il comportamento attraversa più stage;
- test o replay che dimostra lo scenario;
- report prestazionale quando tocca un hot path;
- ADR quando prende una decisione durevole.

I tracepoint Lab sono nomi logici `candidate` o `stable-doc`; non diventano
automaticamente telemetria runtime.

## Conseguenze

- maggiore costo iniziale;
- onboarding e review migliori;
- possibilità di costruire in seguito visualizzazioni, animazioni e laboratori;
- obbligo di mantenere documentazione e codice coerenti.
