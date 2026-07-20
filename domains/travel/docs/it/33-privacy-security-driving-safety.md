# Privacy, sicurezza e sicurezza durante la guida

## Perché è un requisito architetturale

Travel DNA tratta posizione, tragitti, fotografie, preferenze e conversazioni.
Combinati, questi dati possono rivelare casa, vacanze, famiglia, abitudini e
relazioni. La privacy non può essere un pannello impostazioni aggiunto alla fine.

## Data inventory v0

| Dato | Sensibilità | Uso |
| --- | --- | --- |
| Posizione precisa | Alta | navigazione e diario locale |
| Route/destinazione | Alta | viaggio e suggerimenti |
| Presence approssimata | Media | compagni di strada |
| Foto e EXIF | Alta | diario, condivisione opzionale |
| Messaggi | Alta | chat |
| DNA preferenze | Medio/alto | personalizzazione |
| Ruolo conducente | Medio | safety policy |
| Device token | Alto operativo | push |
| Telemetria | Variabile | qualità/performance |

## Privacy by default

Default:

- invisibile fuori da sessione;
- diary private;
- exact location local;
- DnaCard preview;
- no public photo;
- short presence TTL;
- broad destination off;
- contact requests controlled;
- analytics minimized.

## Data-flow review

Ogni feature produce un diagramma:

```text
source
-> local processing
-> persisted local
-> transmitted
-> server processing
-> shared recipients
-> retention/delete
```

## Consent

Il consenso deve essere:

- specifico;
- comprensibile;
- revocabile;
- versionato;
- non bundlato inutilmente;
- separato da funzioni essenziali.

Esempi distinti:

- background location;
- road presence;
- photo library;
- microphone;
- message notifications;
- analytics;
- public DnaCard.

## Precision ladder

```text
exact device location
matched road segment local
coarse corridor
aggregated companion count
region-level destination
```

Ogni consumer riceve il livello minimo.

## Retention

Definire per classe:

- GPS raw buffer: breve;
- compressed trip route: user-owned;
- presence: minuti;
- encounter not saved: breve;
- messages: user policy;
- reports/security audit: ristretto;
- analytics: aggregato.

## Threat model

Attori:

- utente curioso;
- stalker;
- spammer;
- account compromesso;
- insider;
- provider esterno;
- attaccante rete;
- app malevola sul dispositivo;
- backend compromise;
- scraping automatizzato.

Asset:

- posizione;
- identità;
- diario;
- messaggi;
- media;
- social graph;
- consensi;
- token.

## Minacce principali

### Stalking

- correlazione di pseudonimo e posizione;
- osservazione ripetuta;
- destinazione precisa;
- camping/hotel.

Mitigazioni:

- ephemeral IDs;
- coarse cells;
- delay/jitter;
- TTL;
- blocks before match;
- no encounter history;
- rate limit.

### Spoofing

Un utente può falsificare GPS per entrare in un corridoio. Mitigazioni prudenziali:

- plausibility;
- speed/route consistency;
- device signals dove consentito;
- trust level;
- non usare presence come prova forte.

### Spam e molestie

- quotas;
- mutual consent;
- road question expiry;
- block/report;
- reputation;
- moderation;
- no arbitrary mass DM.

### Leakage media

- strip EXIF;
- thumbnails;
- signed URLs;
- access control;
- content review;
- delete derivatives.

### Deep link injection

Validare coordinate, scheme, host e length. Non aprire URL arbitrari da messaggi.

### Supply chain

- pin dependencies;
- review licences;
- SBOM;
- vulnerability scanning;
- signed releases;
- secret scanning;
- minimal SDK count.

## Driver safety

La chat è attiva, ma il conducente non deve leggere e digitare.

Policy:

- voce;
- action breve;
- priorità manovra;
- defer;
- no complex modal;
- no map avatar selection in motion;
- passenger role for full UI;
- conservative unknown state.

## Distraction model

Ogni azione classifica:

```text
glance length
number of steps
manual text input
visual search
urgency
interruptibility
```

Funzioni non sicure vengono disabilitate o rinviate nel driver mode.

## Voice safety

- conferma messaggi con nomi/numeri sensibili;
- non leggere contenuti espliciti se lock/privacy settings;
- stop command;
- mute;
- audio priority;
- local transcript preview da fermi;
- no voice command that bypasses consent.

## Minori

MVP adult-only. Il dato “viaggio con bambini” è preferenza di contesto, non
identità del minore.

## Logging

Vietato per default:

- message bodies;
- exact coordinates;
- auth tokens;
- photo paths cloud;
- full DNA;
- contact information.

Diagnostica usa IDs e quantizzazione.

## Incident response

Definire:

- revoca token;
- disable feature flag;
- purge presence;
- notify user;
- preserve restricted evidence;
- vulnerability disclosure;
- postmortem senza dati personali.

## Security gates maturity

Una feature non è matura se:

- non ha block/report;
- espone posizione esatta;
- non può cancellare dati;
- non gestisce consent revoke;
- usa SDK senza review;
- non ha degraded safety mode.

## Privacy tests

- schema linter per campi vietati;
- log scan;
- DnaCard sanitization;
- presence resolution test;
- retention job;
- account deletion;
- block graph;
- permission revoke during trip;
- export inspection.

## Store and regulatory review

Prima della distribuzione:

- informative e permessi;
- background location justification;
- user-generated content moderation;
- automotive quality;
- GDPR roles/rights;
- data processing agreements;
- age policy;
- legal review OSM/content.

Questo documento non sostituisce consulenza legale.
