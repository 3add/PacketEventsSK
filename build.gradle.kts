plugins {
    java
    id("com.gradleup.shadow") version "9.3.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.21"
}

// Version of PacketEventsSK
val projectVersion = "1.1.1"
// Server version
val serverVersion = "26.1.2"
// Minimum version of Minecraft that PacketEventsSK supports
val apiVersion = "1.21.10"

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
    paperweight.paperDevBundle("$serverVersion.build.+")

    // PacketEvents
    compileOnly("com.github.retrooper:packetevents-spigot:2.12.1")

    // Skript
    compileOnly("com.github.SkriptLang:Skript:2.15.2")

    // SkBee
    compileOnly("com.github.ShaneBeee:SkBee:3.23.0")

    // SkriptRegistration (SKR)
    implementation("com.github.ShaneBeee:SkriptRegistration:1.4.2")

    // EntityLib
    implementation("io.github.tofaa2:spigot:3.2.3-SNAPSHOT")

    // bStats Metrics
    implementation("org.bstats:bstats-bukkit:3.2.1")
}

tasks {
    processResources {
        val props = mapOf(
            "projectVersion" to projectVersion,
            "apiversion" to apiVersion
        )

        filesNotMatching("assets/**") {
            expand(props)
        }
    }
    compileJava {
        sourceCompatibility = "21"
        targetCompatibility = "21"

        options.isIncremental = false
    }
    shadowJar {
        archiveFileName = project.name + "-" + projectVersion + ".jar"

        relocate("me.tofaa.entitylib", "dev.threeadd.packeteventssk.entitylib")
        relocate("org.bstats", "dev.threeadd.packeteventssk.metrics")
        relocate("com.github.shanebee.skr", "dev.threeadd.packeteventssk.skr")
    }
    jar {
        enabled = false
        dependsOn(shadowJar)
    }
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(25))
        }
    }
}

configurations.matching { it.isCanBeResolved }.all {
    attributes {
        attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 25)
    }
}