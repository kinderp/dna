plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(project(":shared:fake-route-planner"))
}

application {
    mainClass.set("org.traveldna.lab.routing.MainKt")
}
