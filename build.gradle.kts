buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.1.2")
        classpath(kotlin("gradle-plugin", version = "1.6.10"))
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:1.6.10")
        classpath("com.vanniktech:gradle-maven-publish-plugin:0.19.0")
    }
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}