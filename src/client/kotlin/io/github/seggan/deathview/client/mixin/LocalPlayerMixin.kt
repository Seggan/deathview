@file:JvmName("LocalPlayerMixin")
@file:Mixin(LocalPlayer::class)

package io.github.seggan.deathview.client.mixin

import io.github.seggan.deathview.client.onRespawn
import net.minecraft.client.player.LocalPlayer
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Inject(method = ["respawn"], at = [At("TAIL")])
fun respawn(ci: CallbackInfo) {
    onRespawn()
}