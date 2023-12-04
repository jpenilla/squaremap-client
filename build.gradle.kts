plugins {
    id("quiet-fabric-loom") version "1.4-SNAPSHOT"
}

group = "xyz.jpenilla.squaremap.client"
version = "1.4.0-SNAPSHOT"

val mcVersion = "1.20.2"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
    maven("https://maven.terraformersmc.com/") {
        mavenContent {
            includeGroup("com.terraformersmc")
        }
    }
    maven("https://maven.parchmentmc.org/") {
        mavenContent {
            includeGroup("org.parchmentmc.data")
        }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:$mcVersion")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-1.20.2:2023.10.08@zip")
    })
    modImplementation("net.fabricmc:fabric-loader:0.15.0")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.91.0+1.20.2")
    modImplementation("com.terraformersmc:modmenu:8.0.0")

    val configurate = "org.spongepowered:configurate-hocon:4.1.2"
    transitiveInclude(configurate)
    modImplementation(configurate)
}

tasks {
    processResources {
        val props = mapOf("version" to project.version)
        inputs.properties(props)
        filesMatching("fabric.mod.json") {
            expand(props)
        }

        filteringCharset = Charsets.UTF_8.name()
    }
    withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }
    jar {
        from("LICENSE") {
            rename { "LICENSE-${project.name}" }
        }
    }
}

loom {
    mixin.defaultRefmapName.set("squaremap-client.refmap.json")
    accessWidenerPath.set(file("src/main/resources/squaremap-client.accesswidener"))
}

loom {
    runs {
        named("client") {
            System.getProperty("user.name", null)?.let { userName ->
                programArgs("--username", userName)
            }
        }
    }
}
