# Architettura di plugin, provider e adapter

## Problema

Travel DNA usa componenti complessi che possono cambiare:

- renderer cartografico;
- tile source;
- routing engine;
- navigation runtime;
- geocoding;
- traffic source;
- navigatori esterni;
- TTS;
- storage e messaggistica.

Se i tipi di una libreria entrano nel dominio, sostituirla richiede modifiche in
mappa, diario, backend, UI e test. La soluzione è una combinazione di porte,
adapter, capability e composition root.

## Principio

> Travel DNA dipende dai propri contratti, non dalle librerie che li
> implementano.

Questo non promette sostituzioni a costo zero. Promette che il costo resta
confinato e misurabile.

## Scomporre le capacità

Evitare `MapService` o `NavigationService` giganteschi.

### Cartografia

```text
MapSceneRenderer
TileSource
MapStyleSource
PlaceSearch
PlaceCatalog
CameraController
MapInteractionSource
```

### Routing e guidance

```text
RoutePlanner
MapMatcher
RouteProgressEngine
OffRouteDetector
ReroutingCoordinator
ManeuverNarrator
VoiceGuidance
NavigationSessionStore
```

### Navigazione esterna

```text
ExternalNavigationProvider
DestinationHandoff
WaypointHandoff
AvailabilityProbe
ReturnLinkCapability
```

### Automotive

```text
CarConversationSurface
CarPoiSurface
CarNavigationSurface
CarLiveActivitySurface
```

## Contratti piccoli

Un contratto deve esprimere ciò che Travel DNA usa, non ricopiare l'intero SDK.

```kotlin
interface RoutePlannerPort {
    val descriptor: ProviderDescriptor

    suspend fun plan(request: RouteRequest): RoutePlanningResult
}
```

```kotlin
interface MapSceneRendererPort {
    suspend fun installBaseScene(scene: MapScene)
    fun apply(delta: MapSceneDelta)
    fun interactions(): Flow<MapInteraction>
}
```

`MapSceneDelta` evita di reinstallare route e layer a ogni aggiornamento.

## Descriptor

```kotlin
data class ProviderDescriptor(
    val id: ProviderId,
    val implementationVersion: String,
    val contractVersion: Int,
    val capabilities: Set<ProviderCapability>,
    val supportedPlatforms: Set<TargetPlatform>,
    val licenseNotices: List<LicenseNotice>,
    val maturity: ProviderMaturity
)
```

Maturity:

```text
EXPERIMENTAL
BETA
SUPPORTED
DEPRECATED
```

## Capability

Esempi:

```text
VECTOR_RENDERING
OFFLINE_TILES
ONLINE_ROUTING
OFFLINE_ROUTING
ALTERNATIVE_ROUTES
MAP_MATCHING
LANE_GUIDANCE
SPEED_LIMITS
LIVE_TRAFFIC
HISTORICAL_TRAFFIC
VOICE_GUIDANCE
ANDROID_AUTO
CARPLAY
DESTINATION_HANDOFF
WAYPOINT_HANDOFF
```

L'app chiede una capacità, non il nome del provider.

## Error model canonico

Gli adapter traducono errori specifici in una tassonomia stabile:

```text
InvalidRequest
UnsupportedCapability
NoRoute
NetworkUnavailable
ProviderUnavailable
RateLimited
DataNotInstalled
PermissionDenied
Cancelled
TimedOut
InternalProviderFailure
```

Il dettaglio del provider può entrare in diagnostica privata, non nella business
rule.

## Plugin mobile e backend

### Mobile

I plugin sono moduli compilati nell'app. Possono essere selezionati tramite
configurazione o feature flag, ma non si scarica codice arbitrario dopo la
pubblicazione.

### Backend

Un provider può essere:

- classe interna;
- modulo separato;
- processo locale;
- servizio remoto;
- deployment indipendente.

Il routing gateway nasconde questa differenza all'app.

## Registry

