package io.github.seggan.uom

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer

class KMixinPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val generateUom = project.tasks.register("generateUom", TODO())
        val sourceSet = project.extensions.getByType(SourceSetContainer::class.java)
        sourceSet.getByName("main").java.srcDir(generateUom)
    }
}