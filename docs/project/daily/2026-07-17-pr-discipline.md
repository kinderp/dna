# Report di sviluppo — 17 luglio 2026 — disciplina PR seriale

## Obiettivo

Ripristinare un flusso GitHub comprensibile e renderlo normativo:

```text
al massimo una PR aperta
-> CI verde
-> due review pulite sullo stesso SHA
-> merge autorizzato
-> verifica del nuovo main
-> soltanto allora PR successiva
```

- issue: [#14](https://github.com/kinderp/tdna/issues/14);
- PR: [#15](https://github.com/kinderp/tdna/pull/15);
- branch: `agent/serial-pr-governance`;
- base `main`: `2128f24b4a6ab00aeb437ed48a0af2910f33e9ec`.

## Situazione iniziale

Il repository aveva temporaneamente quattro PR aperte:

- `#9`: governance dei due round e hardening del routing;
- `#10`: MapScene e fake renderer;
- `#12`: LocationSample/replay aperta prima del merge della dipendenza;
- `#13`: PR accidentale chiamata `noop`.

Questa situazione rendeva ambigui base, diff, CI e ordine delle review.

## Che cosa significa `noop`

`No-op` significa **no operation**: un'operazione che non dovrebbe cambiare lo
stato significativo del sistema.

La PR `#13` non era però una vera PR vuota: era stata creata accidentalmente
durante tentativi di verificare lo stato dei branch e conteneva duplicati della
PR `#9` più note temporanee `REVIEW-GATE-NOTE-*`.

Non possedeva user story, issue-slice legittima, scope autonomo, review unit valida
o risultato da distribuire. È stata chiusa amministrativamente senza merge e il
nome `noop` non verrà usato come tecnica di gestione del flusso.

## Pulizia delle PR

### PR #13

- chiusa senza merge;
- descrizione aggiornata con il motivo amministrativo;
- non registrata come lavoro completato.

### PR #12

- chiusa senza merge perché stacked e dipendente da MapScene;
- branch conservato;
- nessuna review riutilizzabile;
- il lavoro futuro dovrà ripartire o riallinearsi dal `main` successivo.

### PR #9

Verificati CI, due round puliti sullo stesso SHA, thread e mergeability.

Mergiata su `main`:

```text
044e0773dd9afb1530db35688a00c56bfbd5eace
```

### PR #10

La PR MapScene è stata ricostruita direttamente sul nuovo `main`. Durante il
riallineamento sono stati corretti contratti duplicati, merge tree regressivo,
fixture obsolete, capability statiche e atomicità delle installazioni fallite.

Head finale:

```text
a18e73ad015951545c808d489ee66c57c5d1c80d
```

Foundation CI
[#80](https://github.com/kinderp/tdna/actions/runs/29558725017):

```text
documentation             success
architecture boundaries   success
Java reference routing    success
Rust reference routing    success
Java/Rust contract        success
Kotlin Multiplatform      success
```

Review finali sullo stesso SHA:

- round 1, review `4719736270`: clean;
- round 2, review `4719739041`: clean.

Mergiata con expected-head guard:

```text
2128f24b4a6ab00aeb437ed48a0af2910f33e9ec
```

L'issue `#7` è stata chiusa automaticamente dal merge.

## Regole rese normative

La PR `#15` aggiorna:

```text
AGENTS.md
CONTRIBUTING.md
.github/pull_request_template.md
docs/it/00-regole-operative.md
docs/it/04-come-contribuire.md
docs/it/06-review-e-merge.md
```

Le regole centrali sono:

1. al massimo una PR aperta nel repository;
2. branch creato dal `main` corrente;
3. niente PR stacked, placeholder o `noop`;
4. issue future consentite, PR future no;
5. nessuna eccezione parallela prevista dalle regole correnti;
6. due round puliti sullo stesso substantive head;
7. finding o commit sostanziale azzera il contatore;
8. merge autonomo soltanto con autorizzazione e tutti i gate;
9. expected-head guard obbligatorio;
10. verifica di PR, issue e nuovo `main` dopo il merge;
11. soltanto dopo si crea il branch successivo;
12. chiusura amministrativa solo per PR che non distribuiscono nulla.

## Perché non inserire queste regole nella PR MapScene

Governance e MapScene sono due review unit differenti. Inserire le nuove regole
nella PR `#10` avrebbe ampliato lo scope e invalidato nuovamente il gate. La
governance parte quindi dal merge commit MapScene e usa una sola PR separata.

## Finding pre-review

### Finding 1 — indici documentali sullo stato precedente

Il primo passaggio ha trovato che:

- `docs/it/README.md` descriveva ancora MapScene come PR draft;
- `documentation-status.md` non citava il nuovo report né il flusso seriale.

Correzione: indice principale e stato documentale allineati ai tre Lab presenti
in `main` e al nuovo report.

### Finding 2 — deroga implicita alla regola dell'unica PR

La prima versione della governance usava l'espressione “PR ordinaria” e lasciava
una possibile eccezione esplicita per PR parallele. Questo indeboliva la decisione
del maintainer.

Correzione:

- sostituito ovunque con “al massimo una PR aperta”;
- rimossa la deroga parallela da regole, agent guide, contributor guide e template;
- un eventuale cambio futuro richiederà una nuova PR di governance quando non
  esiste un'altra PR;
- la chiusura amministrativa resta consentita solo perché non distribuisce nulla.

Entrambi i finding hanno azzerato il clean-review counter. La CI e i due round
finali devono riferirsi al nuovo substantive head.

## Verifica prevista

La PR governance è documentale `R1`, ma esegue comunque l'intera Foundation CI.

Round 1:

- precisione normativa;
- assenza di scappatoie;
- coerenza fra agenti, contributori e template;
- merge authority ed expected-head guard;
- chiusura amministrativa.

Round 2:

- percorso reale di uno studente/contributore;
- unica PR aperta;
- riallineamento post-merge;
- report e milestone;
- link e CI.

Qualunque finding o commit sostanziale azzera il contatore.

## Decisioni autonome

- chiudere PR accidentale e stacked senza merge;
- preservare il branch della slice LocationSample;
- mergiare PR `#9` e `#10` soltanto dopo gate verificati;
- usare una issue separata per la governance;
- mantenere una sola nuova PR aperta;
- rinviare LocationSample fino al merge della governance.

## Decisioni richieste

Nessuna. Il maintainer ha già stabilito due round consecutivi, autorizzazione al
merge autonomo dopo i gate, al massimo una PR aperta e riallineamento di `main`
prima della PR successiva.

## Debito e prossimo passo

Dopo il merge della governance:

1. verificare il nuovo `main`;
2. verificare che non restino PR aperte;
3. riprendere issue `#11` da un nuovo branch basato sul `main` aggiornato;
4. aprire una sola PR per LocationSample, tempo monotono e replay deterministico.
