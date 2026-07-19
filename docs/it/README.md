# Documentazione italiana di DNA

Questa cartella raccoglie la visione, le decisioni architetturali, la roadmap e lo stato dell'implementazione iniziale della piattaforma DNA.

## Ordine di lettura

1. [Visione e principi](00-visione-e-principi.md)
2. [Architettura della piattaforma](01-architettura-piattaforma.md)
3. [Discovery e adapter di comunicazione](02-discovery-e-adapter-di-comunicazione.md)
4. [DNA Commons, GeoChat e mappa](03-dna-commons-geochat-mappa.md)
5. [Shopping DNA](04-shopping-dna.md)
6. [Roadmap](05-roadmap.md)
7. [Privacy e sicurezza](06-privacy-sicurezza.md)
8. [Implementazione di riferimento v0.1](07-implementazione-di-riferimento.md)

## Contratti ed esempi

- [Schemi JSON v0.1](../../schemas/v0.1/README.md)
- [Implementazione Kotlin di riferimento](../../reference/kotlin/README.md)
- [Esempi v0.1](../../examples/v0.1)

## Decisioni architetturali

- [ADR-0001 — Discovery indipendente dal trasporto](adr/0001-transport-agnostic-discovery.md)

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

## Stato dei documenti

Questi testi descrivono una **baseline v0.1**. Le interfacce e i nomi non sono ancora API stabili. Ogni decisione irreversibile o con impatto trasversale dovrà essere registrata mediante ADR.
