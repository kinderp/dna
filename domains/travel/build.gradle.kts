plugins {
    // Keep the Android, Compose and Kotlin plugin families in the same root
    // classloader. This is required by the mixed Android/KMP build so AGP's
    // built-in Kotlin integration can see the matching AGP APIs.
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.jvm) apply false
}

allprojects {
    group = "org.traveldna"
    version = "0.1.0-SNAPSHOT"
}
