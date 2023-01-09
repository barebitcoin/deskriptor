import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.parmet.buf.gradle.BUF_BUILD_DIR
import com.parmet.buf.gradle.GENERATED_DIR

plugins {
    kotlin("jvm") version "1.7.20"
    application

    // Invoking `buf generate` as part of the build, 
    // and place the sources in the correct places. 
    // Why can't everything just be simple folders...
    // https://github.com/andrewparmet/buf-gradle-plugin
    id("com.parmet.buf") version "0.8.2"
}

group = "no.bb"
version = "1.0-SNAPSHOT"

val kotest = "5.5.4"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    implementation("org.bitcoindevkit:bdk-jvm:0.25.0")

    // TODO: these can be upgraded
    implementation("com.google.api.grpc:proto-google-common-protos:2.10.0")
    implementation("io.grpc:grpc-stub:1.46.0")
    implementation("io.grpc:grpc-protobuf:1.46.0")
    implementation("io.grpc:grpc-services:1.46.0")
    implementation("io.grpc:grpc-netty:1.46.0")
    implementation("com.google.protobuf:protobuf-kotlin:3.20.1")
    implementation("io.grpc:grpc-kotlin-stub:1.3.0")

    // logging
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.4")
    implementation("org.slf4j:slf4j-simple:2.0.3")

    // base58check stuff
    implementation("fr.acinq.bitcoin:bitcoin-kmp-jvm:0.10.0")

    testImplementation("io.kotest:kotest-runner-junit5:$kotest")
    testImplementation("io.kotest:kotest-assertions-core:$kotest")
    testImplementation("io.kotest:kotest-framework-datatest:$kotest")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.named("compileKotlin").configure {
    dependsOn("bufGenerate")
}

// TODO: is this needed?
sourceSets["main"].kotlin {
    srcDir("$buildDir/$BUF_BUILD_DIR/$GENERATED_DIR/kotlin")
}

sourceSets["main"].java {
    srcDir("$buildDir/$BUF_BUILD_DIR/$GENERATED_DIR/java")
}

application {
    mainClass.set("no.bb.deskriptor.DeskriptorKt")
}