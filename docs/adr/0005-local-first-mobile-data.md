# ADR-0005 — Architettura mobile local-first

- Status: Accepted
- Date: 2026-07-16

## Contesto

Durante un viaggio la rete può essere debole o assente. Diario, messaggi in coda,
route corrente e interfaccia non possono dipendere da una risposta server per
ogni operazione.

## Decisione

Lo stato locale è la fonte immediata per la UI. La sincronizzazione aggiorna il
modello locale e propaga modifiche remote.

Dati locali minimi:

- viaggio attivo;
- route e snapshot necessari;
- eventi del diario e bozze;
- conversazioni recenti;
- coda messaggi in uscita;
- consensi;
- cache controllata di luoghi e guida;
- stato di sincronizzazione e conflitti.

Ogni modifica locale riceve un identificatore stabile e uno stato di consegna.
Le policy di merge sono specifiche del bounded context: non esiste una strategia
universale last-write-wins.

## Conseguenze

- UI più reattiva e resiliente;
- maggiore complessità di sync, migrazioni e conflitti;
- necessità di cifratura, retention e cancellazione locali;
- test obbligatori per retry, duplicati, ordine e revoca del consenso.
