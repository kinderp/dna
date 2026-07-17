# Review, due round puliti e flusso di merge seriale

## Scopo

Nessuna pull request di Travel DNA può distribuire una modifica senza:

- CI richiesta verde;
- due review round consecutivi senza nuovi finding;
- stesso substantive head SHA;
- autorità di merge esplicita;
- rispetto del flusso con una sola PR aperta.

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

- un rerun della CI;
- una rilettura superficiale del solo ultimo file;
- la ripetizione testuale del round precedente;
- l'assenza di commenti per distrazione.

### Finding

È un problema che richiede una modifica sostanziale o una decisione esplicita.
Esempi:

- invariante mancante;
- tipo provider che esce dall'adapter;
- test che non protegge il comportamento promesso;
- input esterno non bounded;
- documento che dichiara una capacità inesistente;
- errore di privacy;
- hot path reso più costoso senza prova;
- workflow che non verifica il nuovo modulo;
- scope non autorizzato dalla milestone.

### PR ordinaria

È una PR destinata al merge. Nel flusso autonomo ordinario ne esiste una sola
aperta.

### Chiusura amministrativa

È la chiusura senza merge di una PR accidentale, duplicata, stacked o abbandonata.
Non equivale al completamento della slice.

## Perché una sola PR aperta

Il flusso seriale rende verificabili:

- base reale del branch;
- diff della review unit;
- CI associata allo SHA;
- finding e reset;
- ordine delle dipendenze;
- report e milestone;
- responsabilità del merge.

La sequenza è:

```text
nessuna PR ordinaria aperta
-> branch dal main corrente
-> una draft PR
-> review/fix/CI
-> due round puliti
-> merge o abbandono
-> verifica nuovo main
-> branch successivo
```

Preparare issue future è consentito. Aprire PR stacked o placeholder non lo è,
salvo eccezione esplicita del maintainer.

## Fonti di evidenza

### Ledger autorevole della PR

Timeline delle review e descrizione PR conservano sullo stesso head:

```text
head SHA
risk level
focus
file e contratti controllati
CI e test osservati
finding oppure no new findings
clean-round count
```

Questo è il ledger autorevole per ready e merge.

### Report storico nel repository

Il report Markdown committato prima dei round finali contiene:

- contesto della giornata;
- issue e PR;
- finding e fix già avvenuti;
- substantive head previsto;
- piano e focus dei round;
- link al ledger della PR.

Non viene modificato dopo i round soltanto per copiarne gli esiti: quel commit
cambierebbe lo SHA e invaliderebbe i round. Una successiva PR documentale può
riconciliare report, CI finale e merge commit.

## Sequenza normativa

### 1. Inventario e base

Prima del branch:

1. verificare quante PR sono aperte;
2. chiudere o risolvere eventuali PR accidentali/stacked;
3. leggere il nuovo SHA di `main`;
4. verificare dipendenze della issue;
5. creare il branch da quel `main`.

### 2. PR draft

La PR nasce draft e contiene:

- use case;
- scope e non-obiettivi;
- rischio;
- base `main` SHA;
- conferma che è l'unica PR ordinaria;
- contratti;
- verifiche;
- documentazione;
- reviewer focus.

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

Il finding deve essere:

1. descritto con effetto osservabile;
2. corretto nel repository;
3. protetto da test o altra evidenza quando applicabile;
4. collegato nel report o nel ledger;
5. verificato dalla CI sul nuovo head.

Il conteggio torna a zero.

### 6. Due round consecutivi puliti

Dopo l'ultimo commit sostanziale:

```text
Round A -> no new findings -> clean count 1
Round B -> no new findings -> clean count 2
```

I round devono avere focus complementari. Esempio `R2`:

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

Un commit `docs:` è sostanziale quando modifica la spiegazione consolidata o il
report tecnico.

## Gate prima del ready e merge

Checklist minima:

- [ ] substantive head SHA registrato;
- [ ] branch basato o riallineato al `main` corrente;
- [ ] questa è l'unica PR ordinaria aperta;
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

Il maintainer può concedere:

- autorizzazione singola per una PR;
- autorizzazione permanente per il flusso autonomo;
- nessuna autorizzazione, riservando il merge a sé.

Con autorizzazione permanente un agente può mergiare solo dopo aver verificato
ogni gate e usando l'expected head SHA revisionato. Non può interpretare
l'autorizzazione come permesso di:

- saltare una review;
- mergiare CI rossa o obsoleta;
- ignorare una nuova PR aperta;
- riutilizzare round di uno SHA precedente;
- aprire la PR successiva prima della verifica del merge.

Se lo SHA si muove fra gate e merge, l'operazione deve fallire e la review deve
essere rivalutata.

## Dopo il merge

Prima della PR successiva:

1. verificare `merged=true`;
2. registrare il merge commit;
3. verificare l'issue collegata;
4. leggere il nuovo `main`;
5. verificare che non esistano altre PR aperte;
6. creare o riallineare il branch successivo da quel `main`;
7. aggiornare report/milestone nel punto previsto.

Questa fase non è una formalità: impedisce che la slice successiva parta da una
storia diversa da quella realmente distribuita.

## Chiusura amministrativa

Una PR che non verrà mergiata può essere chiusa senza i due round puliti quando è:

- accidentale;
- duplicata;
- vuota o placeholder;
- stacked e aperta prima della dipendenza;
- abbandonata esplicitamente.

La chiusura deve spiegare perché non distribuisce modifiche. Non può essere usata
per aggirare il gate e poi applicare il contenuto direttamente a `main`.

## Esempio con finding

```text
Head A
Round 1: trova lista mutabile trattenuta
Clean count: 0

Head B
Fix + regression test
CI green
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
- mantenere una sola PR aperta;
- verificare il nuovo `main` prima della PR successiva.

La velocità non modifica il gate di qualità.
