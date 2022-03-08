import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins {
    id("com.android.library")
    id("org.jetbrains.dokka")
    id("com.vanniktech.maven.publish")
    kotlin("android")
}

tasks.dokkaJavadoc.configure {
    outputDirectory.set(buildDir.resolve("javadoc"))
}

android {
    compileSdk = 32
    defaultConfig {
        minSdk = 16
        targetSdk = 32
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        buildConfig = false
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation(kotlin("stdlib-jdk7", KotlinCompilerVersion.VERSION))

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}