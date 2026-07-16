# ADR-0001 — Kotlin Multiplatform per il core, UI critica nativa

- Status: Accepted
- Date: 2026-07-16
- Decision owners: Travel DNA maintainers

## Contesto

Travel DNA deve funzionare su Android e iOS e integrare mappe native, posizione
in background, audio, notifiche, Android Auto e CarPlay. Una parte consistente
della logica applicativa — viaggio, diario, consenso, sincronizzazione e
contratti — è però comune.

Condividere tutto ridurrebbe duplicazione apparente, ma potrebbe introdurre un
ulteriore runtime e bridge nel percorso critico della navigazione. Duplicare
tutto renderebbe invece più difficile mantenere coerenti dominio e protocolli.

## Forze

- accesso idiomatico alle piattaforme;
- controllo di rendering, lifecycle e prestazioni;
- condivisione reale della semantica applicativa;
- codebase studiabile;
- possibilità di usare Java, Kotlin, Swift e Rust con ruoli chiari;
- evitare un'unica enorme UI cross-platform difficile da profilare.

## Alternative considerate

### Tutto nativo e duplicato

Pro: massimo controllo locale. Contro: duplicazione di dominio, sincronizzazione,
validazione e test; rischio di semantiche divergenti.

### Flutter

Pro: UI condivisa e tooling maturo. Contro: confini aggiuntivi con mappe e SDK
automotive nativi; meno diretto per gli obiettivi didattici Kotlin/Java/Swift.

### React Native

Pro: velocità di sviluppo per UI tradizionali. Contro: dipendenze e bridge per
mappe, navigazione, background e Rust; stack meno coerente con gli obiettivi.

### Compose Multiplatform per tutta la UI

È una possibilità futura, ma nella baseline v0 aumenta il rischio sulla schermata
di guida iOS prima di avere misure reali.

## Decisione

- Kotlin Multiplatform condivide modelli, use case, policy, sincronizzazione,
  contratti plugin e presentation model non legati al frame loop.
- Android usa Kotlin e Jetpack Compose.
- iOS usa Swift e SwiftUI, con UIKit dove è preferibile per la mappa o componenti
  critici.
- MapLibre viene integrato come view nativa persistente.
- Nessun flusso a 30/60 fps deve attraversare KMP per principio; ogni eccezione
  richiede benchmark.

## Conseguenze

Positive:

- dominio e contratti condivisi;
- UI idiomatica;
- accesso diretto ad Android Auto e CarPlay;
- profiling per piattaforma più chiaro.

Negative:

- due presentation layer;
- build e debugging multipiattaforma più complessi;
- necessità di wrapper Swift per API KMP poco idiomatiche;
- disciplina per evitare di spostare troppa UI nel modulo shared.

## Verifica

La prima vertical slice deve mostrare la stessa `RoutePlan` canonica su Android e
iOS senza esporre tipi MapLibre al core condiviso. I bridge devono essere
misurati con snapshot piccoli e con una route di riferimento.

## Condizioni di riesame

- maturità e misure di Compose Multiplatform sulla nostra UI;
- costo reale del mantenimento di due presentation layer;
- limiti riscontrati nell'esportazione Swift del core KMP.
