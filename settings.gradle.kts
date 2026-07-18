import org.gradle.api.initialization.resolve.RepositoriesMode

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
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
include(":labs:routing-contracts-cli")
include(":labs:map-scene-cli")
include(":labs:location-replay-cli")
include(":labs:route-progress-cli")
include(":labs:map-matching-cli")
