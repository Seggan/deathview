@file:JvmName("LocalPlayerMixin")
@file:Mixin(LocalPlayer::class)

package io.github.seggan.deathview.client.mixin

import io.github.seggan.deathview.client.lastCameraType
import io.github.seggan.deathview.client.originalChatOpacity
import io.github.seggan.deathview.client.recentDeath
import net.minecraft.client.Minecraft
import net.minecraft.client.player.LocalPlayer
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject

@Inject(method = ["respawn"], at = [At("TAIL")])
fun respawn() {
    val options = Minecraft.getInstance().options
    options.cameraType = lastCameraType
    options.textBackgroundOpacity().set(originalChatOpacity)
    recentDeath = false
}