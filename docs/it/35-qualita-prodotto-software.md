# Qualità del prodotto software

## Correttezza non basta

Un navigatore può calcolare la manovra giusta ma:

- mostrarla tardi;
- scaricare la batteria;
- perdere la chat;
- bloccare la UI;
- esporre la posizione;
- non funzionare offline;
- essere incomprensibile agli studenti.

Quindi misuriamo dimensioni separate.

## Matrice

| Dimensione | Domanda |
| --- | --- |
| Correttezza | Produce il risultato atteso? |
| Robustezza | Gestisce input sporchi e failure? |
| Affidabilità | Continua a comportarsi bene nel tempo? |
| Stabilità | Evita crash, leak e degrado? |
| Performance | Risponde entro budget? |
| Leggerezza | Usa CPU, memoria, rete e batteria con disciplina? |
| Sicurezza | Resiste ad abusi e compromissioni? |
| Privacy | Minimizza e controlla i dati? |
| Driving safety | Riduce distrazione e priorizza guida? |
| Coerenza | Stesso significato tra moduli/piattaforme? |
| Semplicità | Evita complessità non necessaria? |
| Manutenibilità | Si modifica senza regressioni diffuse? |
| Estensibilità | Provider e moduli si sostituiscono realmente? |
| Operabilità | Si diagnostica in produzione? |
| Accessibilità | È usabile da persone diverse? |
| Documentazione | È studiabile e contribuibile? |

## Regole guida

```text
Security gates maturity.
Privacy limits product freedom.
Driving safety overrides engagement.
Performance is separate from correctness.
Undocumented behavior is not mature.
Architecture must pay rent.
A provider abstraction is real only if a second implementation can pass its tests.
```

## Robustezza

Scenari:

- GPS nullo/rumoroso;
- route vuota;
- provider 429;
- tile failure;
- DB full;
- photo unavailable;
- permission revoke;
- message duplicate;
- process death;
- timezone change;
- invalid deep link.

Il sistema deve fallire in modo controllato e spiegabile.

## Affidabilità e stabilità

- replay ripetuto;
- soak;
- long drive;
- restart restore;
- queue drain;
- provider failover;
- memory plateau;
- crash-free sessions.

## Coerenza

Esempi:

- `NO_ROUTE` ha stesso significato tra provider;
- Android e iOS mostrano stessa privacy policy;
- DailyPage non pubblica in una piattaforma e resta privata nell'altra;
- distanza usa unità coerenti;
- UTC/storage e timezone presentation separate.

## Semplicità

Semplice non significa assenza di architettura. Significa che ogni livello ha una
responsabilità e un motivo.

Segnali di complicazione:

- wrapper che duplicano API;
- stato globale;
- flag provider sparsi;
- cross-context DB access;
- configuration combinatoria;
- event bus senza ownership;
- microservizi prematuri.

## Manutenibilità

- moduli piccoli;
- test vicini al contratto;
- ADR;
- naming;
- migrations;
- dependency rules;
- no vendor leakage;
- code map.

## Operabilità

- health/capability;
- degraded mode;
- metrics;
- redacted logs;
- feature flags;
- rollback;
- incident playbook;
- user-visible status.

## Maturità feature

Livelli candidati:

```text
0 documented
1 deterministic prototype
2 integrated lab
3 internal field test
4 limited beta
5 supported
```

Una feature può essere livello 3 funzionale ma livello 1 privacy; la maturità
globale è limitata dalla dimensione più critica.

## Review checklist

- behavior;
- limits;
- test;
- performance;
- battery;
- privacy;
- safety;
- offline;
- provider;
- docs;
- rollback.

## Debito

Il debito è accettabile se:

- dichiarato;
- circoscritto;
- con condizione di rimborso;
- non nascosto nel hot path;
- non viola privacy/safety.

## Didattica come qualità

Una codebase didattica deve mostrare:

- perché;
- alternative;
- errori comuni;
- test;
- misure;
- evoluzione.

Non deve però sacrificare il design di produzione per creare esempi artificiali.
I laboratori possono avere reference implementation separate.
