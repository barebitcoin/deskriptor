
import com.parmet.buf.gradle.BUF_BUILD_DIR
import com.parmet.buf.gradle.GENERATED_DIR
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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

tasks {
    val fatJar = register<Jar>("fatJar") {
        dependsOn.addAll(listOf("compileJava", "compileKotlin", "processResources")) // We need this for Gradle optimization to work
        archiveClassifier.set("standalone") // Naming the jar
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest { attributes(mapOf("Main-Class" to application.mainClass)) } // Provided we set it up in the application plugin configuration
        val sourcesMain = sourceSets.main.get()
        val contents = configurations.runtimeClasspath.get()
            .map { if (it.isDirectory) it else zipTree(it) } +
                sourcesMain.output
        from(contents)
    }
    build {
        dependsOn(fatJar) // Trigger fat jar creation during build
    }
}
