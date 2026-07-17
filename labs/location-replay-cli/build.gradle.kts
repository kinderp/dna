import org.gradle.api.tasks.testing.Test

plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(project(":shared:location-replay"))
    testImplementation(kotlin("test"))
}

application {
    mainClass.set("org.traveldna.lab.location.MainKt")
}

tasks.named<Test>("test") {
    workingDir(rootProject.projectDir)
}
