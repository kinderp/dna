# Regole operative di DNA

Questo documento è la costituzione pratica del monorepo. Le procedure dettagliate di review sono in [01-review-and-merge.md](01-review-and-merge.md). Durante la fase A della migrazione Travel, i comandi Git obbligatori sono spiegati nella [guida ai submodule](02-git-submodules.md).

## 1. Obiettivo

DNA è contemporaneamente una piattaforma, un insieme di domini, una famiglia di esperienze client e una codebase didattica. Il metodo deve produrre codice, contratti, prove e decisioni ricostruibili.

## 2. Fonti di verità

```text
Discussion       -> ragionamento aperto
ADR              -> decisione specifica
Platform docs    -> spiegazione trasversale autorevole
Domain docs      -> spiegazione specifica del bounded context
Issue            -> lavoro da svolgere
Pull request     -> unica modifica concreta attiva
PR review ledger -> evidenza dei round sullo stesso head
Test             -> evidenza eseguibile
Benchmark        -> evidenza misurata
Git              -> storia cronologica
```

Una chat o Discussion non è un contratto. Una scelta consolidata entra in ADR e documenti stabili.

## 3. Bootstrap della sessione

Prima di modificare codice o contratti:

1. leggere `AGENTS.md`;
2. leggere questo documento e la guida review;
3. leggere l'indice documentale;
4. leggere issue, roadmap e contratto del componente;
5. aprire codice e test reali;
6. verificare SHA di `main`, PR aperte e dipendenze;
7. per Travel, leggere la guida ai submodule e verificare il commit TDNA registrato nel manifest di migrazione.

## 4. Scheda del task

Ogni passo non banale dichiara use case, bounded context, superfici, contratto, provider, hot path, dati personali, test, documentazione, scope e base Git.

## 5. Vertical slice

Una slice deve essere coerente, reviewabile e reversibile e deve produrre una prova osservabile. Sono vietati sia i mega-refactor che le PR vuote o artificialmente frammentate.

## 6. Una sola PR

Nel repository può esistere al massimo una PR aperta. Issue e ricerca future possono essere preparate, ma la loro PR attende il merge o la chiusura della PR corrente.

## 7. Confini dei domini

- `platform/` possiede capacità trasversali;
- `domains/travel` possiede Travel;
- i futuri `domains/shopping`, `domains/social` e `domains/economy` possiedono le rispettive regole;
- `experiences/` compone casi d'uso per una superficie;
- `ui/` contiene design system e componenti condivisi;
- nessun dominio legge direttamente storage o internals di un altro;
- gli scambi avvengono tramite contratti ed eventi.

## 8. Provider e adapter

Map provider, navigatori esterni, pagamenti, sensori e trasporti di comunicazione restano dietro porte. Un adapter traduce e non decide la logica di prodotto.

## 9. Percorsi caldi

Una modifica a navigazione, rendering, discovery, delivery messaggi o registrazione eventi dichiara allocazioni, I/O, lock, rete e serializzazione introdotti. Le operazioni pesanti restano fuori dal percorso caldo salvo prova misurata.

## 10. Privacy e sicurezza

Ogni funzione che usa posizione, identità, contatti, foto, ricevute, finanza o messaggi dichiara dato, precisione, scopo, durata, visibilità, cancellazione, condivisione, abuso e revoca.

## 11. Test e benchmark

Scegliere la prova in base al contratto: unit, property, state-machine, contract, replay, integration, UI, performance o field audit. Un test funzionale non dimostra prestazioni; un benchmark non dimostra correttezza.

## 12. Documentazione

Le decisioni trasversali vivono in `docs/`; quelle specifiche del dominio vivono vicino al dominio. Durante la migrazione TDNA ogni documento è classificato come autorevole, migrato, storico o da rimuovere. Non mantenere due copie entrambe dichiarate autorevoli.

## 13. CI e costi

- azioni esterne bloccate a SHA completi e allowlist;
- controlli leggeri automatici;
- build Travel solo quando cambia il riferimento o l'infrastruttura Travel;
- emulatore Android e prove costose solo manuali o su gate esplicito;
- artifact bounded e retention breve;
- nessun workflow attivato su ogni commit senza necessità.

## 14. Merge

Ogni PR che distribuisce una modifica richiede CI applicabile verde, due round puliti consecutivi sullo stesso substantive head, autorizzazione e expected-head guard.
