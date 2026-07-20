# ADR-0006 — Rust selettivo per core deterministici e percorsi caldi

- Status: Accepted
- Date: 2026-07-16

## Contesto

Travel DNA vuole insegnare Rust e, in prospettiva, possedere parti della guidance.
Usare Rust ovunque aumenterebbe però build, FFI e complessità senza beneficio
proporzionato.

## Decisione

Rust viene introdotto prima in componenti:

- deterministici;
- privi di UI;
- indipendenti dalla rete;
- verificabili con fixture;
- sensibili a latenza o memoria;
- riusabili su Android e iOS.

Candidati:

```text
tdna-geo
tdna-replay
tdna-guidance
tdna-map-matching (futuro)
```

Il primo consumo è tramite CLI e test. I binding mobili arrivano dopo la
stabilizzazione dell'API.

Non si spostano inizialmente in Rust account, chat CRUD, diario editoriale o
orchestrazione applicativa.

## FFI

- chiamate coarse-grained;
- buffer e ownership documentati;
- errori tipizzati;
- nessun panic oltre il confine;
- cancellazione esplicita;
- benchmark del costo di crossing e copie.

## Conseguenze

Rust diventa un insegnamento collegato a un problema reale, non una seconda
codebase parallela. Il prezzo è una pipeline di build più complessa e la
necessità di mantenere binding e test cross-language.
