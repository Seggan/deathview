package io.github.seggan.kmixin

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

open class GenerationTask : DefaultTask() {

    private val buildDir = project.layout.buildDirectory.get().asFile

    @TaskAction
    fun generate() {
        val mixins = getMixins() ?: return
        for (config in mixins) {
            val subDir = if (config.global) "main" else config.environment.toString()
            val file = buildDir.resolve("resources/$subDir/${config.file}")
            val mixinJson = Json.parseToJsonElement(file.readText())
            val pkg = mixinJson.jsonObject["package"]?.jsonPrimitive?.content ?: continue
            val mixinList = mixinJson.jsonObject[config.environment.toString()]?.jsonArray ?: continue
            val mapped = mixinList.map {
                val mixin = it.jsonPrimitive.content
                val packagePath = pkg.replace('.', '/')
                val mixinFile = buildDir.resolve("classes/kotlin/$subDir/$packagePath/$mixin.class")
                if (!mixinFile.exists()) return@map it
                val generator = JavaGenerator(mixinFile)
                if (generator.isKotlinMixin) {
                    generator.doStuff()
                    return@map JsonPrimitive(generator.emittedName)
                } else {
                    return@map it
                }
            }.let(::JsonArray)
            val newMixins = mixinJson.jsonObject.toMutableMap()
            newMixins[config.environment.toString()] = mapped
            val newJson = Json.encodeToString(JsonObject(newMixins))
            file.writeText(newJson)
        }
    }

    private fun getMixins(): List<MixinConfig>? {
        val fabricModJson = buildDir.resolve("resources/main/fabric.mod.json")
        if (!fabricModJson.exists()) return null
        val modConfig = Json.parseToJsonElement(fabricModJson.readText())
        val mixinConfigs = modConfig.jsonObject["mixins"]?.jsonArray ?: return null
        val mixins = mutableListOf<MixinConfig>()
        for (config in mixinConfigs) {
            if (config is JsonPrimitive) {
                val path = config.content
                for (env in MixinConfig.Environment.values()) {
                    mixins.add(MixinConfig(path, env, true))
                }
            } else {
                val path = config.jsonObject["config"]?.jsonPrimitive?.content ?: continue
                val envString = config.jsonObject["environment"]?.jsonPrimitive?.content ?: continue
                if (envString == "*") {
                    for (env in MixinConfig.Environment.values()) {
                        mixins.add(MixinConfig(path, env, true))
                    }
                } else {
                    mixins.add(MixinConfig(path, MixinConfig.Environment.valueOf(envString.uppercase()), false))
                }
            }
        }
        return mixins
    }
}

