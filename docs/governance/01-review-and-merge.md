# Review e merge seriale

## Gate normativo

Nessuna PR può distribuire una modifica senza:

- controlli applicabili verdi;
- due review round consecutivi senza nuovi finding;
- stesso substantive head SHA;
- una sola PR aperta;
- autorità di merge;
- expected-head guard.

## Vocabolario

**Substantive head:** ultimo commit che modifica codice, test, contratti, fixture, workflow, documentazione stabile o report tecnico.

**Review round:** lettura distinta end-to-end con focus dichiarato.

**Finding:** problema che richiede una modifica sostanziale o una decisione esplicita.

## Sequenza

```text
inventario PR e main
-> branch dal main corrente
-> draft PR
-> baseline CI
-> review/finding/fix
-> clean round 1
-> clean round 2 sullo stesso SHA
-> ready
-> merge con expected head
-> verifica nuovo main
```

## Ledger di review

Ogni round registra:

```text
Review round N
Head SHA:
Risk level:
Focus:
Files/contracts inspected:
CI/test evidence:
Findings:
Outcome:
Consecutive clean rounds:
```

Un commit sostanziale o un fix azzera il conteggio. Modificare solo descrizione PR, review, label o rerun sullo stesso SHA non lo azzera.

## Focus consigliati

- Round 1: correttezza, contratti, invarianti, ownership, error model e test.
- Round 2: architettura, dipendenze, performance, privacy, documentazione, scope e CI.
- Per R3: threat model, abuso, guida, degradazione e revoca.

## Checklist pre-merge

- [ ] substantive head registrato;
- [ ] unica PR aperta;
- [ ] branch basato sul main corretto;
- [ ] CI applicabile verde;
- [ ] nessun finding o thread aperto;
- [ ] round 1 pulito;
- [ ] round 2 pulito sullo stesso SHA;
- [ ] nessun commit sostanziale successivo;
- [ ] issue e documentazione coerenti;
- [ ] autorizzazione presente;
- [ ] expected-head guard.

## Dopo il merge

Verificare `merged=true`, merge commit, issue, nuovo `main` e assenza di PR aperte prima di creare la slice seguente.
