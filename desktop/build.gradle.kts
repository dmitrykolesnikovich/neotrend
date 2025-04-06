plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

group = "site.neotrend"
version = "1.0-SNAPSHOT"

kotlin {
    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":common"))
                implementation(compose.desktop.currentOs)
            }
            configurations.all {
                exclude("org.jetbrains.kotlinx", "kotlinx-coroutines-android")
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "site.neotrend.desktop.MainKt"
    }
}
