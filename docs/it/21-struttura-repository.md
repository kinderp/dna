# Struttura del repository

## Obiettivo

Travel DNA usa un monorepo per mantenere vicini contratti, adapter, fixture,
documentazione e test. Il monorepo non significa un unico modulo senza confini.

## Albero target

```text
traveldna/
├── apps/
│   ├── android/
│   └── ios/
├── shared/
│   ├── domain/
│   ├── application/
│   ├── contracts/
│   ├── presentation-models/
│   ├── plugin-sdk/
│   └── plugin-testkit/
├── java/
│   ├── reference-routing/
│   └── backend/
├── crates/
│   ├── tdna-geo/
│   ├── tdna-guidance/
│   ├── tdna-replay/
│   └── tdna-ffi/
├── plugins/
│   ├── maplibre-android/
│   ├── maplibre-ios/
│   ├── valhalla-remote/
│   ├── ferrostar-android/
│   ├── ferrostar-ios/
│   ├── external-navigation-android/
│   └── external-navigation-ios/
├── backend/
│   ├── app/
│   ├── modules/
│   └── providers/
├── schemas/
│   ├── api/
│   ├── events/
│   └── database/
├── fixtures/
│   ├── gps/
│   ├── routes/
│   ├── journeys/
│   ├── places/
│   └── conversations/
├── tests/
│   ├── contract/
│   ├── integration/
│   ├── replay/
│   ├── performance/
│   ├── soak/
│   ├── security/
│   └── field/
├── tools/
├── docs/
├── .github/
├── AGENTS.md
└── README.md
```

Questa è una struttura target. Le directory vengono create quando una milestone
introduce un contratto reale.

## `apps/android`

Responsabilità:

- entry point Android;
- Compose UI;
- MapLibre host;
- lifecycle e permessi;
- foreground/background service;
- notifiche e TTS;
- Android Auto;
- composition root Android.

Non contiene:

- semantica del diario;
- modelli Valhalla nel dominio;
- algoritmo di route progress se condivisibile;
- business rule del consenso.

## `apps/ios`

Responsabilità:

- SwiftUI shell;
- UIKit map host;
- Core Location e PhotoKit;
- ActivityKit e CarPlay;
- TTS e notification handling;
- composition root iOS.

## `shared/domain`

Moduli per bounded context. Dipendenze minime, niente SDK mobile o provider.

Esempio:

```text
shared/domain/journey
shared/domain/journal
shared/domain/navigation
shared/domain/travel-dna
shared/domain/presence
shared/domain/conversation
shared/domain/identity-consent
```

## `shared/application`

Use case e orchestrazione:

```text
CreateTrip
StartTripSession
LaunchNavigation
RecordJourneyEvent
ComposeDailyPage
PublishDnaCard
SendRoadQuestion
```

Dipende da domain e ports, non da implementazioni.

## `shared/contracts`

Contratti canonici trasversali:

- geo;
- routing;
- navigation;
- map scene;
- media references;
- messaging transport;
- local storage;
- time and IDs.

Un contratto non deve diventare una copia completa dell'API del provider.

## `shared/plugin-sdk`

Contiene:

- descriptor;
- capability;
- lifecycle;
- health;
- provider selection context;
- error model comune.

## `shared/plugin-testkit`

Contiene test di conformità riutilizzabili, fake e fixture. Ogni nuovo adapter
deve poter eseguire lo stesso contratto.

## `java/reference-routing`

Reference implementation didattica:

- grafo piccolo;
- Dijkstra e A*;
- euristiche;
- costi;
- route reconstruction;
- test comuni;
- benchmark comparativo prudente.

Non è automaticamente il router di produzione.

## `crates`

### `tdna-geo`

Tipi geometrici e algoritmi puri, senza mobile SDK.

### `tdna-guidance`

Futuro core di route progress, off-route e maneuver selection.

### `tdna-replay`

Parser fixture, clock virtuale, runner e report.

### `tdna-ffi`

Binding sottili. Non deve contenere logica di dominio duplicata.

## `plugins`

Ogni plugin importa:

```text
contratto Travel DNA + SDK concreto
```

Non importa moduli di dominio non necessari. Deve contenere mapping, error
translation, capability e test.

## `backend`

Il backend può essere organizzato come monolite modulare. Ogni modulo possiede
schema e API interne chiare. Le dipendenze tra moduli sono controllate con test o
regole di build.

## `schemas`

Gli schemi pubblici vengono versionati separatamente dal codice:

```text
API schema
sync envelope
push payload
public event schema
local DB migration
```

Non serializzare direttamente le classi interne come contratto esterno senza una
decisione esplicita.

## `fixtures`

Solo dati sintetici o anonimizzati. Ogni fixture ha metadati:

```text
id
purpose
source
privacy status
coordinate system
sample rate
ground truth when known
license
```

## `tests`

La directory integra test cross-module. I test strettamente locali restano vicino
al modulo quando lo strumento lo richiede.

## `tools`

Wrapper `tdna`, generatori, link checker, fixture builder, benchmark runner,
licence audit e strumenti di documentazione.

## `docs`

- `it`: guida narrativa;
- `adr`: decisioni;
- generated: output rigenerabile;
- commenting style/status;
- eventuale API reference.

## Regole di dipendenza

```text
domain -> standard/shared primitives only
application -> domain + ports
presentation -> application + presentation models
plugin -> port + vendor SDK
app -> presentation + plugins + composition
backend module -> own domain + declared module APIs
```

Regole vietate:

```text
domain -> MapLibre
journal -> Android Context
navigation core -> SwiftUI
conversation -> Valhalla response
plugin A -> internals of plugin B
```

## Codice generato

Codice FFI, API client e schemi generati devono vivere in directory riconoscibili
e non essere modificati a mano. La sorgente e il comando di generazione devono
essere documentati.
