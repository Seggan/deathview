@file:JvmName("RenderHandler")

package io.github.seggan.deathview.client

import io.github.seggan.deathview.client.mixin.ClickableWidgetAccessor
import io.github.seggan.deathview.client.mixin.DeathScreenAccessor
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.DeathScreen

private var opacity = 0f

private var oldAlpha = 0f

fun ClickableWidgetAccessor.startRender() {
    if (!isOnDeathScreen) return
    oldAlpha = this.alpha
    this.alpha = opacity
}

fun ClickableWidgetAccessor.endRender() {
    if (!isOnDeathScreen) return
    this.alpha = oldAlpha
}

fun tickChat() {
    if (!recentDeath) return
    val client = MinecraftClient.getInstance()
    val currentScreen = client.currentScreen
    if (currentScreen !is DeathScreen) return
    val accessor = currentScreen as DeathScreenAccessor
    opacity = if (accessor.ticksSinceDeath <= 30) {
        0f
    } else {
        (30..70).percentage(accessor.ticksSinceDeath).coerceIn(0.0, 1.0).toFloat()
    }
    client.options.textBackgroundOpacity.value = originalChatOpacity * opacity
}

fun modifyAlpha(argb: Int): Int {
    if (!isOnDeathScreen) return argb
    val opacity = (opacity * 255).toInt()
    if (opacity == 0) return 0
    return argb and 0x00FFFFFF or (opacity shl 24)
}

private fun IntRange.percentage(value: Int): Double {
    return (value - first) / (last - first).toDouble()
}

private val isOnDeathScreen: Boolean
    get() = recentDeath && MinecraftClient.getInstance().currentScreen is DeathScreen