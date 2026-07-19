# DNA

**DNA** è una piattaforma modulare per mettere in relazione persone, luoghi, intenzioni e servizi attraverso frammenti di profilo condivisibili, matching contestuale, mappe, comunità e diversi canali di comunicazione.

Il progetto nasce dall'esperienza di **TDNA / Travel DNA**, ma il nucleo è progettato per supportare più verticali: viaggio, spesa e acquisti, mobilità, servizi locali, socialità e futuri domini.

## Principi

- **DNA privato per impostazione predefinita**: il profilo completo non viene trasmesso; si condividono frammenti minimali, contestuali, revocabili e con scadenza.
- **Architettura multi-verticale**: Travel, Shopping e gli altri servizi usano capacità comuni senza dipendere gli uni dagli altri.
- **Mappa come interfaccia territoriale**: luoghi, zone, tratte, conversazioni, richieste, offerte e gruppi sono rappresentabili nel navigatore.
- **Comunicazione transport-agnostic**: Internet, Wi-Fi, Wi-Fi Direct/Aware, Bluetooth LE, NFC e LoRa sono adapter intercambiabili con capacità differenti.
- **LoRa come opzione, non dipendenza**: particolarmente utile per discovery e rendezvous a basso consumo e lungo raggio; i contenuti completi passano normalmente attraverso Wi-Fi, rete mobile o Internet.
- **Coordinamento umano**: chat geografiche, topic, sottoscrizioni e gruppi permettono agli utenti di confrontarsi e organizzarsi direttamente.
- **Privacy, sicurezza e trasparenza by design**.

## Componenti previsti

- **DNA Identity** — identità, pseudonimi e dispositivi.
- **DNA Exchange** — frammenti, consenso, matching e revoca.
- **DNA Discovery** — scoperta di prossimità e rendezvous indipendenti dal trasporto.
- **DNA Commons** — GeoChat, topic, gruppi, sottoscrizioni e moderazione.
- **Geo & Navigation Core** — luoghi, aree, tratte, percorsi e ancore geografiche.
- **Verticali** — TDNA Travel, Shopping DNA e futuri servizi.

## Documentazione

La documentazione architetturale iniziale sarà pubblicata in [`docs/it`](docs/it/README.md).

## Stato

Repository in fase di fondazione e definizione architetturale. Il primo obiettivo è validare il flusso:

```text
DNA Profile
  → DNA Fragment
  → DNA Trace
  → Discovery / Compatibility
  → Rendezvous
  → GeoRoom o azione di un verticale
```

## Licenza

Da definire prima della pubblicazione del primo codice riutilizzabile.
