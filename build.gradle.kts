plugins {
    kotlin("jvm") version "1.5.31"
    id("maven")
    id("maven-publish")
}

group = "com.d10ng"

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