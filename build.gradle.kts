buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.0")
        classpath(kotlin("gradle-plugin", version = "1.5.21"))
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:1.4.32")
        classpath("com.vanniktech:gradle-maven-publish-plugin:0.15.1")
    }
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}