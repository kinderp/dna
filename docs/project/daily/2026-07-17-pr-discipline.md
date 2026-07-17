# Report di sviluppo — 17 luglio 2026 — disciplina PR seriale

## Obiettivo

Ripristinare un flusso GitHub comprensibile e renderlo normativo:

```text
una sola PR aperta
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

Non possedeva:

- una user story;
- una issue-slice legittima;
- scope autonomo;
- una review unit valida;
- un risultato da distribuire.

È stata quindi chiusa amministrativamente senza merge. Il nome `noop` non verrà
usato come tecnica di gestione del flusso.

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
riallineamento sono stati corretti:

- due generazioni duplicate dei contratti MapScene;
- merge tree che avrebbe reintrodotto vecchia governance;
- fixture fake rimasta sui tipi obsoleti;
- capability statiche non applicate durante `install`;
- test di atomicità mancante per installazioni rifiutate.

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

1. una sola PR ordinaria aperta;
2. branch creato dal `main` corrente;
3. niente PR stacked, placeholder o `noop`;
4. issue future consentite, PR future no;
5. due round puliti sullo stesso substantive head;
6. finding o commit sostanziale azzera il contatore;
7. merge autonomo soltanto con autorizzazione e tutti i gate;
8. expected-head guard obbligatorio per il merge automatico;
9. verifica di PR, issue e nuovo `main` dopo il merge;
10. soltanto dopo si crea il branch successivo;
11. chiusura amministrativa consentita solo per PR che non distribuiscono nulla.

## Perché non inserire queste regole nella PR MapScene

Governance e MapScene sono due review unit differenti. Inserire le nuove regole
nella PR `#10` avrebbe:

- ampliato lo scope;
- confuso review tecnica e processo;
- aggiunto commit sostanziali dopo i round;
- invalidato nuovamente il gate.

Per questo la governance parte dal merge commit MapScene e usa una sola PR
separata.

## Finding pre-review

Il primo passaggio di coerenza ha trovato che:

- `docs/it/README.md` descriveva ancora MapScene come PR draft invece che slice
  già mergiata;
- `documentation-status.md` non citava il nuovo report né il flusso seriale nelle
  note dei capitoli operativi.

Correzione:

- indice principale aggiornato ai tre Lab presenti in `main`;
- capitoli `00`, `04` e `06` descritti esplicitamente come governance seriale;
- nuovo report aggiunto allo stato documentale;
- la CI sul primo SHA della PR è storica e il clean-review counter resta zero.

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
- una sola PR aperta;
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

Nessuna. Il maintainer ha già stabilito:

- due round consecutivi obbligatori;
- autorizzazione al merge autonomo dopo i gate;
- una sola PR aperta;
- riallineamento di `main` prima della PR successiva.

## Debito e prossimo passo

Dopo il merge della governance:

1. verificare il nuovo `main`;
2. verificare che non restino PR aperte;
3. riprendere issue `#11` da un nuovo branch basato sul `main` aggiornato;
4. aprire una sola PR per LocationSample, tempo monotono e replay deterministico.
