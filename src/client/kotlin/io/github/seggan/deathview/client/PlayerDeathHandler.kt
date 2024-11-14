@file:JvmName("PlayerDeathHandler")

package io.github.seggan.deathview.client

import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.Perspective

private var lastPerspective = Perspective.FIRST_PERSON

fun onDeath() {
    val options = MinecraftClient.getInstance().options
    lastPerspective = options.perspective
    options.perspective = Perspective.THIRD_PERSON_BACK
    println("Player died!")
}

fun onRespawn() {
    val options = MinecraftClient.getInstance().options
    options.perspective = lastPerspective
    println("Player respawned!")
}