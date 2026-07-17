plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.jvm) apply false
}

allprojects {
    group = "org.traveldna"
    version = "0.1.0-SNAPSHOT"
}
