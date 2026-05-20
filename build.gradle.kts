import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    id("com.gradleup.shadow") version "9.3.0"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.21"
}

// Version of PacketEventsSK
val projectVersion = "1.1.3"
// API Version
val apiVersion = "26.1.2"
// Minimum paper version that PacketEventsSK supports
val minApiVersion = "1.21.10"
// Where this builds on the server
val serverLocation = "C:/Users/jaspe/Desktop/Servers/packetSKTestServer/plugins"

repositories {
    mavenCentral()

    // Paper (Server Software)
    maven("https://repo.papermc.io/repository/maven-public/")

    // Skript
    maven("https://repo.skriptlang.org/releases")

    // EntityLib (entity management)
    maven("https://maven.pvphub.me/tofaa")

    // Skript Registration (SKR) and SkBee (Skript implementation of NBT-API)
    maven("https://jitpack.io") {
        mavenContent {
            // JitPack holds an outdated version of entity lib that's private, causes issues so exclude and just use pvphub
            excludeGroup("io.github.tofaa2")
        }
    }

    // PacketEvents (packet library)
    maven("https://repo.codemc.org/repository/maven-public")
}

dependencies {
    // Paper (and NMS)
    paperweight.paperDevBundle("$apiVersion.build.+")

    // PacketEvents
    compileOnly("com.github.retrooper:packetevents-spigot:2.12.1")

    // Skript
    compileOnly("com.github.SkriptLang:Skript:2.15.2")

    // SkBee
    compileOnly("com.github.ShaneBeee:SkBee:3.23.0")

    // SkriptRegistration (SKR)
    implementation("com.github.ShaneBeee:SkriptRegistration:1.4.2")

    // EntityLib
    implementation("io.github.tofaa2:spigot:3.3.0-SNAPSHOT")

    // bStats Metrics
    implementation("org.bstats:bstats-bukkit:3.2.1")
}

tasks {
    register<Copy>("buildServer") {
        group = "build"

        dependsOn("shadowJar")

        val shadowJarTask = named<ShadowJar>("shadowJar")

        from(shadowJarTask.flatMap { it.archiveFile })
        into(file(serverLocation))

        outputs.dir(file(serverLocation))
    }
    processResources {
        val props = mapOf(
            "projectVersion" to projectVersion,
            "apiversion" to minApiVersion
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