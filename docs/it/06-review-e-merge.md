# Review, due round puliti e merge

## Scopo

Questo capitolo rende eseguibile la regola introdotta nelle
[regole operative](00-regole-operative.md): nessuna pull request di Travel DNA
può essere chiusa o mergiata senza **due review round consecutivi senza nuovi
finding** sullo stesso substantive head.

La regola serve a evitare un errore comune:

```text
review trova un problema
-> il problema viene corretto
-> la PR viene mergiata subito
```

Il fix può introdurre un nuovo difetto, rendere obsoleta una spiegazione o
spostare il rischio in un altro modulo. Per questo la sequenza corretta è:

```text
review con finding
-> fix
-> test
-> CI
-> review pulita 1
-> review pulita 2
-> ready/merge
```

## Vocabolario

### Substantive head

È il commit più recente che modifica il contenuto tecnico o documentale della
slice:

- codice;
- test;
- contratti;
- fixture;
- workflow;
- documentazione stabile;
- report tecnico della giornata.

I due round puliti devono riferirsi allo stesso SHA.

### Review round

È una lettura distinta della PR corrente con un focus dichiarato. Non è:

- un semplice rerun della CI;
- una rilettura superficiale del solo ultimo file;
- la ripetizione testuale del round precedente;
- l'assenza di commenti per distrazione.

### Finding

È un problema che richiede una modifica sostanziale o una decisione esplicita.
Esempi:

- invariante mancante;
- provider type che esce dall'adapter;
- test che non protegge il comportamento promesso;
- input esterno non bounded;
- documento che dichiara una capacità inesistente;
- errore di privacy;
- hot path reso più costoso senza prova;
- workflow che non verifica il nuovo modulo;
- scope non autorizzato dalla milestone.

Un'osservazione puramente editoriale che non richiede modifica può essere
registrata come nota e non resetta automaticamente il contatore. Se il testo
viene però modificato nella PR, il commit è sostanziale e la sequenza riparte.

## Sequenza normativa

### 1. PR draft

La PR nasce draft e contiene:

- use case;
- scope e non-obiettivi;
- rischio;
- contratti;
- verifiche;
- documentazione;
- reviewer focus.

### 2. Baseline verde

Prima della review finale devono essere verdi i controlli applicabili sul head
corrente. Una review può iniziare con CI in corso, ma non può essere dichiarata
pulita finché la verifica richiesta non è conclusa.

### 3. Review round

Ogni round registra:

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

### 4. Se esiste un finding

Il finding deve essere:

1. descritto con effetto osservabile;
2. corretto nel repository;
3. protetto da test o altra evidenza quando applicabile;
4. documentato nel report;
5. verificato dalla CI sul nuovo head.

Il conteggio torna a zero.

### 5. Due round consecutivi puliti

Dopo l'ultimo commit sostanziale:

```text
Round A -> no new findings -> clean count 1
Round B -> no new findings -> clean count 2
```

I round devono avere focus complementari. Un esempio per una PR `R2`:

```text
Round A:
correttezza, invarianti, ownership, error model, test

Round B:
architettura, concorrenza, performance, documentazione, scope e CI
```

Per una PR `R3` aggiungere sempre privacy, abuso, sicurezza durante la guida e
failure/degraded mode.

## Profondità per rischio

| Rischio | Round 1 | Round 2 |
| --- | --- | --- |
| `R0` | correttezza editoriale, link, scope | rilettura indipendente e CI docs |
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
build or workflow
stable documentation
architecture map
technical daily report
```

Non reset automatico:

```text
PR description only
review comment only
labels/milestone
CI rerun on the same SHA
```

Un commit chiamato `docs:` non è automaticamente non sostanziale: se modifica la
spiegazione consolidata o il report tecnico, deve essere nuovamente revisionato.

## Gate prima del merge

Checklist minima:

- [ ] substantive head SHA registrato;
- [ ] CI richiesta verde su quello SHA;
- [ ] nessun finding aperto;
- [ ] round pulito 1 registrato;
- [ ] round pulito 2 registrato;
- [ ] nessun commit sostanziale fra i due round e il merge;
- [ ] PR body aggiornato;
- [ ] report giornaliero aggiornato;
- [ ] documentazione e stato milestone coerenti;
- [ ] issue collegata pronta a chiudersi con il merge.

Solo dopo questi gate la PR può passare da draft a ready e può essere mergiata.

## Esempio con finding

```text
Head A
Round 1: trova lista mutabile trattenuta dal contratto
Clean count: 0

Head B
Fix: snapshot difensiva + regression test
CI: green

Round 2: no finding
Clean count: 1

Round 3: trova metadata provider non bounded
Clean count: 0

Head C
Fix: limiti + rejection tests
CI: green

Round 4: no finding
Clean count: 1
Round 5: no finding
Clean count: 2

PR ready e merge consentito
```

## Esempio documentale `R0`

Anche una PR di sola documentazione richiede due passaggi:

```text
Round 1:
accuratezza, link, terminologia e scope

Round 2:
rilettura dal percorso dello studente, coerenza con stato e indice
```

I passaggi possono essere brevi, ma devono essere reali e registrati.

## Rapporto con la CI

La CI non sostituisce la review:

```text
CI -> dimostra proprietà automatizzate
review -> valuta significato, confini, omissioni e rischi
```

Allo stesso modo, due review pulite non sostituiscono una CI rossa o mancante.
Servono entrambe.

## Rapporto con i report giornalieri

Il report finale della giornata contiene:

- head finale;
- run CI finale;
- finding e fix;
- review round effettuati;
- conteggio pulito finale;
- stato draft/ready/merged;
- decisioni ancora richieste.

L'indice permanente vive in
[`docs/project/daily/README.md`](../project/daily/README.md).

## Regola per agenti autonomi

Un agente può implementare, correggere e svolgere entrambi i round, ma deve:

- separarli esplicitamente;
- usare focus diversi;
- non dichiarare pulito un round senza aver riesaminato il substantive head;
- resettare il conteggio dopo ogni finding o commit sostanziale;
- non mergiare autonomamente se il repository riserva il merge al maintainer.

La velocità non modifica il gate di qualità.
