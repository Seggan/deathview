@file:JvmName("LocalPlayerMixin")
@file:Mixin(LocalPlayer::class)

package io.github.seggan.deathview.client.mixin

import io.github.seggan.deathview.client.DeathViewClient
import net.minecraft.client.CameraType
import net.minecraft.client.Minecraft
import net.minecraft.client.player.LocalPlayer
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject

@Inject(method = ["respawn"], at = [At("TAIL")])
private fun respawn() {
    val options = Minecraft.getInstance().options
    val save = DeathViewClient.death
    options.cameraType = save?.lastCameraType ?: CameraType.FIRST_PERSON
    options.textBackgroundOpacity().set(save?.originalChatOpacity ?: .5)
    DeathViewClient.death = null
}