buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        google()
    }
}

plugins {
    kotlin("jvm") version "1.7.10"
}

allprojects {
    group = "com.erzbir.numeron"

    version = "1.0.0"

    repositories {
        mavenLocal()

        mavenCentral()
        gradlePluginPortal()
        google()
    }

    tasks.withType<JavaCompile>() {
        options.encoding = "UTF-8"
    }
}