plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    maven("https://repo.spongepowered.org/maven/")
}

dependencies {
    implementation("org.ow2.asm:asm:9.7.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("org.spongepowered:mixin:0.8.7")
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