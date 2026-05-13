import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    id("com.gradleup.shadow") version "9.3.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.21"
}

group = "threeadd"
version = "1.1.0"

repositories {
    mavenCentral()

    // Paper (Server Software)
    maven("https://repo.papermc.io/repository/maven-public/")

    // Skript
    maven("https://repo.skriptlang.org/releases")

    // Skript Registration (SKR) and SkBee (Skript implementation of NBT-API)
    maven("https://jitpack.io")

    // PacketEvents (packet library)
    maven("https://repo.codemc.org/repository/maven-public")

    // EntityLib (entity management)
    maven("https://maven.pvphub.me/tofaa")
}

dependencies {
    // Paper (and NMS)
    paperweight.paperDevBundle("26.1.2.build.+")

    // PacketEvents
    compileOnly("com.github.retrooper:packetevents-spigot:2.12.1")

    // Skript
    compileOnly("com.github.SkriptLang:Skript:2.15.2")

    // SkBee
    compileOnly("com.github.ShaneBeee:SkBee:3.22.1")

    // SkriptRegistration (SKR)
    implementation("com.github.ShaneBeee:SkriptRegistration:1.2.0")

    // EntityLib
    implementation("io.github.tofaa2:spigot:3.2.3-SNAPSHOT")

    // bStats Metrics
    implementation("org.bstats:bstats-bukkit:3.2.1")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.named<ShadowJar>("shadowJar") {
    archiveFileName = project.name + "-" + project.version + ".jar"

    relocate("me.tofaa.entitylib", "dev.threeadd.packeteventssk.entitylib")
    relocate("org.bstats", "dev.threeadd.packeteventssk.metrics")
    relocate("com.github.shanebee.skr", "dev.threeadd.packeteventssk.skr")
}

tasks.runServer {
    minecraftVersion("1.21")
}

val targetJavaVersion = 25
java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"

    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible()) {
        options.release.set(targetJavaVersion)
    }
}

tasks.processResources {
    val props = mapOf("version" to project.version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}