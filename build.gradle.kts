import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.4.20"
    id("com.github.johnrengelman.shadow") version("6.1.0")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
    mavenCentral()
}

tasks {
    named<ShadowJar>("shadowJar") {
        archiveBaseName.set("fatjar")
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to "com.redpills.correction.framework.Main"))
        }
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }
}

dependencies {
    implementation("org.eclipse.jgit:org.eclipse.jgit:5.7.0.202003110725-r")
    implementation("org.springframework:spring-web:5.2.11.RELEASE")
    implementation("junit:junit:4.13")
    implementation("io.ktor:ktor-server-core:1.4.0")
    implementation("io.ktor:ktor-server-netty:1.4.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.11.3")
    implementation(kotlin("stdlib-jdk8"))

    testImplementation("junit:junit:4.13")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        suppressWarnings = true
    }
}