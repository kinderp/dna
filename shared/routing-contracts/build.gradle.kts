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
                api(project(":shared:plugin-sdk"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}
