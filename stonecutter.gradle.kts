plugins {
    id("dev.kikugie.stonecutter")
    id("fabric-loom") version "1.8.11" apply false
}
stonecutter active "1.20.6" /* [SC] DO NOT EDIT */

stonecutter registerChiseled tasks.register("chiseledBuild", stonecutter.chiseled) { 
    group = "project"
    ofTask("buildAndCollect")
}

stonecutter configureEach {
    swap("mod_version", "\"${property("mod.version")}\";")
    const("release", property("mod.id") != "template")
    dependency("fapi", project.property("deps.fabric_api").toString())
}