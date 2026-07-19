import org.gradle.api.initialization.resolve.RepositoriesMode

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "tdna"

include(":shared:plugin-sdk")
include(":shared:geo-contracts")
include(":shared:routing-contracts")
include(":shared:routing-testkit")
include(":shared:fake-route-planner")
include(":shared:map-contracts")
include(":shared:map-testkit")
include(":shared:fake-map-renderer")
include(":shared:route-map-projector")
include(":shared:location-contracts")
include(":shared:location-replay")
include(":shared:navigation-contracts")
include(":shared:route-progress")
include(":shared:route-progress-map-projector")
include(":shared:map-matching-contracts")
include(":shared:map-matching-testkit")
include(":shared:fake-map-matcher")
include(":shared:off-route-contracts")
include(":shared:off-route-state-machine")
include(":shared:reroute-coordinator")
include(":labs:routing-contracts-cli")
include(":labs:map-scene-cli")
include(":labs:location-replay-cli")
include(":labs:route-progress-cli")
include(":labs:map-matching-cli")
include(":labs:missed-exit-cli")

// Foundation-only commands set -Ptdna.includeAndroid=false so students can run
// the deterministic shared Labs without installing the Android SDK. Android
// builds use the default and include the application module.
if (providers.gradleProperty("tdna.includeAndroid").orNull != "false") {
    include(":apps:android")
}
