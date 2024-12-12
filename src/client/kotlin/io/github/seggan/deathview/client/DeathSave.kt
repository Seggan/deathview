package io.github.seggan.deathview.client

import net.minecraft.client.CameraType

object DeathSave {
    var recentDeath = false
    var opacity = 0f

    var oldAlpha = 0f
    var lastCameraType = CameraType.FIRST_PERSON
    var originalChatOpacity = 0.0
}