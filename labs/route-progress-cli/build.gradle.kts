plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(project(":shared:navigation-contracts"))
    implementation(project(":shared:route-progress"))
    implementation(project(":shared:route-progress-map-projector"))
    implementation(project(":shared:routing-contracts"))
    implementation(project(":shared:map-contracts"))
    testImplementation(kotlin("test"))
}

application {
    mainClass.set("org.traveldna.lab.progress.MainKt")
}
