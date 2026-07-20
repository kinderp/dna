# Travel DNA documentation foundation v0 — report

## Scopo

Questo pacchetto trasforma la progettazione iniziale in una base documentale
utilizzabile per:

- discutere il prodotto;
- insegnare app mobile, mappe, routing, navigazione e prestazioni;
- avviare il repository senza perdere il ragionamento architetturale;
- guidare studenti e contributori attraverso percorsi di lettura;
- aprire milestone, issue e pull request verificabili.

## Copertura

Il corpus comprende:

- visione, linguaggio di dominio e use case;
- DDD e bounded context;
- architettura mobile, backend, dati e deployment;
- contratti canonici, adapter, plugin, capability, fallback e shadow mode;
- OpenStreetMap, tile, ricerca, POI e licenze;
- rendering, routing, guidance, map matching, rerouting e voce;
- navigatori esterni, Android Auto e CarPlay;
- diario, fotografie, pensieri e Pagina del giorno;
- presenza approssimata, chat durante la guida e Cartoline DNA;
- Kotlin, Java, Swift, Rust, KMP, FFI e GUI native;
- local-first, sincronizzazione e messaggistica;
- test, replay GPS, benchmark, energia, privacy e sicurezza;
- Travel DNA Lab, tracepoint e scenari didattici;
- roadmap di milestone e librerie open source;
- template GitHub, ADR e regole operative;
- roadmap separata per lo spike LoRa.

## Decisioni già stabilizzate

- Travel è la prima verticale.
- Il diario offre valore indipendente dalla rete sociale.
- Posizione e identità degli altri utenti sono approssimate e minimizzate.
- La chat rimane attiva con navigatori esterni, cambiando superficie in base al
  ruolo conducente/passeggero.
- I navigatori esterni sono una modalità di prima classe.
- MapLibre, Valhalla e Ferrostar sono la baseline open source dietro adapter.
- Il core dipende da contratti Travel DNA.
- UI critica nativa, logica applicativa condivisa selettivamente.
- Rust entra prima nei core deterministici e misurabili.
- Lo stato mobile è local-first.
- Documentazione, test, replay e benchmark fanno parte della Definition of Done.

## Decisioni ancora aperte

- framework backend JVM: Java/Spring o Kotlin/Ktor;
- tecnologia definitiva per binding Rust;
- provider iniziale di tile, geocoding e ricerca;
- modello di licenza del progetto;
- eventuale valore di LoRa/LoRaWAN o accessori radio;
- soglie prestazionali finali dopo i primi prototipi su dispositivi reali.

## Verifiche eseguite sul pacchetto

- tutti i link Markdown interni risolvono;
- i code fence Markdown risultano bilanciati;
- non risultano whitespace finali nei file Markdown;
- i template issue contengono il front matter GitHub richiesto;
- la documentazione distingue decisioni, proposte, roadmap e non-obiettivi;
- non sono incluse fixture con dati personali reali.

## Come proseguire

La prima milestone operativa consigliata è `Foundations and Travel DNA Lab v0`,
descritta in `docs/it/50-registro-milestone.md`. Il primo codice dovrebbe
realizzare una route canonica finta e un replay GPS deterministico prima di
integrare traffico, chat reale o dati di produzione.
