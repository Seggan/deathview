@file:JvmName("GameRendererMixin")
@file:Mixin(GameRenderer::class)
@file:Suppress("NOTHING_TO_INLINE")

package io.github.seggan.deathview.client.mixin

import net.minecraft.client.renderer.GameRenderer
import net.minecraft.world.entity.LivingEntity
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.Redirect
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Inject(
    method = ["bobHurt"],
    at = [At(
        value = "FIELD",
        target = "Lnet/minecraft/world/entity/LivingEntity;deathTime:I",
        shift = At.Shift.BEFORE
    )],
    cancellable = true
)
private inline fun cancelDeathTilt(ci: CallbackInfo) {
    ci.cancel()
}

@Redirect(
    method = ["getFov"],
    at = At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isDeadOrDying()Z")
)
@Suppress("unused")
private fun cancelZoomOnDeath(entity: LivingEntity): Boolean {
    return false
}