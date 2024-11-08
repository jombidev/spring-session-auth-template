import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    kotlin("jvm") version "1.9.24"

    id("org.springframework.boot") version "3.4.0-RC1" // TODO: Bump version to 3.4.0 (stable) when it releases.
    id("io.spring.dependency-management") version "1.1.5"

    kotlin("plugin.jpa") version "1.9.24"
    kotlin("plugin.spring") version "1.9.24"
}

repositories {
    mavenCentral()
}

allprojects {
    apply(plugin = "org.jetbrains.kotlin.kapt")
    apply(plugin = "org.jetbrains.kotlin.jvm")

    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "org.jetbrains.kotlin.plugin.jpa")

    group = "dev.jombi"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
        maven("https://repo.spring.io/milestone")
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlin {
            jvmToolchain(17)
            compilerOptions {
                languageVersion.set(KotlinVersion.KOTLIN_1_9)
                apiVersion.set(KotlinVersion.KOTLIN_1_9)
                freeCompilerArgs.addAll("-Xjsr305=strict")
            }
        }
    }

    tasks.bootJar {
        enabled = false
    }

    dependencies {
        /// SPRING BOOT
        api("org.springframework.boot:spring-boot-starter-web")

        /// KOTLIN
        implementation(kotlin("reflect"))

        /// REDIS
        implementation("org.springframework.boot:spring-boot-starter-data-redis")

        /// TEST
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("org.springframework.security:spring-security-test")

        testImplementation(kotlin("test-junit5"))
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }
}

subprojects {
    dependencies {
        /// SPRING BOOT
        implementation("org.springframework.boot:spring-boot-starter-security")
        implementation("org.springframework.boot:spring-boot-starter-validation")
    }
}

dependencies {
    implementation(project(":api"))
    implementation(project(":business"))
    implementation(project(":core"))
    implementation(project(":infra"))

    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.jar {
    enabled = false
}

tasks.bootJar {
    enabled = true
}
