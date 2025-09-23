plugins {
    id("dev.kikugie.stonecutter")
    kotlin("jvm") version "2.2.10" apply false
    id("fabric-loom") version "1.11-SNAPSHOT" apply false
    id("com.modrinth.minotaur") version "2.+" apply false
    id("com.google.devtools.ksp") version "2.2.10-2.0.2" apply false
}
stonecutter active "1.21.4"