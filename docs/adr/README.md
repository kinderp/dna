# Architecture Decision Records

Gli ADR registrano decisioni architetturali specifiche e durevoli. Non
sostituiscono i capitoli didattici: l'ADR conserva contesto, alternative,
decisione e conseguenze; `docs/it` spiega il sistema in modo narrativo.

## Stati

- `Proposed`: decisione candidata;
- `Accepted`: decisione corrente;
- `Superseded`: sostituita da un nuovo ADR;
- `Deprecated`: ancora rilevante ma non consigliata;
- `Rejected`: alternativa non adottata.

## Regole

1. Un ADR tratta una decisione, non un'intera roadmap.
2. Indica contesto, alternative, decisione, conseguenze, prove e revisit
   conditions.
3. Una decisione accepted deve apparire anche nei documenti e nel codice
   interessato.
4. Non riscrivere retroattivamente il senso storico: creare un nuovo ADR.
5. Prestazioni, licenze, privacy e capability richiedono evidenza collegata.

## Indice

| ADR | Stato | Decisione |
| --- | --- | --- |
| [0001](0001-kotlin-multiplatform-native-ui.md) | Accepted | Core condiviso, UI critica nativa. |
| [0002](0002-canonical-contracts-provider-adapters.md) | Accepted | Contratti canonici e adapter provider. |
| [0003](0003-external-navigation-first-class.md) | Accepted | Navigatori esterni come modalità di prima classe. |
| [0004](0004-open-source-navigation-baseline.md) | Accepted | MapLibre, Valhalla e Ferrostar come baseline sostituibile. |
| [0005](0005-local-first-mobile-data.md) | Accepted | Stato locale come base UI/resilienza. |
| [0006](0006-rust-for-deterministic-hot-paths.md) | Accepted | Rust selettivo nei core deterministici. |
| [0007](0007-jvm-backend-framework-spike.md) | Proposed | Spike Spring/Ktor prima del backend. |
| [0008](0008-documentation-and-lab-as-product.md) | Accepted | Documentazione e Lab come prodotto. |
| [0009](0009-project-licensing-model.md) | Proposed | Licenza codice, documentazione e fixture. |
| [0010](0010-android-first-pilot-sequence.md) | Accepted | Android-first, Pilot 0/1/2 e app separata dai moduli KMP. |
