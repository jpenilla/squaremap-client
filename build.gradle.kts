plugins {
    id("quiet-fabric-loom") version "0.10-SNAPSHOT"
}

group = "net.pl3x.map.fabric"
version = "1.3"

val mcVersion = "1.18.1"

base {
    archivesName.set("Pl3xMap")
}

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
        inputs.property("version", project.version)

        filesMatching("fabric.mod.json") {
            expand(mapOf("version" to project.version))
        }
    }
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(17)
    }
    jar {
        from("LICENSE") {
            rename { "LICENSE-${project.name}" }
        }
    }
}

loom {
    mixin.defaultRefmapName.set("pl3xmap.refmap.json")
    accessWidenerPath.set(file("src/main/resources/pl3xmap.accesswidener"))
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
