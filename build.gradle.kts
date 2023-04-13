val bds100MavenUsername: String by project
val bds100MavenPassword: String by project

plugins {
    id("dev.petuska.npm.publish") version "3.2.1"
    kotlin("multiplatform") version "1.8.10"
    id("maven-publish")
}

group = "com.github.D10NGYANG"
version = "1.6.8"

repositories {
    mavenCentral()
    maven("https://jitpack.io" )
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
    }
    js(IR) {
        nodejs()
        binaries.library()
        binaries.executable()
    }
    ios {
        binaries {
            framework {}
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

publishing {
    repositories {
        maven {
            url = uri("/Users/d10ng/project/kotlin/maven-repo/repository")
        }
        maven {
            credentials {
                username = bds100MavenUsername
                password = bds100MavenPassword
            }
            setUrl("https://nexus.bds100.com/repository/maven-releases/")
        }
    }
}

npmPublish {
    registries {
        register("npm-hosted") {
            uri.set("https://nexus.bds100.com/repository/npm-hosted")
        }
    }
    packages {
        named("js") {
            scope.set("hailiao")
        }
    }
}