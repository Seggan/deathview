@file:JvmName("DeathScreenMixin")
@file:Mixin(DeathScreen::class)

package io.github.seggan.deathview.client.mixin

import io.github.seggan.deathview.client.DeathSave
import io.github.seggan.deathview.client.DeathViewClient
import io.github.seggan.deathview.client.percentage
import net.minecraft.client.CameraType
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.DeathScreen
import net.minecraft.world.level.ClipContext
import net.minecraft.world.phys.HitResult
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject

@Inject(method = ["render"], at = [At("TAIL")])
private fun tickDeathScreen() {
    val save = DeathViewClient.death ?: return
    val client = Minecraft.getInstance()
    val currentScreen = client.screen
    if (currentScreen !is DeathScreen) return
    val accessor = currentScreen as DeathScreenAccessor
    if (!DeathViewClient.config.screen.fade) {
        save.opacity = 1f
    } else {
        val fadeDelay = DeathViewClient.config.screen.fadeDelay
        val fadeDuration = DeathViewClient.config.screen.fadeDuration
        save.opacity = if (accessor.ticksSinceDeath <= fadeDelay) {
            0f
        } else {
            (fadeDelay..(fadeDelay + fadeDuration)).percentage(accessor.ticksSinceDeath).coerceIn(0.0, 1.0).toFloat()
        }
    }
    client.options.textBackgroundOpacity().set(save.originalChatOpacity * save.opacity)
}

@Inject(method = ["<init>"], at = [At("RETURN")])
private fun onDeath() {
    val client = Minecraft.getInstance()
    val options = client.options
    val originalChatOpacity = options.textBackgroundOpacity().get()
    val lastCameraType = options.cameraType
    DeathViewClient.death = DeathSave(lastCameraType, originalChatOpacity)
    val player = client.player
    if (player == null) {
        options.cameraType = CameraType.THIRD_PERSON_BACK
    } else {
        val headPos = player.getEyePosition(1f)
        val cameraDir = player.getViewVector(1f)
        val twoBlocksBack = headPos.subtract(cameraDir.scale(DeathViewClient.config.backCheck))
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