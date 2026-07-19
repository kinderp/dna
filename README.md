# DNA

**DNA** è una piattaforma modulare per mettere in relazione persone, luoghi, intenzioni e servizi attraverso frammenti di profilo condivisibili, matching contestuale, mappe, comunità e diversi canali di comunicazione.

Il progetto nasce dall'esperienza di **TDNA / Travel DNA**, ma il nucleo è progettato per supportare più verticali: viaggio, spesa e acquisti, mobilità, servizi locali, socialità e futuri domini.

## Principi

- **DNA privato per impostazione predefinita**: il profilo completo non viene trasmesso; si condividono frammenti minimali, contestuali, revocabili e con scadenza.
- **Architettura multi-verticale**: Travel, Shopping e gli altri servizi usano capacità comuni senza dipendere gli uni dagli altri.
- **Mappa come interfaccia territoriale**: luoghi, zone, tratte, conversazioni, richieste, offerte e gruppi sono rappresentabili nel navigatore.
- **Comunicazione transport-agnostic**: Internet, Wi-Fi, Wi-Fi Direct/Aware, Bluetooth LE, NFC e LoRa sono adapter intercambiabili con capacità differenti.
- **LoRa come opzione, non dipendenza**: può trasportare trace o rendezvous; i contenuti completi passano normalmente attraverso Wi-Fi, rete mobile o Internet.
- **Coordinamento umano**: chat geografiche, topic, sottoscrizioni e gruppi permettono agli utenti di confrontarsi e organizzarsi direttamente.
- **Privacy, sicurezza e trasparenza by design**.

## Componenti

- **DNA Identity** — identità, pseudonimi e dispositivi.
- **DNA Exchange** — frammenti, consenso, matching e revoca.
- **DNA Discovery** — scoperta di prossimità e rendezvous indipendenti dal trasporto.
- **DNA Commons** — GeoChat, topic, gruppi, sottoscrizioni e moderazione.
- **Geo & Navigation Core** — luoghi, aree, tratte, percorsi e ancore geografiche.
- **Verticali** — TDNA Travel, Shopping DNA e futuri servizi.

## Stato

La baseline v0.1 comprende documentazione, schemi JSON versionati e un simulatore Kotlin/JVM del primo flusso comune:

```text
DNA Profile
  → DNA Fragment
  → DNA Trace
  → Discovery / Compatibility
  → Consent / Rendezvous
  → GeoRoom o azione di un verticale
```

Il simulatore verifica matching Travel e Shopping, consenso, scelta del trasporto, fallback, TTL e deduplicazione. Non è ancora l'SDK Android definitivo.

## Iniziare

```bash
python3 -m pip install -r requirements-dev.txt
./scripts/test-reference.sh
./scripts/run-demo.sh
```

Sono richiesti Python 3, JDK e `kotlinc` nel `PATH`.

## Risorse

- [Documentazione italiana](docs/it/README.md)
- [Schemi JSON v0.1](schemas/v0.1/README.md)
- [Implementazione Kotlin di riferimento](reference/kotlin/README.md)
- [Esempi](examples/v0.1)

## Licenza

Da definire prima della pubblicazione del primo codice riutilizzabile.
