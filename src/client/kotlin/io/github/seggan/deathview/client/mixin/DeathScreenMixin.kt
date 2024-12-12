@file:JvmName("DeathScreenMixin")
@file:Mixin(DeathScreen::class)

package io.github.seggan.deathview.client.mixin

import io.github.seggan.deathview.client.DeathSave
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
fun tickDeathScreen() {
    if (!DeathSave.recentDeath) return
    val client = Minecraft.getInstance()
    val currentScreen = client.screen
    if (currentScreen !is DeathScreen) return
    val accessor = currentScreen as DeathScreenAccessor
    DeathSave.opacity = if (accessor.ticksSinceDeath <= 30) {
        0f
    } else {
        (30..70).percentage(accessor.ticksSinceDeath).coerceIn(0.0, 1.0).toFloat()
    }
    client.options.textBackgroundOpacity().set(DeathSave.originalChatOpacity * DeathSave.opacity)
}

@Inject(method = ["<init>"], at = [At("RETURN")])
fun onDeath() {
    DeathSave.recentDeath = true
    val client = Minecraft.getInstance()
    val options = client.options
    DeathSave.originalChatOpacity = options.textBackgroundOpacity().get()
    DeathSave.lastCameraType = options.cameraType
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