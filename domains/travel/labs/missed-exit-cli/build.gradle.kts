import org.gradle.api.tasks.testing.Test

plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(project(":shared:off-route-state-machine"))
    implementation(project(":shared:reroute-coordinator"))
    implementation(project(":shared:fake-route-planner"))
    implementation(project(":shared:route-progress"))
    implementation(project(":shared:plugin-sdk"))
    implementation(project(":shared:routing-contracts"))
    testImplementation(kotlin("test"))
}

application {
    mainClass.set("org.traveldna.lab.reroute.MainKt")
}

tasks.named<Test>("test") {
    workingDir(rootProject.projectDir)
}
