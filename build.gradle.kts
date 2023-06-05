val bds100MavenUsername: String by project
val bds100MavenPassword: String by project
val bds100NpmToken: String by project
val npmJsToken: String by project

plugins {
    id("dev.petuska.npm.publish") version "3.3.1"
    kotlin("multiplatform") version "1.8.20"
    id("maven-publish")
}

group = "com.github.D10NGYANG"
version = "1.7.0"

repositories {
    mavenCentral()
    maven("https://jitpack.io" )
}

kotlin {
    jvm {
        jvmToolchain(8)
        withJava()
    }
    js(IR) {
        binaries.library()
        nodejs()
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
        register("npmjs") {
            uri.set("https://registry.npmjs.org")
        }
        register("npm-releases") {
            uri.set("https://nexus.bds100.com/repository/npm-releases/")
            authToken.set(bds100NpmToken)
        }
    }
    packages {
        named("js") {
            packageName.set("dl-latlng-util")
        }
    }
}