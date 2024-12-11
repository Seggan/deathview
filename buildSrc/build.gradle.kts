plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("org.ow2.asm:asm:9.7.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
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