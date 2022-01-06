plugins {
    kotlin("jvm") version "1.5.31"
    id("maven-publish")
}

group = "com.github.D10NGYANG"

repositories {
    mavenCentral()
    maven("https://jitpack.io" )
}

kotlin {
    java {
        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(kotlin("stdlib"))
}

publishing {
    publications {
        create("release", MavenPublication::class) {
            from(components.getByName("java"))
        }
    }
}