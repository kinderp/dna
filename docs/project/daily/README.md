# Indice dei report giornalieri

Questa cartella conserva un report Markdown per ogni giornata o sessione
autonoma significativa. L'indice permette di ricostruire cosa è stato fatto,
quale rischio è stato affrontato, quali prove sono state raccolte e quali
decisioni restano aperte.

## Come leggere i report

Per ogni data controllare:

1. obiettivo;
2. codice e documentazione;
3. test e CI realmente eseguiti;
4. finding di review;
5. decisioni autonome;
6. decisioni richieste;
7. debito e prossimo passo.

I report non sostituiscono issue, pull request, commit o ADR. Li collegano in una
narrazione stabile anche per gli studenti.

## Report

| Data | Tema principale | Issue / PR | Esito |
| --- | --- | --- | --- |
| [2026-07-17](2026-07-17.md) | Contratti routing provider-neutral, plugin SDK, fake planner e KMP. | [#5](https://github.com/kinderp/tdna/issues/5) / [#6](https://github.com/kinderp/tdna/pull/6) | Implementazione e review complete; merge del maintainer pendente. |
| [2026-07-16](2026-07-16.md) | Routing Java/Rust, fixture, Dijkstra, A* e contract diff. | [#3](https://github.com/kinderp/tdna/issues/3) / [#4](https://github.com/kinderp/tdna/pull/4) | Mergiata il 17 luglio 2026. |

## Convenzione

```text
YYYY-MM-DD.md
```

Se una data contiene più sessioni, il report viene aggiornato in ordine
cronologico. Se diventa troppo grande si potranno introdurre suffissi, mantenendo
questo indice come fonte ufficiale.

## Regola di manutenzione

Alla chiusura di ogni sessione:

- creare o aggiornare il report;
- aggiornare questa tabella;
- collegare issue, PR, commit e CI;
- distinguere fatti verificati da lavoro pianificato;
- indicare se serve una decisione del maintainer.
