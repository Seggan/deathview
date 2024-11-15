@file:JvmName("PlayerDeathHandler")

package io.github.seggan.deathview.client

import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.Perspective
import net.minecraft.util.hit.HitResult
import net.minecraft.world.RaycastContext

var recentDeath = false
private var lastPerspective = Perspective.FIRST_PERSON
var originalChatOpacity = 0.0

fun onDeath() {
    recentDeath = true
    val client = MinecraftClient.getInstance()
    val options = client.options
    originalChatOpacity = options.textBackgroundOpacity.value

    lastPerspective = options.perspective
    val player = client.player
    if (player == null) {
        options.perspective = Perspective.THIRD_PERSON_BACK
    } else {
        val headPos = player.getCameraPosVec(1f)
        val cameraDir = player.getRotationVec(1f)
        val twoBlocksBack = headPos.subtract(cameraDir.multiply(2.0))
        val raycastContext = RaycastContext(
            headPos,
            twoBlocksBack,
            RaycastContext.ShapeType.OUTLINE,
            RaycastContext.FluidHandling.NONE,
            player
        )
        val hit = player.world.raycast(raycastContext)
        if (hit != null && hit.type == HitResult.Type.MISS) {
            options.perspective = Perspective.THIRD_PERSON_BACK
        } else {
            options.perspective = Perspective.THIRD_PERSON_FRONT
        }
    }
}

fun onRespawn() {
    val options = MinecraftClient.getInstance().options
    options.perspective = lastPerspective
    options.textBackgroundOpacity.value = originalChatOpacity
    recentDeath = false
}