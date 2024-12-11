package io.github.seggan.kmixin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.get

class KMixinPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val jarTask = project.tasks["jar"] ?: return
        val allDependTasks = listOf(
            "classes",
            "compileClientKotlin",
            "compileServerKotlin",
            "processClientResources",
            "processServerResources"
        )
        val thisTask = project.tasks.register("generateJavaMixinWrappers", GenerationTask::class.java)
        /// whyy do you have to be like this
        project.afterEvaluate {
            thisTask.configure {
                for (task in allDependTasks) {
                    dependsOn(project.tasks.findByName(task) ?: continue)
                }
            }
        }
        jarTask.dependsOn(thisTask)
    }
}