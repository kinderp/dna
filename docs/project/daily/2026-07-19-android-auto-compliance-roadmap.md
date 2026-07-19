# Daily development report — 2026-07-19 — Android Auto compliance roadmap

## Goal

Translate the Android Auto discussion into a durable, reviewable roadmap without
adding automotive runtime code or declaring unsupported car categories.

## Tracking

- issue: [#29](https://github.com/kinderp/tdna/issues/29);
- branch: `agent/android-auto-compliance-roadmap`;
- verified base: `6198bd0a6c5a89f0e32fa218015125b5d452703e`;
- risk: `R1`;
- authoritative final ledger: pull request for issue #29.

## Context verified

The repository had no open pull request before the branch was created. The base is
the verified merge of Android Pilot 0 v0.2.

Official Android for Cars sources were rechecked on 2026-07-19 because supported
categories, APIs and quality policy can change independently of this repository.
The documentation records the verification date and requires a new check before
runtime implementation or Play submission.

## Accepted decisions

### Separate car experience

Android Auto is not a landscape or mirrored version of the mobile Compose UI. A
future car surface uses Android for Cars App Library templates and exposes only a
bounded, driver-safe subset of Travel DNA.

### POI-first Pilot 1

The first candidate Android Auto category is:

```text
androidx.car.app.category.POI
```

The car experience may show stops, saved places and brief POI information, then
handoff to an external navigator. TDNA remains a companion and does not claim
turn-by-turn authority.

The initial readiness spike prefers `PlaceListMapTemplate`, which uses a map
rendered by the host and avoids introducing MapLibre or another map renderer. The
prototype declares `androidx.car.app.MAP_TEMPLATES` and validates the template's
content limits and distance metadata.

### Navigation later

The category:

```text
androidx.car.app.category.NAVIGATION
```

is deferred to an Internal Navigation Beta after guidance, map matching, route
progress, reroute, audio focus, intents, navigation lifecycle, AUTO_DRIVE, DHU and
quality gates are implemented and verified.

### Driver-safe shared contract

Pilot 0 v0.3 must prepare a provider-neutral `DriverJourneySnapshot` or equivalent
contract. Mobile Compose and future automotive adapters consume the snapshot; the
shared modules do not import Android framework, Compose or `androidx.car.app`.

### Replay reuse

The deterministic replay remains a mobile teaching and test feature in v0.3. A
future Navigation adapter may reuse it to implement
`NavigationManagerCallback.onAutoDriveEnabled()` without treating simulation as
road evidence.

### Platform separation

```text
Android emulator       mobile app evidence
Desktop Head Unit      Android Auto evidence
AAOS emulator          Android Automotive OS package evidence
vehicle                compatibility evidence
field test             battery/usability/road evidence
```

No layer substitutes for the next.

## Documentation delivered

- ADR-0011: Android Auto POI-first and separate car surfaces;
- chapter 61: categories, architecture, POI template baseline, permissions, voice,
  testing, packaging, risks and quality gates;
- chapter 55 rewritten to integrate v0.3/v0.4, automotive readiness, Pilot 1 POI
  and later Navigation Beta;
- chapter 25 aligned with external-navigation-first and POI-first decisions;
- chapter 57 extended with an optional Android Auto POI field subprotocol,
  prerequisites, scenarios and stop conditions;
- root and documentation indexes;
- milestone register and development status;
- documentation status with a distinct `Decision-backed` state;
- this report and daily index entry.

## Updated roadmap

```text
Pilot 0 v0.3
    replay/progress UI + driver-safe snapshot

Pilot 0 v0.4
    missed-exit/reroute + degraded state

Pilot 0 v0.5
    external navigator adapters

Automotive readiness
    PlaceListMapTemplate POI prototype + DHU

Pilot 1
    foreground companion + external navigator + optional POI car surface

Internal Navigation Beta
    Navigation category + guidance + AUTO_DRIVE + voice + car quality review
```

## Non-goals

- Car App Library dependency;
- `CarAppService` or `Session`;
- car manifest category;
- DHU automation;
- Android Automotive OS module;
- Play submission;
- navigation or road-compliance claim;
- changes to mobile runtime behavior.

## Evidence and review gate

This is a documentation/architecture slice. Completion requires:

```text
exact substantive head
-> documentation CI green
-> no unresolved threads
-> two clean reviews on the same SHA
-> expected-head merge
-> issue/main/open-PR verification
```

Any stable-document or report change resets the clean-review count.
