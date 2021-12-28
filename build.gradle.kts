plugins {
    id("quiet-fabric-loom") version "0.10-SNAPSHOT"
}

group = "xyz.jpenilla.squaremap.client"
version = "1.4.0-SNAPSHOT"

val mcVersion = "1.18.1"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    maven("https://maven.terraformersmc.com/") {
        mavenContent {
            includeGroup("com.terraformersmc")
        }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:$mcVersion")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:0.12.12")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.45.0+1.18")
    modImplementation("com.terraformersmc:modmenu:3.0.0")
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
