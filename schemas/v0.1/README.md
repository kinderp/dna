# Schemi DNA v0.1

Questa cartella contiene i contratti dati iniziali, indipendenti dal linguaggio e dal trasporto.

## Schemi

- `dna-fragment.schema.json` — frammento del profilo condiviso dopo consenso;
- `dna-trace.schema.json` — traccia minimale per discovery;
- `dna-intent.schema.json` — bisogno o disponibilità contestuale;
- `geo-anchor.schema.json` — punto, area o tratta;
- `transport-capabilities.schema.json` — capacità dichiarate dagli adapter;
- `transport-policy.schema.json` — requisiti e limiti richiesti dal dominio;
- `transport-envelope.schema.json` — contenitore indipendente dal mezzo;
- `georoom.schema.json` — stanza associata alla mappa.

## Stato

`v0.1` è una baseline sperimentale. Le modifiche incompatibili generano una nuova directory di versione; gli schemi già pubblicati non vengono riscritti silenziosamente.

## Validazione

Dalla radice del repository:

```bash
python3 -m pip install -r requirements-dev.txt
python3 scripts/validate-schemas.py
```

Gli esempi validati si trovano in `examples/v0.1`.
