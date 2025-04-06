plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    kotlin("native.cocoapods")
    kotlin("plugin.serialization")
}

group = "site.neotrend"
version = "1.0-SNAPSHOT"

kotlin {
    jvm("desktop") {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }
    ios()
    iosSimulatorArm64()
    cocoapods {
        summary = "neotrend"
        homepage = "neotrend.site"
        version = "1.0"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../ios/Podfile")
        framework {
            baseName = "common"
            isStatic = true
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                implementation(libs.koin.core)
                implementation(libs.ktor.json)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.serialization)
                implementation(libs.ktor.contentnegotiation)
                implementation(libs.ktor.serialization.json)
                implementation(libs.kotlin.serialization)
                implementation(libs.material.icon.core)
                implementation(libs.material.icon.extended)
                api(libs.image.loader)
                implementation(libs.compose.util)
            }
        }
        val desktopMain by getting {
            dependencies {
                api(compose.preview)
                implementation(libs.koin.core)
                implementation(libs.ktor.java)
                implementation("uk.co.caprica:vlcj:4.7.0")
                implementation("uk.co.caprica:vlcj-natives:4.7.0")
            }
        }
        val iosMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(libs.ktor.ios)
            }
        }
        val iosSimulatorArm64Main by getting {
            dependsOn(iosMain)
        }
    }
}
