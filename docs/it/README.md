# Documentazione italiana di DNA

Questa cartella raccoglie visione, decisioni architetturali e roadmap iniziale della piattaforma DNA.

## Prima di contribuire

- [Regole operative comuni](../governance/00-operational-rules.md)
- [Review e merge seriale](../governance/01-review-and-merge.md)
- [Git nel monorepo DNA](../governance/02-git-monorepo.md)
- [Manifest della migrazione TDNA](../migration/tdna-import.md)
- [`AGENTS.md`](../../AGENTS.md)

## Ordine di lettura

1. [Visione e principi](00-visione-e-principi.md)
2. [Architettura della piattaforma](01-architettura-piattaforma.md)
3. [Discovery e adapter di comunicazione](02-discovery-e-adapter-di-comunicazione.md)
4. [DNA Commons, GeoChat e mappa](03-dna-commons-geochat-mappa.md)
5. [Shopping DNA](04-shopping-dna.md)
6. [Roadmap](05-roadmap.md)
7. [Privacy e sicurezza](06-privacy-sicurezza.md)
8. [Implementazione di riferimento](07-implementazione-di-riferimento.md)
9. [Alfred come Observation e Correlation Runtime per DNA](08-alfred-observation-correlation-runtime.md)
10. [Architettura delle esperienze DNA: mobile, Android Auto e Automotive](09-architettura-esperienze-app-e-automotive.md)

La documentazione tecnica e didattica Travel vive in `domains/travel/docs`. Travel è una directory ordinaria del monorepo; consultare la [guida Git](../governance/02-git-monorepo.md).

## Decisioni architetturali

- [ADR-0001 — Discovery indipendente dal trasporto](adr/0001-transport-agnostic-discovery.md)
- [ADR-0002 — Alfred come piano asincrono di osservazione e correlazione](adr/0002-alfred-observation-plane.md)
- [ADR-0003 — Un ecosistema, più esperienze specializzate](adr/0003-multi-surface-product-architecture.md)
- [ADR-0004 — Monorepo DNA e migrazione history-aware di TDNA](adr/0004-monorepo-and-tdna-migration.md)

## Contratti eseguibili

- [JSON Schema v0.1](../../schemas/v0.1/README.md)
- [Esempi v0.1](../../examples/v0.1/)
- [Reference implementation Kotlin](../../reference/kotlin/README.md)

## Glossario minimo

| Termine | Significato |
|---|---|
| **DNA Profile** | Profilo completo e privato di una persona, organizzazione, luogo o servizio. |
| **DNA Fragment** | Sottoinsieme esplicitamente condivisibile del profilo, limitato per scopo, destinatari e durata. |
| **DNA Trace** | Segnale minimo, pseudonimo e temporaneo usato per discovery e pre-matching. |
| **DNA Intent** | Bisogno, disponibilità o obiettivo attivo: viaggio, acquisto, incontro, consegna, richiesta. |
| **Compatibility** | Risultato spiegabile del confronto tra trace, fragment, intenti e contesto. |
| **Rendezvous** | Passaggio sicuro dalla scoperta al canale con cui proseguire lo scambio. |
| **GeoAnchor** | Collegamento di un'entità a un punto, un'area, una tratta o una zona dinamica. |
| **GeoRoom** | Conversazione o spazio di coordinamento associato a un GeoAnchor. |
| **Transport Adapter** | Implementazione di un mezzo di comunicazione: Internet, BLE, Wi-Fi, NFC, LoRa o altri. |
| **Observation** | Rappresentazione di un fatto, misura, inferenza, azione o outcome con fonte e validità esplicite. |
| **Correlation Pattern** | Regola versionata che collega osservazioni nel tempo e tra domini producendo un risultato spiegabile. |
| **DNA-Alfred Bridge** | Adapter asincrono che minimizza e traduce eventi DNA nel piano di osservazione Alfred. |
| **Surface** | Esperienza di presentazione specifica per telefono, Android Auto, Android Automotive OS, voce o notifiche. |
| **Domain Lens** | Vista che filtra la piattaforma comune secondo Travel, Shopping, Social, Economy o un altro dominio. |
| **Imported Travel history** | Cronologia TDNA raggiungibile dal monorepo e codice corrente sotto `domains/travel`. |

## Stato dei documenti

Questi testi descrivono una **baseline v0.1**. Le interfacce e i nomi non sono ancora API stabili. Ogni decisione irreversibile o con impatto trasversale deve essere registrata mediante ADR.
