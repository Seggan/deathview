plugins {
    kotlin("jvm") version "2.0.21"
    id("fabric-loom")
    id("maven-publish")
    id("com.modrinth.minotaur") version "2.+"
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
    archivesName.set(mod.id)
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
}

dependencies {
    fun fapi(vararg modules: String) = modules.forEach {
        modImplementation(fabricApi.module(it, deps["fabric_api"]))
    }

    minecraft("com.mojang:minecraft:$mcVersion")
    @Suppress("UnstableApiUsage")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-${deps["parchment"]}@zip")
    })
    modImplementation("net.fabricmc:fabric-loader:${deps["fabric_loader"]}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${deps["koltin_fabric_loader"]}")
}

tasks.processResources {
    inputs.property("id", mod.id)
    inputs.property("name", mod.name)
    inputs.property("version", mod.version)
    inputs.property("mcdep", mcDep)
    inputs.property("loader", deps["fabric_loader"])
    inputs.property("kotlin_loader", deps["koltin_fabric_loader"])

    val map = mapOf(
        "id" to mod.id,
        "name" to mod.name,
        "version" to mod.version,
        "mcdep" to mcDep,
        "loader" to deps["fabric_loader"],
        "kotlin_loader" to deps["koltin_fabric_loader"]
    )

    filesMatching("fabric.mod.json") { expand(map) }
}

tasks.withType<JavaCompile>().configureEach {
    // ensure that the encoding is set to UTF-8, no matter what the system default is
    // this fixes some edge cases with special characters not displaying correctly
    // see http://yodaconditions.net/blog/fix-for-java-file-encoding-problems-with-gradle.html
    // If Javadoc is generated, this must be specified in that task too.
    options.encoding = "UTF-8"
    options.release.set(targetJavaVersion)
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
    syncBodyFrom = rootProject.file("README.md").readText()

    token = System.getenv("MODRINTH_TOKEN")
    projectId = mod.id
    versionNumber = mod.version
    uploadFile.set(tasks.remapJar)
    dependencies {
        required.project("fabric-loader")
        required.project("fabric-language-kotlin")
    }
}

tasks.modrinth {
    dependsOn(tasks.modrinthSyncBody)
}
