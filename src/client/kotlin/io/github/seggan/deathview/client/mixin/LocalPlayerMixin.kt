@file:JvmName("LocalPlayerMixin")
@file:Mixin(LocalPlayer::class)

package io.github.seggan.deathview.client.mixin

import io.github.seggan.deathview.client.DeathSave
import net.minecraft.client.Minecraft
import net.minecraft.client.player.LocalPlayer
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject

@Inject(method = ["respawn"], at = [At("TAIL")])
fun respawn() {
    val options = Minecraft.getInstance().options
    options.cameraType = DeathSave.lastCameraType
    options.textBackgroundOpacity().set(DeathSave.originalChatOpacity)
    DeathSave.recentDeath = false
}