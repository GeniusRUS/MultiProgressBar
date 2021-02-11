import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins {
    id("com.jfrog.bintray")
    id("maven-publish")
    id("com.android.library")
    kotlin("android")
}

android {
    compileSdkVersion(30)
    defaultConfig {
        minSdkVersion(16)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    lintOptions {
        isAbortOnError = false
    }

    buildFeatures {
        buildConfig = false
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation(kotlin("stdlib-jdk7", KotlinCompilerVersion.VERSION))

    testImplementation("junit:junit:4.13.1")
    androidTestImplementation("androidx.test:runner:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
}

group = "com.geniusrus.multiprogressbar"
version = android.defaultConfig.versionName.toString()

tasks {
    register("androidSourcesJar", Jar::class) {
        archiveClassifier.set("sources")
        from(android.sourceSets.getByName("main").java.srcDirs)
    }
}

publishing {
    publications {
        register<MavenPublication>("MultiProgressBarLibrary") {
            artifactId = "multiprogressbar"

            afterEvaluate { artifact(tasks.getByName("bundleReleaseAar")) }
            artifact(tasks.getByName("androidSourcesJar"))

            pom {
                name.set("MultiProgressBar")
                description.set("Android library for multiple displays of progress like Instagram Stories")
                url.set("https://github.com/GeniusRUS/MultiProgressBar")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("GeniusRUS")
                        name.set("Viktor Likhanov")
                        email.set("Gen1usRUS@yandex.ru")
                    }
                }
                scm {
                    connection.set("git://github.com/GeniusRUS/MultiProgressBar.git")
                    developerConnection.set("git://github.com/GeniusRUS/MultiProgressBar.git")
                    url.set("https://github.com/GeniusRUS/MultiProgressBar")
                }

                // Saving external dependencies list into .pom-file
                withXml {
                    fun groovy.util.Node.addDependency(dependency: Dependency, scope: String) {
                        appendNode("dependency").apply {
                            appendNode("groupId", dependency.group)
                            appendNode("artifactId", dependency.name)
                            appendNode("version", dependency.version)
                            appendNode("scope", scope)
                        }
                    }

                    asNode().appendNode("dependencies").let { dependencies ->
                        // List all "api" dependencies as "compile" dependencies
                        configurations.api.get().allDependencies.forEach {
                            dependencies.addDependency(it, "compile")
                        }
                        // List all "implementation" dependencies as "runtime" dependencies
                        configurations.implementation.get().allDependencies.forEach {
                            dependencies.addDependency(it, "runtime")
                        }
                    }
                }
            }
        }
    }

    repositories {
        maven {
            name = "bintray"
            credentials {
                username = gradleLocalProperties(rootDir).getProperty("bintray.user")
                password = gradleLocalProperties(rootDir).getProperty("bintray.apikey")
            }
            url = uri("https://api.bintray.com/maven/geniusrus/MultiProgressBar/$group/;publish=1")
        }
    }
}