```kotlin
class ProviderRegistry(
    val routePlanners: List<RoutePlannerPort>,
    val mapRenderers: List<MapSceneRendererPort>,
    val externalNavigators: List<ExternalNavigationProvider>,
    val carSurfaces: List<CarSurfaceProvider>
)
```

Il registry viene interrogato durante composizione o cambio controllato. Il
provider selezionato viene poi conservato in `ActiveTripSession`.

## Selection policy

Può considerare:

- piattaforma;
- disponibilità app esterna;
- rete;
- regione offline;
- capability;
- salute;
- preferenza utente;
- costo;
- paese;
- feature flag;
- sperimentazione.

Esempio:

```text
utente sceglie Waze e Waze è disponibile
-> Waze handoff

utente sceglie navigazione interna, rete disponibile
-> remote route planner + Ferrostar

rete assente, regione installata
-> offline route planner
```

## Fallback

Un fallback non deve cambiare silenziosamente semantica importante.

Esempio:

```text
primary supports live traffic
fallback does not
```

Il risultato deve dichiarare la provenance e la capability mancante. La UI può
mostrare “traffico non disponibile”.

## Shadow mode

Per introdurre un nuovo provider:

```text
request
  -> primary result shown
  -> shadow result recorded for comparison
```

Confronti:

- successo/fallimento;
- latenza;
- distanza e durata;
- geometria;
- manovre;
- consumo memoria;
- off-route e reroute;
- errori.

Il risultato shadow non influenza l'utente finché non supera criteri definiti.

## Branch by abstraction

Sequenza:

1. definire il port;
2. coprire il provider corrente con contract test;
3. introdurre il nuovo adapter;
4. eseguire shadow;
5. attivare internamente;
6. rollout limitato;
7. provider primario con fallback;
8. rimuovere il vecchio solo dopo osservazione.

## Contract test kit

```kotlin
abstract class RoutePlannerContract {
    abstract fun provider(): RoutePlannerPort

    @Test fun startsNearRequestedOrigin() { }
    @Test fun endsNearRequestedDestination() { }
    @Test fun cancellationIsObserved() { }
    @Test fun errorsAreCanonical() { }
    @Test fun vendorTypesDoNotEscape() { }
}
```

Un test non può verificare direttamente “nessun vendor type” a runtime in tutti i
linguaggi; questa regola va protetta anche con dipendenze di build e architecture
test.

## Costo dell'astrazione

L'indirection è trascurabile se risolta una volta, ma mapping e copie possono
essere costosi. Regole:

- non convertire geometrie complete a ogni GPS;
- non serializzare per passare tra moduli nello stesso processo;
- usare tipi compatti per snapshot;
- misurare attraversamenti FFI;
- evitare wrapper annidati senza responsabilità.

## Quando non creare un adapter

Non serve un adapter per una libreria interna puramente locale se:

- non attraversa un confine significativo;
- non è probabile sostituzione;
- il wrapper replicherebbe l'API senza semplificarla;
- non migliora testabilità o sicurezza.

## Dipendenze iniziali

| Capacità | Provider iniziale | Contratto Travel DNA |
| --- | --- | --- |
| Rendering | MapLibre Native | `MapSceneRendererPort` |
| Routing | Valhalla via gateway | `RoutePlannerPort` |
| Guidance | Ferrostar | `NavigationRuntimePort` |
| Navigazione esterna | Waze/Google/system | `ExternalNavigationProvider` |
| Diario | Travel DNA | moduli Journey/Journal |
| Presenza | Travel DNA backend | `RoadPresencePort` |
| Chat | Travel DNA backend | `ConversationPort` |

## Anti-pattern

```text
if (provider == VALHALLA) dentro il dominio

MapLibre Feature usata come Place

JSON del provider salvato come modello principale DB

plugin registry interrogato dieci volte al secondo

adapter che decide privacy o ranking

fallback che finge lane guidance non disponibile
```
