val bds100MavenUsername: String by project
val bds100MavenPassword: String by project
val npmJsToken: String by project

plugins {
    id("dev.petuska.npm.publish") version "3.3.1"
    kotlin("multiplatform") version "1.8.21"
    id("maven-publish")
}

group = "com.github.D10NGYANG"
version = "1.7.2"

repositories {
    mavenCentral()
}

kotlin {
    jvm {
        jvmToolchain(8)
        withJava()
    }
    js(IR) {
        moduleName = "dl-latlng-util"
        binaries.library()
        binaries.executable()
        nodejs()
        generateTypeScriptDefinitions()
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
            authToken.set(npmJsToken)
        }
    }
    packages {
        named("js") {
            packageName.set("dl-latlng-util")
        }
    }
}