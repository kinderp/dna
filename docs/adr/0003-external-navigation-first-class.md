# ADR-0003 — Navigatori esterni come modalità di prima classe

- Status: Accepted
- Date: 2026-07-16

## Contesto

Un viaggiatore può preferire Waze, Google Maps, Sygic, Apple Maps o il navigatore
dell'auto per affidabilità, traffico e abitudine. La presenza di chat e contenuti
turistici non è sufficiente a convincerlo a sostituire immediatamente il suo
navigatore.

## Decisione

Travel DNA deve offrire il proprio valore anche quando un navigatore esterno è in
primo piano.

Prima dell'handoff l'app avvia esplicitamente:

- sessione di viaggio;
- registrazione autorizzata;
- diario;
- ricezione chat e push;
- presenza, se abilitata;
- percorso ombra, se disponibile.

Il provider esterno riceve solo i dati supportati — destinazione, eventuali tappe
e opzioni — attraverso URL, intent o API documentate. Non si presume di poter
inserire una UI Travel DNA dentro l'app esterna.

## Modalità

```text
External navigation
Embedded commercial navigation
Open-source Travel DNA navigation
```

Sono implementazioni differenti dello stesso bisogno utente, non una sequenza in
cui la prima è un workaround temporaneo.

## Conseguenze

- il viaggio e la chat non dipendono dal nostro renderer;
- occorre gestire background, push e perdita di sincronizzazione;
- il percorso ombra ha una confidenza esplicita e non deve fingere di conoscere
  il ricalcolo interno del navigatore esterno;
- le integrazioni profonde richiedono SDK o partnership e restano separate.
