plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(project(":shared:fake-route-planner"))
    implementation(project(":shared:fake-map-renderer"))
    implementation(project(":shared:route-map-projector"))
}

application {
    mainClass.set("org.traveldna.lab.map.MainKt")
}
