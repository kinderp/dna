# Travel DNA Lab

Il Lab collega un'azione comprensibile a uno studente con dati, stati, moduli,
tracepoint logici, test, prestazioni e proprietà di sicurezza.

## Come leggere uno scenario

1. leggere `41-tracepoint-model-v0.md`;
2. aprire lo scenario;
3. eseguire o ispezionare la fixture indicata;
4. seguire il percorso logico e, quando esiste, quello delle funzioni reali;
5. confrontare output e test;
6. rispondere alle domande di ripasso;
7. provare una variante senza modificare il contratto stabile.

## Scenari iniziali

| Scenario | Cosa insegna |
| --- | --- |
| [Route canonica](scenarios/render-canonical-route.md) | Separazione renderer/modello/provider. |
| [Uscita mancata e ricalcolo](scenarios/navigation-missed-exit-reroute.md) | GPS, map matching, progress, off-route e reroute. |
| [Chat con navigatore esterno](scenarios/chat-with-external-navigation.md) | Background, push e superficie sicura. |
| [Pagina del giorno](scenarios/daily-page-photos-thoughts.md) | Eventi viaggio, media, pensieri e privacy. |
| [Scambio DNA](scenarios/dna-exchange-privacy.md) | Minimizzazione, consenso e proiezione condivisa. |

## Regola

Uno scenario `stable-doc` non implica che il codice esista già. Implica che il
percorso didattico e il vocabolario sono abbastanza chiari da guidare una futura
implementazione. Lo stato dei test deve dichiarare `existing`, `missing` e
`future` senza fingere copertura.
