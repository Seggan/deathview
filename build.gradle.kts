plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.serialization") version "2.0.21"
    id("fabric-loom")
    id("maven-publish")
    id("com.modrinth.minotaur") version "2.+"
    id("io.github.seggan.kmixin") version "0.1.0"
}

class ModData {
    val id = property("mod.id").toString()
    val name = property("mod.name").toString()
    val version = property("mod.version").toString()
    val group = property("mod.group").toString()
}

class ModDependencies {
    operator fun get(name: String) = property("deps.$name").toString()
}

val mod = ModData()
val deps = ModDependencies()
val mcVersion = stonecutter.current.version
val mcDep = property("mod.mc_dep").toString()

version = "${mod.version}+${mcVersion}"
group = mod.group

base {
    archivesName = mod.id
}

val targetJavaVersion = if (stonecutter.eval(mcVersion, ">=1.20.6")) 21 else 17

java {
    toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()
}

loom {
    splitEnvironmentSourceSets()

    mods {
        register("deathview") {
            sourceSet("main")
            sourceSet("client")
        }
    }
}

repositories {
    maven("https://maven.parchmentmc.org")
    maven("https://maven.shedaniel.me/") {
        name = "Cloth"
    }
    maven("https://maven.terraformersmc.com/") {
        name = "ModMenu"
    }
}

dependencies {
    minecraft("com.mojang:minecraft:$mcVersion")
    @Suppress("UnstableApiUsage")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-${deps["parchment"]}@zip")
    })
    modImplementation("net.fabricmc:fabric-loader:${deps["fabric_loader"]}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${deps["koltin_fabric_loader"]}")
    modImplementation("me.shedaniel.cloth:cloth-config-fabric:${deps["cloth"]}")
    modImplementation("com.terraformersmc:modmenu:${deps["modmenu"]}") {
        exclude("net.fabricmc.fabric-api")
    }
}

tasks.processResources {
    val map = mapOf(
        "name" to mod.name,
        "version" to mod.version,
        "mcdep" to mcDep,
        "loader" to deps["fabric_loader"],
        "kotlin_loader" to deps["koltin_fabric_loader"],
        "cloth" to deps["cloth"]
    )

    for ((key, value) in map) {
        inputs.property(key, value)
    }

    filesMatching("fabric.mod.json") { expand(map) }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release = targetJavaVersion
}

kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${project.base.archivesName}" }
    }
}

tasks.register<Copy>("buildAndCollect") {
    group = "build"
    from(tasks.remapJar.get().archiveFile)
    into(rootProject.layout.buildDirectory.file("libs/${mod.version}"))
    dependsOn("build")
}

modrinth {
    token = property("modrinth.token").toString()
    projectId = mod.id
    versionNumber = project.version.toString()
    uploadFile.set(tasks.remapJar)
    dependencies {
        required.project("fabric-language-kotlin")
        required.project("cloth-config")
        optional.project("modmenu")
    }

    syncBodyFrom = rootProject.file("README.md").readText()
}

tasks.modrinth {
    dependsOn(tasks.modrinthSyncBody)
}
