package io.github.seggan.deathview.client

import net.minecraft.client.CameraType

data class DeathSave(val lastCameraType: CameraType, val originalChatOpacity: Double) {
    var opacity = 0f
}