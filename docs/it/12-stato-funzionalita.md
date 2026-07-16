# Stato delle funzionalità

Questo documento impedisce di confondere visione, decisione architetturale e
software già funzionante.

## Legenda

- `documented`: comportamento e contratto descritti, nessun codice.
- `prototype planned`: previsto nella prima milestone tecnica.
- `future`: direzione approvata ma non pianificata ora.
- `open`: richiede discussione o spike.
- `non-goal`: escluso dal perimetro corrente.

## Stato corrente

| Funzionalità | Stato | Nota |
| --- | --- | --- |
| Visione Travel DNA | documented | Diario, guida, navigazione e socialità. |
| Modello DDD | documented | Bounded context iniziali. |
| Contratti plugin | documented | Nessuna implementazione. |
| MapLibre adapter | prototype planned | Dopo modelli canonici e fake renderer. |
| Valhalla adapter | prototype planned | Via routing gateway, non chiamata diretta dal dominio. |
| Ferrostar adapter | prototype planned | Incapsulato; API beta da proteggere. |
| Navigatore esterno | prototype planned | Deep link e sessione Companion. |
| Percorso ombra | prototype planned | Prima versione senza promesse di precisione forte. |
| Diario automatico | prototype planned | Fixture e replay prima del GPS reale. |
| Pagina del giorno | prototype planned | Foto e pensieri locali. |
| Chat reale | future | Prima fake/in-memory e policy di guida. |
| Android Auto messaging | future | Dopo chat mobile stabile. |
| CarPlay messaging/widget | future | Richiede capability e review Apple. |
| Road presence | future | Richiede backend e threat model. |
| Cartolina DNA | future | Dopo diario e privacy model. |
| Mappe offline | future | Richiede provider/licenza/distribuzione. |
| Routing offline | future | Valhalla locale o altro provider da valutare. |
| Guidance core Rust proprio | future | Dopo replay e shadow comparison. |
| Java reference router | prototype planned | Laboratorio didattico, non produzione. |
| Traffico crowdsourced | future | Richiede massa critica e dati. |
| Contenuti Touring | open | Solo partnership/licenza. |
| LoRa/LoRaWAN | open | Spike dedicato, nessuna decisione. |
| Profili minori | non-goal | Fuori MVP. |
| Verticali shopping/study | non-goal | Fuori Travel DNA iniziale. |

## Regola di comunicazione

Non dire “Travel DNA supporta” una capacità finché non esistono:

- implementazione;
- test;
- documentazione corrente;
- criteri di degraded mode;
- review privacy/performance quando necessaria.

Usare formulazioni come:

```text
è documentato
è pianificato
è in prototipo
è sperimentale
è disponibile
```

con significato esplicito.
