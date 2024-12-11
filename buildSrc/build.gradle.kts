plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
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