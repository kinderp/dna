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
                api(project(":shared:location-contracts"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}
