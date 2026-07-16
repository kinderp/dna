# Architecture Decision Records

Gli ADR registrano decisioni architetturali specifiche e durevoli. Non
sostituiscono i capitoli didattici: l'ADR conserva contesto, alternative,
decisione e conseguenze; `docs/it` spiega il sistema in modo narrativo e collega
più decisioni tra loro.

## Stati

- `Proposed`: decisione candidata, ancora discutibile;
- `Accepted`: decisione corrente da rispettare nel codice;
- `Superseded`: sostituita da un altro ADR, senza cancellare la storia;
- `Deprecated`: non più consigliata ma ancora rilevante per compatibilità;
- `Rejected`: alternativa valutata e non adottata.

## Regole

1. Un ADR tratta una decisione, non un'intera roadmap.
2. Deve indicare almeno contesto, forze, alternative, decisione, conseguenze,
   verifiche e condizioni di riesame.
3. Una decisione `Accepted` deve essere riflessa anche nei documenti didattici e
   nei contratti interessati.
4. Non modificare retroattivamente il senso storico di un ADR accettato: creare
   un nuovo ADR che lo sostituisce.
5. Quando una decisione dipende da prestazioni, licenze, privacy o capacità di
   piattaforma, citare la prova o lo spike che la sostiene.

## Indice iniziale

| ADR | Stato | Decisione |
| --- | --- | --- |
| [0001](0001-kotlin-multiplatform-native-ui.md) | Accepted | Core applicativo condiviso, UI critica nativa. |
| [0002](0002-canonical-contracts-provider-adapters.md) | Accepted | Contratti canonici e adapter per ogni provider. |
| [0003](0003-external-navigation-first-class.md) | Accepted | Navigatori esterni come modalità di prima classe. |
| [0004](0004-open-source-navigation-baseline.md) | Accepted | MapLibre, Valhalla e Ferrostar come baseline sostituibile. |
| [0005](0005-local-first-mobile-data.md) | Accepted | Stato locale come base della UI e della resilienza. |
| [0006](0006-rust-for-deterministic-hot-paths.md) | Accepted | Rust selettivo nei core deterministici e misurabili. |
| [0007](0007-jvm-backend-framework-spike.md) | Proposed | Confronto Java/Spring e Kotlin/Ktor prima della scelta backend. |
| [0008](0008-documentation-and-lab-as-product.md) | Accepted | Documentazione e Lab come output di prodotto. |
| [0009](0009-project-licensing-model.md) | Proposed | Modello di licenza per codice, documentazione e fixture. |
