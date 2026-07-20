# Review, due round puliti e flusso di merge seriale

## Scopo

Nessuna pull request di Travel DNA può distribuire una modifica senza:

- CI richiesta verde;
- due review round consecutivi senza nuovi finding;
- stesso substantive head SHA;
- autorità di merge esplicita;
- rispetto della regola con al massimo una PR aperta.

La regola evita due errori:

```text
review trova un problema
-> fix
-> merge immediato senza riesaminare il fix
```

```text
PR A ancora aperta
-> PR B costruita sopra A
-> A cambia o viene mergiata diversamente
-> B contiene diff, storia e review ambigui
```

## Vocabolario

### Substantive head

È il commit più recente che modifica codice, test, contratti, fixture, workflow,
documentazione stabile o report tecnico della slice. I due round puliti devono
riferirsi allo stesso SHA.

### Review round

È una lettura distinta della PR corrente con un focus dichiarato. Non è un rerun
della CI, una lettura superficiale del solo ultimo file o la ripetizione testuale
del round precedente.

### Finding

È un problema che richiede una modifica sostanziale o una decisione esplicita,
per esempio un'invariante mancante, un provider che esce dall'adapter, input non
bounded, documento errato, rischio privacy o workflow incompleto.

### PR attiva

È l'unica PR aperta nel repository. Non può esistere una seconda PR finché la
prima non è stata mergiata o chiusa senza merge.

### Chiusura amministrativa

È la chiusura senza merge di una PR accidentale, duplicata, stacked o abbandonata.
Non equivale al completamento della slice e non autorizza applicazione diretta a
`main`.

## Perché al massimo una PR aperta

Il flusso seriale rende verificabili base, diff, CI, finding, ordine delle
dipendenze, report e responsabilità del merge.

```text
nessuna PR aperta
-> branch dal main corrente
-> una draft PR
-> review/fix/CI
-> due round puliti
-> merge o chiusura senza merge
-> verifica nuovo main
-> branch successivo
```

Preparare issue future è consentito. Aprire PR stacked o placeholder non lo è.
Le regole correnti non prevedono eccezioni per PR parallele.

## Fonti di evidenza

### Ledger autorevole della PR

Timeline delle review e descrizione PR conservano:

```text
head SHA
risk level
focus
file e contratti controllati
CI e test osservati
finding oppure no new findings
clean-round count
```

### Report storico nel repository

Il report Markdown committato prima dei round finali contiene contesto, issue,
PR, finding e fix già avvenuti, substantive head previsto, focus dei round e link
al ledger.

Non viene modificato dopo i round soltanto per copiarne gli esiti. Una successiva
PR documentale può riconciliare report, CI finale e merge commit.

## Sequenza normativa

### 1. Inventario e base

Prima del branch:

1. verificare che non esistano PR aperte;
2. chiudere o risolvere eventuali PR accidentali o stacked;
3. leggere il nuovo SHA di `main`;
4. verificare dipendenze della issue;
5. creare il branch da quel `main`.

### 2. PR draft

La PR nasce draft e contiene use case, scope, non-obiettivi, rischio, base `main`,
conferma di essere l'unica PR, contratti, verifiche, documentazione e reviewer
focus.

### 3. Baseline verde

Prima di dichiarare pulito un round devono essere verdi i controlli applicabili
sul head corrente. La review può iniziare con CI in corso, ma l'esito resta
pendente finché la CI non termina.

### 4. Review round

Ogni round viene registrato:

```text
Review round N
Head SHA:
Risk level:
Focus:
Files/contracts inspected:
CI/test evidence:
Findings:
Outcome:
Consecutive clean rounds:
```

### 5. Se esiste un finding

Il finding viene descritto, corretto, protetto da prova quando applicabile e
verificato dalla CI sul nuovo head. Il conteggio torna a zero.

### 6. Due round consecutivi puliti

Dopo l'ultimo commit sostanziale:

```text
Round A -> no new findings -> clean count 1
Round B -> no new findings -> clean count 2
```

Focus consigliato `R2`:

```text
Round A:
contratti, correttezza, invarianti, ownership, error model e test

Round B:
architettura, concorrenza, performance, privacy, documentazione, scope e CI
```

