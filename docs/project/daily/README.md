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
5. review round puliti e substantive head;
6. decisioni autonome;
7. decisioni richieste;
8. debito e prossimo passo.

I report non sostituiscono issue, pull request, commit o ADR. Li collegano in una
narrazione stabile anche per gli studenti.

## Report

| Data / sessione | Tema principale | Issue / PR | Esito |
| --- | --- | --- | --- |
| [2026-07-17 — review policy](2026-07-17-review-policy.md) | Due review consecutive obbligatorie, reset e hardening `RouteManeuver`. | [#8](https://github.com/kinderp/tdna/issues/8) / [#9](https://github.com/kinderp/tdna/pull/9) | Implementazione completa; CI e due round puliti da registrare sulla testa finale. |
| [2026-07-17](2026-07-17.md) | Contratti routing provider-neutral, plugin SDK, fake planner e KMP. | [#5](https://github.com/kinderp/tdna/issues/5) / [#6](https://github.com/kinderp/tdna/pull/6) | Mergiata il 17 luglio 2026. |
| [2026-07-16](2026-07-16.md) | Routing Java/Rust, fixture, Dijkstra, A* e contract diff. | [#3](https://github.com/kinderp/tdna/issues/3) / [#4](https://github.com/kinderp/tdna/pull/4) | Mergiata il 17 luglio 2026. |

## Convenzione

Il file principale della data usa:

```text
YYYY-MM-DD.md
```

Se una data contiene più sessioni significative, usare:

```text
YYYY-MM-DD-argomento.md
```

e mantenere questo indice come fonte ufficiale.

## Regola di manutenzione

Alla chiusura di ogni sessione:

- creare o aggiornare il report;
- aggiornare questa tabella;
- collegare issue, PR, commit e CI;
- distinguere fatti verificati da lavoro pianificato;
- registrare finding, fix e conteggio dei round puliti;
- indicare se serve una decisione del maintainer.
