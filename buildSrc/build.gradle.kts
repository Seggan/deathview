plugins {
    `kotlin-dsl`
    id("com.gradleup.shadow") version "8.3.2"
}

repositories {
    mavenCentral()
    maven("https://repo.spongepowered.org/maven/")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("org.spongepowered:mixin:0.8.7")
    implementation(files("libs/kotlin-grammar-parser-0.1.jar", "libs/kotlin-grammar-tools-0.1.jar"))
}

kotlin {
    jvmToolchain(21)
}

gradlePlugin {
    plugins {
        create("kotlin-mixin-plugin") {
            id = "io.github.seggan.kotlin-mixin-plugin"
            implementationClass = "io.github.seggan.kmixin.KMixinPlugin"
        }
    }
}