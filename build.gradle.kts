// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    extra.apply{
        set("dokka_version", "1.4.30")
    }
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.1.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.30")
        classpath("com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.5")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}

// решает проблему ошибки генерации javadoc'ов при gradlew install
subprojects {
    tasks.withType<Javadoc>().all { enabled = false }
}