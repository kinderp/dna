# Strategia di test

## Principio

Travel DNA non può essere validato con una sola piramide di unit test. Un
navigatore coinvolge geometria, tempo, sensori, rendering, process lifecycle,
rete, background e comportamento umano.

```text
TDD protegge il comportamento locale.
Contract test protegge la sostituibilità.
Replay protegge la navigazione deterministica.
Benchmark protegge il costo.
Field audit protegge la realtà.
```

## 1. Unit test

Adatti a:

- value object;
- validazione coordinate;
- scoring;
- privacy policy;
- selezione provider;
- composizione DailyPage;
- message state;
- route confidence.

Devono essere veloci e deterministici.

## 2. Property-based test

Generano molti input e verificano invarianti.

Esempi:

- encode/decode polyline preserva coordinate entro tolleranza;
- distanza non negativa;
- route progress non supera la lunghezza;
- semplificazione mantiene estremi;
- expiry non estende un segnale;
- una revoca non viene annullata da una versione più vecchia;
- dedup di operation ID è idempotente.

## 3. State-machine test

### Navigation

```text
IDLE -> ROUTE_READY -> NAVIGATING -> SUSPECTED_OFF_ROUTE
-> REROUTING -> NAVIGATING -> COMPLETED
```

### Conversation

```text
LOCAL_QUEUED -> SENDING -> ACCEPTED -> FAILED/DELIVERED
```

### Daily page

```text
AUTO_DRAFT -> USER_EDITED -> CONFIRMED -> ARCHIVED
```

Il test genera sequenze valide e invalide e controlla transizioni.

## 4. Contract test

Ogni implementazione di un port esegue la stessa suite.

Route planner:

- origine/destinazione;
- no route;
- cancellazione;
- timeout;
- capability;
- error mapping;
- provenance;
- geometry validity.

External navigator:

- availability;
- URL encoding;
- unsupported waypoint;
- fallback;
- no crash app missing.

Map renderer:

- install scene;
- delta idempotente;
- remove feature;
- interaction mapping;
- lifecycle.

## 5. Integration test

- SQLite migration;
- PostGIS query;
- Valhalla container;
- WebSocket reconnect;
- push handling con fake;
- media upload;
- MapLibre lifecycle;
- Ferrostar adapter;
- KMP/Swift bridge;
- Rust FFI.

## 6. GPS replay

Il replay è il cuore della verifica navigation. Usa clock virtuale e fixture con
ground truth.

Scenari:

- autostrada lineare;
- svincolo mancato;
- rampe parallele;
- galleria;
- GPS rumoroso;
- città canyon;
- inversione;
- traghetto;
- sosta;
- perdita rete;
- route replacement.

Output:

- snapshot timeline;
- tracepoint logico;
- errori;
- latenza;
- confidenza;
- reroute count.

## 7. Golden test

Usare golden con moderazione per output strutturato stabile:

- canonical route fixture;
- NavigationSnapshot timeline;
- DailyPage projection;
- public sync envelope;
- DnaCard sanitized.

Evitare golden di screenshot fragili per ogni pixel.

## 8. UI test

### Android/iOS

- start trip;
- handoff navigator;
- return;
- message queued;
- driver/passenger mode;
- diary edit;
- permission denied;
- accessibility labels;
- dynamic font.

### Screenshot test

Solo componenti stabili e layout critici. Validare manualmente anche luce, scala
e lingua.

## 9. Automotive test

Android Auto:

- Desktop Head Unit;
- messaging notification;
- reply/mark read;
- POI template;
- focus;
- constraints.

CarPlay:

- simulator;
- SiriKit messaging;
- widget/Live Activity;
- entitlement-dependent flows con mock;
- route handoff.

## 10. Performance test

Famiglie:

- location-to-snapshot;
- snapshot-to-HUD;
- map delta;
- route normalization;
- reroute;
- FFI;
- conversation delivery;
- local DB;
- startup;
- long trip.

Non confrontare numeri di hardware diverso senza dichiararlo.

## 11. Soak test

Simulare 2–12 ore:

- route lunga;
- campioni GPS;
- tile churn;
- messaggi;
- foto candidate;
- stop;
- rete intermittente;
- foreground/background.

Controllare:

- memoria;
- code;
- file descriptor;
- DB growth;
- wakeup;
- batteria;
- crash/hang.

## 12. Energy test

- GPS modes;
- background frequency;
- screen on/off;
- map rendering;
- WebSocket/push;
- tile download;
- media processing;
- TTS.

La batteria è requisito funzionale: un'app scarica viene disattivata.

## 13. Security test

- auth/session;
- deep link validation;
- location spoof;
- block bypass;
- rate limit;
- malicious media;
- path traversal in offline packages;
- schema fuzzing;
- dependency scan;
- secrets;
- delete/export.

## 14. Privacy test

Asserire proprietà:

- exact location non esce nel PresenceSignal;
- EXIF rimosso nella DnaCard;
- consent revocation blocca publish;
- logs non contengono message body;
- fixture non contiene coordinate vietate;
- block elimina matching reciproco.

## 15. Field audit

Usa build nota su tratta dichiarata. Registra:

- ambiente;
- dispositivo;
- OS;
- durata;
- rete;
- batteria;
- navigatore;
- eventi;
- problemi;
- artifact anonimizzati.

Un field audit non sostituisce il replay. Un bug reale va ricostruito come fixture
sintetica riproducibile.

## 16. Test matrix

| Cambio | Minimo |
| --- | --- |
| Regola dominio | unit + doc |
| Provider adapter | contract + integration |
| Navigation core | unit + property + replay + benchmark |
| Map rendering | integration + UI + performance |
| Background | integration device + field |
| Chat | state + integration + automotive |
| Privacy | unit property + threat review |
| Public schema | compatibility + golden + migration |
| Dependency | licence/security + smoke |

## 17. TDD outside-in

Per una slice:

1. scenario di accettazione;
2. fake ports;
3. application use case;
4. domain rules;
5. adapter;
6. integration;
7. performance/safety gates.

Spike esplorativi possono precedere TDD, ma il codice spike non entra in
produzione senza contratti e test.

## 18. Flaky test

Un test flaky è un bug del sistema di verifica. Deve essere:

- riprodotto;
- isolato;
- corretto;
- quarantinato solo con issue e scadenza;
- non semplicemente rilanciato finché passa.

## 19. Test data governance

Ogni fixture dichiara:

```text
synthetic/anonymized
source
license
purpose
expected ground truth
sensitive fields
retention
```
