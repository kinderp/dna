# Travel DNA

Travel DNA è un progetto didattico e di prodotto per costruire una guida-diario
sociale dei viaggiatori, capace di funzionare sia con navigatori esterni sia con
una futura navigazione integrata.

Il progetto nasce da quattro idee unite:

1. il viaggio deve avere valore anche quando non si incontra nessun altro utente;
2. il diario deve raccogliere automaticamente percorso, soste e luoghi, lasciando
   all'utente il controllo su fotografie, pensieri e condivisione;
3. gli incontri lungo il viaggio devono diventare consigli, saluti, conversazioni
   e Cartoline DNA senza trasformare l'app in uno strumento di sorveglianza;
4. l'intera codebase deve essere studiabile da studenti e nuovi contributori.

## Stato

Questo repository è uno **scheletro documentale v0**. Contiene decisioni,
contratti, percorsi di lettura, scenari Lab e roadmap. Non contiene ancora un
navigatore di produzione.

La documentazione consolida tutte le decisioni emerse durante la progettazione
iniziale: visione del prodotto, diario, OpenStreetMap, MapLibre, Valhalla,
Ferrostar, navigatori esterni, Android Auto, CarPlay, chat durante la guida,
architettura a plugin, Kotlin, Java, Swift, Rust, DDD, TDD, Unified Process
leggero, performance, privacy e progetto didattico.

## Da dove iniziare

- Studente o nuovo lettore: [docs/it/03-guida-lettura-documentazione.md](docs/it/03-guida-lettura-documentazione.md)
- Contributore: [docs/it/00-regole-operative.md](docs/it/00-regole-operative.md)
- Architettura: [docs/it/20-architettura-generale.md](docs/it/20-architettura-generale.md)
- Tecnologie: [docs/it/52-matrice-tecnologie-decisioni.md](docs/it/52-matrice-tecnologie-decisioni.md)
- Navigazione: [docs/it/24-routing-e-navigazione.md](docs/it/24-routing-e-navigazione.md)
- Primo percorso didattico: [docs/it/42-traveldna-lab-roadmap.md](docs/it/42-traveldna-lab-roadmap.md)
- Decisioni architetturali: [docs/adr/README.md](docs/adr/README.md)
- Scenari Lab: [docs/it/lab/README.md](docs/it/lab/README.md)
- Rapporto della fondazione: [docs/project/FOUNDATION-REPORT.md](docs/project/FOUNDATION-REPORT.md)

## Principi

```text
Travel DNA dipende dai propri contratti, non dai provider.

Il navigatore è una capacità del prodotto, non l'intero prodotto.

La chat rimane attiva durante il viaggio, ma la superficie cambia in base al
contesto di guida.

Il diario è privato per impostazione predefinita; la condivisione è esplicita.

Ogni astrazione e ogni linguaggio devono pagare il proprio costo.

Una funzione corretta ma lenta, insicura o non documentata non è conclusa.
```

## Linguaggio della documentazione

- codice, API, commenti, commit e pull request: inglese;
- documentazione didattica in `docs/it`: italiano;
- nomi del dominio: inglese nel codice, con glossario italiano;
- riferimenti esterni: documentazione ufficiale quando disponibile.

## Licenza

Il modello di licenza non è ancora stato deciso. Prima di accettare contributi
esterni di codice deve essere chiuso
[ADR-0009](docs/adr/0009-project-licensing-model.md) e aggiunto il file
`LICENSE` appropriato.
