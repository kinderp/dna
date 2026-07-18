# Stato della documentazione

## Stati

- `Foundation complete`: base utilizzabile;
- `Implementation-backed`: collegato a codice, test o processo corrente;
- `Partial`: utile ma incompleto;
- `Planned`: previsto per una milestone futura.

## Capitoli

| Stato | Documento | Nota |
| --- | --- | --- |
| Implementation-backed | `00-regole-operative.md` | PR seriale, due round e merge gated. |
| Foundation complete | `01-visione-prodotto.md` | Visione consolidata. |
| Foundation complete | `02-glossario.md` | Glossario iniziale. |
| Implementation-backed | `03-guida-lettura-documentazione.md` | Percorsi verso Lab, codebase e governance. |
| Implementation-backed | `04-come-contribuire.md` | Branch, PR, review e merge. |
| Foundation complete | `05-tracciabilita-conversazione.md` | Origine dei temi iniziali. |
| Implementation-backed | `06-review-e-merge.md` | Substantive head, reset, autorità e riallineamento. |
| Foundation complete | `10-ddd-bounded-context.md` | Confini strategici v0. |
| Foundation complete | `11-use-case-principali.md` | Use case prioritari. |
| Implementation-backed | `12-stato-funzionalita.md` | Distingue Lab e prodotto. |
| Foundation complete | `20-architettura-generale.md` | Architettura target. |
| Implementation-backed | `21-struttura-repository.md` | Moduli reali e albero target. |
| Implementation-backed | `22-architettura-plugin-provider.md` | SDK, fake e adapter boundaries. |
| Foundation complete | `23-openstreetmap-e-cartografia.md` | Uso corretto di OSM. |
| Foundation complete | `24-routing-e-navigazione.md` | Fondamenti del navigatore. |
| Foundation complete | `25-navigatori-esterni-e-automotive.md` | Companion e auto. |
| Foundation complete | `26-diario-media-pagina-giorno.md` | Diario e condivisione. |
| Foundation complete | `27-presenza-chat-dna.md` | Socialità e privacy. |
| Implementation-backed | `28-stack-linguaggi-e-gui.md` | Java, Rust e KMP. |
| Foundation complete | `29-backend-dati-sync.md` | Architettura dati v0. |
| Implementation-backed | `30-strategia-test.md` | Contract, rendering, replay, progress, matching e state machine. |
| Implementation-backed | `31-gps-replay-e-fixture.md` | Fixture replay e separazione I/O/core. |
| Implementation-backed | `32-performance-budget.md` | Scene/delta e benchmark diagnostici. |
| Foundation complete | `33-privacy-security-driving-safety.md` | Threat model iniziale. |
| Implementation-backed | `34-debugging-e-strumenti.md` | Comandi reali. |
| Foundation complete | `35-qualita-prodotto-software.md` | Matrice qualità. |
| Implementation-backed | `36-licenze-dati-supply-chain.md` | Policy più Wrapper/Action integrity. |
| Implementation-backed | `37-build-riproducibile-gradle-wrapper.md` | Bootstrap, checksum, trust model e upgrade. |
| Implementation-backed | `40-mappa-codice-e-stati.md` | Percorsi reali e target. |
| Implementation-backed | `41-tracepoint-model-v0.md` | Tracepoint dei Lab. |
| Implementation-backed | `42-traveldna-lab-roadmap.md` | Build-bootstrap più sette scenari navigation. |
| Implementation-backed | `43-reference-routing-java-rust.md` | Algoritmo Java/Rust. |
| Implementation-backed | `44-contratti-routing-e-fake-provider.md` | Contratti KMP e fake planner. |
| Implementation-backed | `45-map-scene-e-fake-renderer.md` | Scena, delta e fake renderer. |
| Implementation-backed | `46-location-sample-e-replay-deterministico.md` | Sample, clock e replay. |
| Implementation-backed | `47-posizione-matched-e-route-progress.md` | Matched position, tracker, binding e benchmark. |
| Implementation-backed | `48-porta-map-matching-e-fake-deterministico.md` | Porta route-bound, esiti, fake, testkit e pipeline. |
| Implementation-backed | `49-off-route-missed-exit-e-reroute.md` | Evidenza, state machine, reroute e replacement. |
| Implementation-backed | `50-registro-milestone.md` | Milestone e slice tracciate. |
| Foundation complete | `51-roadmap-librerie-open-source.md` | Sostituzione progressiva. |
| Foundation complete | `52-matrice-tecnologie-decisioni.md` | Scelte e alternative. |
| Implementation-backed | `53-riferimenti-tecnici.md` | Fonti tecniche e build bootstrap. |
| Planned | `54-spike-lora-roadmap.md` | Nessuna decisione prima dello spike. |

## Documenti di progetto vivi

| Stato | Documento | Nota |
| --- | --- | --- |
| Implementation-backed | `../project/development-status.md` | Milestone e prossimi gate. |
| Implementation-backed | `../project/foundation-v0-closure.md` | Deliverable, prove, limiti e debito residuo. |
| Implementation-backed | `../project/daily/README.md` | Indice storico dei report. |
| Implementation-backed | `../project/daily/2026-07-17-location-replay.md` | LocationSample, replay e benchmark. |
| Implementation-backed | `../project/daily/2026-07-17-route-progress.md` | Route progress e merge PR #18. |
| Implementation-backed | `../project/daily/2026-07-18-map-matcher-port.md` | Porta matching e merge PR #20. |
| Implementation-backed | `../project/daily/2026-07-18-missed-exit-reroute.md` | Off-route e merge PR #22. |
| Implementation-backed | `../project/daily/2026-07-18-gradle-wrapper-foundation-closure.md` | Wrapper, supply chain e closure PR #24. |
| Implementation-backed | `lab/scenarios/gradle-wrapper-riproducibile.md` | Lab engineering. |
| Implementation-backed | `lab/scenarios/reference-routing-java-rust.md` | Lab navigation 1. |
| Implementation-backed | `lab/scenarios/routing-contracts-fake-provider.md` | Lab navigation 2. |
| Implementation-backed | `lab/scenarios/map-scene-fake-renderer.md` | Lab navigation 3. |
| Implementation-backed | `lab/scenarios/location-replay-deterministico.md` | Lab navigation 4. |
| Implementation-backed | `lab/scenarios/route-progress-tracker.md` | Lab navigation 5. |
| Implementation-backed | `lab/scenarios/map-matching-fake-provider.md` | Lab navigation 6. |
| Implementation-backed | `lab/scenarios/navigation-missed-exit-reroute.md` | Lab navigation 7. |

Aggiornare questa tabella quando codice o processi rendono obsoleta una
spiegazione.
