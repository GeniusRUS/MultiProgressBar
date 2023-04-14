plugins {
    id("com.android.application") version "8.0.0" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    id("org.jetbrains.dokka") version "1.6.10" apply false
    id("com.vanniktech.maven.publish") version "0.25.1" apply false
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}