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
include(":shared:routing-contracts")
include(":shared:routing-testkit")
include(":shared:fake-route-planner")
include(":shared:map-contracts")
include(":shared:map-testkit")
include(":shared:fake-map-renderer")
include(":shared:route-map-projector")
include(":labs:routing-contracts-cli")
include(":labs:map-scene-cli")
