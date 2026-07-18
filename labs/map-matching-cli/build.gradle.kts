import org.gradle.api.tasks.testing.Test

plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(project(":shared:fake-map-matcher"))
    implementation(project(":shared:map-matching-testkit"))
    implementation(project(":shared:route-progress"))
    testImplementation(kotlin("test"))
}

application {
    mainClass.set("org.traveldna.lab.matching.MainKt")
}

tasks.named<Test>("test") {
    workingDir(rootProject.projectDir)
}
