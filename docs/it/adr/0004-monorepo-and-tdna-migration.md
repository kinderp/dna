# ADR-0004 — Monorepo DNA e migrazione history-aware di TDNA

- **Stato:** Implementata; fase submodule storica
- **Data:** 2026-07-19
- **Issue:** #3

## Contesto

TDNA è nato come repository autonomo ed è oggi il primo dominio implementato di DNA. La piattaforma, la mappa, il design system, gli eventi e le esperienze mobile/auto sono però condivisi con Shopping, Social, Economy e Commons. Mantenere `dna` e `tdna` come fonti architetturali concorrenti produrrebbe documentazione, contratti e build divergenti.

Un import manuale immediato dello snapshot TDNA sarebbe rischioso perché potrebbe perdere file, blame e riferimenti alla cronologia. Un grande history rewrite non deve essere eseguito senza un ambiente Git completo e una verifica della build.

## Decisione

`kinderp/dna` diventa il repository principale dell'ecosistema. Travel vive sotto `domains/travel`.

La migrazione avviene in due fasi:

1. **Fase A:** un gitlink/submodule fissa l'intero TDNA al commit sorgente verificato; governance, CI e documentazione trasversale vengono centralizzate in DNA.
2. **Fase B:** TDNA viene importato definitivamente sotto lo stesso prefisso mediante `git subtree` o history rewrite equivalente, dopo verifica di cronologia, path, build e test.

Il submodule è quindi un meccanismo di migrazione reversibile, non il modello finale del monorepo.

## Commit sorgente

```text
kinderp/tdna@85c73ab78dd56506c5595673098adf514765de9c
```

## Conseguenze positive

- una sola fonte per governance e decisioni trasversali;
- TDNA completo e verificabile durante la transizione;
- nessuna copia parziale spacciata per migrazione;
- possibilità di eseguire i controlli Travel dal repository DNA;
- separazione tra CI leggera, CI Travel e prova emulatore costosa;
- archiviazione del vecchio repository differita fino a prova completata.

## Conseguenze negative

- durante la fase A è necessario clonare con `--recurse-submodules`;
- i due sistemi di build restano separati;
- link e issue continuano temporaneamente a puntare a due repository;
- il submodule aggiunge complessità operativa e non consente refactor atomici tra file DNA e TDNA;
- l'import definitivo resta un passo sostanziale separato.

## Vincoli

- nessun nuovo contratto trasversale nasce soltanto in TDNA;
- il riferimento TDNA deve essere aggiornato mediante PR revisionata;
- il commit sorgente è registrato nel manifest;
- il vecchio repository non viene archiviato nella fase A;
- l'import definitivo conserva o rende raggiungibile la cronologia;
- dopo l'import si rimuovono `.gitmodules` e il gitlink;
- Actions esterne restano bloccate a SHA completi;
- il test emulatore non viene eseguito automaticamente su ogni modifica.

## Alternative considerate

### Repository separati permanenti

Scartata per i domini applicativi iniziali: richiederebbe versionamento e PR coordinate prima che i contratti siano stabili.

### Copia snapshot senza cronologia

Scartata come soluzione finale perché perde provenance e rende difficile verificare la completezza.

### History rewrite immediato

Rimandato: è la destinazione corretta, ma deve essere eseguito con strumenti Git completi e validazione end-to-end.

### Submodule permanente

Scartato come architettura finale perché ostacola modifiche atomiche, refactoring e unificazione della build.

## Criteri di uscita dalla fase A

- governance DNA autorevole;
- checkout ricorsivo documentato;
- controlli root per DNA e Travel;
- CI Travel eseguita solo quando necessario;
- matrice documentale definita;
- PR di import history-aware pronta e riproducibile.

## Riesame

Riesaminare questa ADR dopo l'import definitivo. A quel punto lo stato della fase submodule diventa storico e la decisione permanente resta: DNA è il monorepo dei domini applicativi, Alfred rimane un repository autonomo.

## Esito della fase B

Il commit TDNA `85c73ab78dd56506c5595673098adf514765de9c` è stato importato sotto
`domains/travel` mediante subtree merge. `.gitmodules` e il gitlink sono
stati rimossi. La cronologia TDNA rimane raggiungibile dalla storia DNA;
i domini applicativi iniziali vivono ora nello stesso monorepo.
