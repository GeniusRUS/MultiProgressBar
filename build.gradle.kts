plugins {
    id("com.android.application") version "7.4.1" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    id("org.jetbrains.dokka") version "1.6.10" apply false
    id("com.vanniktech.maven.publish") version "0.24.0" apply false
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}