# ADR-0009 — Modello di licenza del progetto e delle librerie

- Status: Proposed
- Date: 2026-07-16

## Contesto

Travel DNA vuole pubblicare progressivamente contratti, test kit, replay e
librerie riusabili. Una repository pubblica senza licenza esplicita non concede
automaticamente i diritti necessari a usare, modificare e redistribuire il
codice. Anche documentazione, fixture, dati OSM e contenuti editoriali possono
avere licenze differenti.

## Decisioni da prendere

- licenza del codice applicativo;
- licenza delle librerie generiche;
- licenza della documentazione didattica;
- licenza delle fixture sintetiche;
- gestione di contributi, DCO o CLA;
- separazione tra codice open source, servizi gestiti e contenuti editoriali;
- compatibilità con dipendenze BSD, MIT, Apache e dati ODbL.

## Alternative iniziali

### Apache License 2.0

Pro: licenza permissiva con concessione brevetti esplicita. Contro: testo e
adempimenti più articolati.

### MIT o BSD-2-Clause

Pro: semplici e permissive. Contro: protezione brevettuale meno esplicita rispetto
ad Apache-2.0.

### MPL-2.0

Pro: copyleft a livello di file, può proteggere miglioramenti alle librerie senza
imporre copyleft all'intera applicazione. Contro: maggiore complessità per nuovi
contributori.

### GPL/AGPL

Pro: copyleft forte. Contro: può complicare integrazioni mobile, SDK e strategie
commerciali; richiede una decisione consapevole, non automatica.

## Stato corrente

Nessuna licenza viene scelta in questo pacchetto documentale. Prima di accettare
contributi di codice esterni, il maintainer deve stabilire il modello e aggiungere
file `LICENSE`, notice e procedura contributiva coerenti.

## Esperimento/analisi richiesta

- chiarire modello di prodotto e partnership;
- inventariare dipendenze e dati;
- valutare brevetti e distribuzione app store;
- decidere se le librerie possono avere licenza diversa dall'app;
- ottenere review legale prima del lancio commerciale.
