# Indice dei report giornalieri

Questa cartella conserva un report Markdown per ogni giornata o sessione
autonoma significativa. L'indice permette di ricostruire nel tempo non soltanto
cosa è stato scritto, ma quale rischio è stato affrontato, quali prove sono state
raccolte e quali decisioni restano aperte.

## Come leggere i report

Per ogni data controllare nell'ordine:

1. obiettivo della giornata;
2. codice e documentazione prodotti;
3. test e CI realmente eseguiti;
4. finding emersi durante la review;
5. decisioni prese autonomamente;
6. decisioni richieste al maintainer;
7. debito e prossimo passo eseguibile.

I report non sostituiscono issue, pull request, commit o ADR. Li collegano in una
narrazione stabile pensata anche per gli studenti.

## Report

| Data | Tema principale | Issue / PR | Esito |
| --- | --- | --- | --- |
| [2026-07-17](2026-07-17.md) | Contratti routing provider-neutral, plugin SDK, fake planner e Kotlin Multiplatform. | [#5](https://github.com/kinderp/tdna/issues/5) / [#6](https://github.com/kinderp/tdna/pull/6) | In review; CI e stato finale sono registrati nel report. |
| [2026-07-16](2026-07-16.md) | Primo routing eseguibile in Java/Rust, fixture, Dijkstra, A* e contract test. | [#3](https://github.com/kinderp/tdna/issues/3) / [#4](https://github.com/kinderp/tdna/pull/4) | PR mergiata il 17 luglio 2026. |

## Convenzione dei nomi

```text
YYYY-MM-DD.md
```

Se una giornata contiene più sessioni, il report resta unico e viene aggiornato
cronologicamente. Se in futuro diventa troppo grande, si potranno usare sezioni o
file `YYYY-MM-DD-a.md`, mantenendo qui l'ordine ufficiale.

## Regola di manutenzione

Alla chiusura di ogni sessione autonoma:

- creare o aggiornare il report della data;
- aggiornare questa tabella;
- collegare issue, PR, commit e CI;
- distinguere fatti verificati da lavoro pianificato;
- indicare esplicitamente se serve una decisione del maintainer.
