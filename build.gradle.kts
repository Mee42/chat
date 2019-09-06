import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.50"
    java
    application
    id("com.github.johnrengelman.shadow") version "5.1.0"
}
group = "org.othercraft"
version = "v0.0.1"
dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("io.lettuce","lettuce-core","5.1.8.RELEASE")
    implementation("com.googlecode.lanterna","lanterna","3.0.1")
    implementation("com.google.code.gson:gson:2.8.5")
}
repositories {
    mavenCentral()
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

application {
    this.mainClassName = "MainKt"
}