plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    jvm()
    linuxX64()
    jvmToolchain(21)

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":shared:map-matching-contracts"))
                api(project(":shared:location-contracts"))
                api(project(":shared:routing-contracts"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}
