# Riferimenti tecnici ufficiali

Verificati per il corpus iniziale il 16 luglio 2026. Le policy e le API possono
cambiare: prima di una milestone che dipende da esse, ricontrollare la fonte e
registrare la data.

## MapLibre

- MapLibre Native developer documentation: https://maplibre.org/maplibre-native/docs/book/
- MapLibre Native repository: https://github.com/maplibre/maplibre-native
- MapLibre projects: https://maplibre.org/projects/native/

Punti usati: renderer C++ multipiattaforma, vector tiles/style, GPU, Android/iOS,
licenza BSD.

## Valhalla

- Documentation: https://valhalla.github.io/valhalla/
- Route API: https://valhalla.github.io/valhalla/api/turn-by-turn/api-reference/
- Map matching: https://valhalla.github.io/valhalla/api/map-matching/api-reference/
- Architecture: https://valhalla.github.io/valhalla/
- Repository: https://github.com/valhalla/valhalla

Punti usati: OSM routing, route/matrix/isochrone/map matching, moduli interni,
MIT, traffic come input esterno.

## Ferrostar

- Book: https://stadiamaps.github.io/ferrostar/
- Repository: https://github.com/stadiamaps/ferrostar

Punti usati: navigation SDK, Rust core, Kotlin/Swift, vendor-neutral, BSD, beta e
assenza di garanzia API 1.0, non è routing engine/basemap/search.

## OpenStreetMap

- Tile Usage Policy: https://operations.osmfoundation.org/policies/tiles/
- Vector Tile Usage Policy: https://operations.osmfoundation.org/policies/vector/
- Nominatim Usage Policy: https://operations.osmfoundation.org/policies/nominatim/
- Attribution Guidelines: https://osmfoundation.org/wiki/Licence/Attribution_Guidelines
- OSM copyright: https://www.openstreetmap.org/copyright

Punti usati: dati vs server, no SLA, no bulk/offline standard tile, massimo
Nominatim pubblico, attribution e ODbL.

## Android e Android Auto

- Android for Cars overview: https://developer.android.com/training/cars
- Navigation apps: https://developer.android.com/training/cars/apps/navigation
- POI apps: https://developer.android.com/training/cars/apps/poi
- Messaging notifications: https://developer.android.com/training/cars/communication/notification-messaging
- Performance: https://developer.android.com/topic/performance
- Macrobenchmark: https://developer.android.com/topic/performance/benchmarking/macrobenchmark-overview
- Baseline Profiles: https://developer.android.com/topic/performance/baselineprofiles/overview

Punti usati: host/template, navigation/POI categories, MessagingStyle, voice reply,
performance tooling.

## Apple e CarPlay

- CarPlay: https://developer.apple.com/carplay/
- Core Location: https://developer.apple.com/documentation/corelocation
- ActivityKit: https://developer.apple.com/documentation/activitykit
- MetricKit: https://developer.apple.com/documentation/metrickit
- App Store Review Guidelines: https://developer.apple.com/app-store/review/guidelines/

Punti usati: messaging SiriKit, navigation categories, widgets/Live Activities,
entitlement e strumenti.

## Navigatori esterni

- Waze Deep Links: https://developers.google.com/waze/deeplinks
- Waze Transport SDK overview: https://developers.google.com/waze/intro-transport
- Google Maps URLs: https://developers.google.com/maps/documentation/urls/get-started
- Google Navigation SDK Android: https://developers.google.com/maps/documentation/navigation/android-sdk/overview
- Google Navigation SDK iOS: https://developers.google.com/maps/documentation/navigation/ios-sdk/overview
- Sygic Maps SDK: https://developers.sygic.com/maps-sdk/

## Kotlin, Java e Rust

- Kotlin Multiplatform: https://kotlinlang.org/docs/multiplatform.html
- Kotlin coroutines: https://kotlinlang.org/docs/coroutines-overview.html
- Jetpack Compose: https://developer.android.com/develop/ui/compose
- Java 21: https://docs.oracle.com/en/java/javase/21/
- Rust book: https://doc.rust-lang.org/book/
- Rustonomicon: https://doc.rust-lang.org/nomicon/
- UniFFI repository: https://github.com/mozilla/uniffi-rs
- Swift: https://docs.swift.org/swift-book/documentation/the-swift-programming-language/
- SwiftUI: https://developer.apple.com/xcode/swiftui/

## Dati e backend

- PostgreSQL: https://www.postgresql.org/docs/
- PostGIS: https://postgis.net/documentation/
- SQLDelight: https://sqldelight.github.io/sqldelight/
- Ktor: https://ktor.io/docs/
- Spring Boot: https://docs.spring.io/spring-boot/

## Processo e documentazione

Il metodo è adattato anche dall'organizzazione pubblica di Alfred:

- repository: https://github.com/kinderp/alfred
- regole operative: https://github.com/kinderp/alfred/blob/main/docs/it/00-regole-operative.md
- documentazione didattica: https://github.com/kinderp/alfred/tree/main/docs/it
- commenting style: https://github.com/kinderp/alfred/blob/main/docs/commenting-style.md

## Regola di aggiornamento

Quando una fonte cambia una capability o policy:

1. aprire issue;
2. identificare documenti/ADR coinvolti;
3. eseguire spike o test;
4. aggiornare data di verifica;
5. non cambiare silenziosamente la matrice.
