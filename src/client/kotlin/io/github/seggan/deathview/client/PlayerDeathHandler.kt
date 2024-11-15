@file:JvmName("PlayerDeathHandler")

package io.github.seggan.deathview.client

import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.Perspective

var recentDeath = false
private var lastPerspective = Perspective.FIRST_PERSON
var originalChatOpacity = 0.0

fun onDeath() {
    recentDeath = true
    val options = MinecraftClient.getInstance().options
    lastPerspective = options.perspective
    options.perspective = Perspective.THIRD_PERSON_BACK
    originalChatOpacity = options.textBackgroundOpacity.value
}

fun onRespawn() {
    val options = MinecraftClient.getInstance().options
    options.perspective = lastPerspective
    options.textBackgroundOpacity.value = originalChatOpacity
}