Per `R3` aggiungere threat model, abuso, sicurezza durante la guida e
failure/degraded mode.

## Profondità per rischio

| Rischio | Round 1 | Round 2 |
| --- | --- | --- |
| `R0` | accuratezza, link, scope | rilettura indipendente e CI docs |
| `R1` | comportamento e test locali | architettura, documentazione e regressioni |
| `R2` | contratti, ownership, cancellazione, provider | performance, failure mode, CI e didattica |
| `R3` | sicurezza, privacy, abuso, threat model | review trasversale completa e prove dedicate |

Il numero minimo resta due per tutti i livelli.

## Commit che fanno ripartire il conteggio

Reset obbligatorio:

```text
source code
unit/contract/replay tests
fixture
build o workflow
documentazione stabile
architecture map
report tecnico giornaliero
```

Non reset automatico:

```text
PR description
review submission o commento
label o milestone
CI rerun sullo stesso SHA
```

## Gate prima del ready e merge

- [ ] substantive head SHA registrato;
- [ ] branch basato o riallineato al `main` corrente;
- [ ] questa è l'unica PR aperta;
- [ ] CI richiesta verde su quello SHA;
- [ ] nessun finding o thread aperto;
- [ ] round pulito 1 registrato;
- [ ] round pulito 2 registrato sullo stesso SHA;
- [ ] nessun commit sostanziale dopo i round;
- [ ] PR body aggiornato senza cambiare il head;
- [ ] report giornaliero presente e indicizzato;
- [ ] documentazione, issue e milestone coerenti;
- [ ] autorità di merge registrata;
- [ ] expected-head guard pronto.

Solo dopo questi gate la PR passa da draft a ready.

## Autorità e merge autonomo

Il maintainer può concedere autorizzazione singola, autorizzazione permanente o
riservare il merge a sé.

Con autorizzazione permanente un agente può mergiare solo dopo aver verificato
ogni gate e usando l'expected head SHA revisionato. Non può saltare una review,
mergiare CI rossa o obsoleta, ignorare una seconda PR, riutilizzare round di uno
SHA precedente o aprire la PR successiva prima della verifica del merge.

Se lo SHA si muove fra gate e merge, l'operazione deve fallire e la review deve
essere rivalutata.

## Dopo il merge

Prima della PR successiva:

1. verificare `merged=true`;
2. registrare il merge commit;
3. verificare l'issue collegata;
4. leggere il nuovo `main`;
5. verificare che non esistano PR aperte;
6. creare o riallineare il branch successivo da quel `main`;
7. aggiornare report e milestone nel punto previsto.

## Chiusura amministrativa

Una PR che non verrà mergiata può essere chiusa senza i due round puliti quando è
accidentale, duplicata, vuota, placeholder, stacked o abbandonata esplicitamente.

La chiusura deve spiegare perché non distribuisce modifiche. Il contenuto non può
essere applicato direttamente a `main` e la PR non può essere registrata come
slice completata.

## Esempio con finding

```text
Head A
Round 1: trova lista mutabile trattenuta
Clean count: 0

Head B
Fix + regression test + CI green
Round 2: no finding
Clean count: 1
Round 3: trova metadata non bounded
Clean count: 0

Head C
Fix + CI green
Round 4: no finding
Clean count: 1
Round 5: no finding
Clean count: 2

PR body aggiornato sullo stesso Head C
ready
merge con expected Head C
verifica nuovo main
solo ora branch successivo
```

## Rapporto con la CI

```text
CI -> dimostra proprietà automatizzate
review -> valuta significato, omissioni, confini e rischi
```

La CI non sostituisce la review e due review non sostituiscono la CI.

## Regola per agenti autonomi

Un agente può implementare, correggere, svolgere entrambi i round e mergiare se
autorizzato, ma deve:

- separare esplicitamente i round;
- usare focus diversi;
- non dichiarare pulito un round senza riesaminare il substantive head;
- resettare il conteggio dopo finding o commit sostanziali;
- registrare gli esiti nel ledger PR, non con un commit auto-invalidante;
- usare expected-head guard;
- mantenere al massimo una PR aperta;
- verificare il nuovo `main` prima della PR successiva.

La velocità non modifica il gate di qualità.
