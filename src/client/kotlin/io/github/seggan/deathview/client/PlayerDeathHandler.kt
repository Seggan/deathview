@file:JvmName("PlayerDeathHandler")

package io.github.seggan.deathview.client

import net.minecraft.client.CameraType
import net.minecraft.client.Minecraft
import net.minecraft.world.level.ClipContext
import net.minecraft.world.phys.HitResult

var recentDeath = false
private var lastCameraType = CameraType.FIRST_PERSON
var originalChatOpacity = 0.0

fun onDeath() {
    recentDeath = true
    val client = Minecraft.getInstance()
    val options = client.options
    originalChatOpacity = options.textBackgroundOpacity().get()

    lastCameraType = options.cameraType
    val player = client.player
    if (player == null) {
        options.cameraType = CameraType.THIRD_PERSON_BACK
    } else {
        val headPos = player.getEyePosition(1f)
        val cameraDir = player.getViewVector(1f)
        val twoBlocksBack = headPos.subtract(cameraDir.scale(2.5))
        val clipContext = ClipContext(
            headPos,
            twoBlocksBack,
            ClipContext.Block.VISUAL,
            ClipContext.Fluid.NONE,
            player
        )
        val hit = player.level().clip(clipContext)
        if (hit.type == HitResult.Type.MISS) {
            options.cameraType = CameraType.THIRD_PERSON_BACK
        } else {
            options.cameraType = CameraType.THIRD_PERSON_FRONT
        }
    }
}

fun onRespawn() {
    val options = Minecraft.getInstance().options
    options.cameraType = lastCameraType
    options.textBackgroundOpacity().set(originalChatOpacity)
    recentDeath = false
}