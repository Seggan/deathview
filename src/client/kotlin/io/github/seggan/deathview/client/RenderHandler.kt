@file:JvmName("RenderHandler")

package io.github.seggan.deathview.client

import io.github.seggan.deathview.client.DeathSave.oldAlpha
import io.github.seggan.deathview.client.DeathSave.opacity
import io.github.seggan.deathview.client.DeathSave.recentDeath
import io.github.seggan.deathview.client.mixin.AbstractWidgetAccessor
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.DeathScreen

fun AbstractWidgetAccessor.startRender() {
    if (!isOnDeathScreen) return
    oldAlpha = this.alpha
    this.alpha = opacity
}

fun AbstractWidgetAccessor.endRender() {
    if (!isOnDeathScreen) return
    this.alpha = oldAlpha
}

fun modifyAlpha(argb: Int): Int {
    if (!isOnDeathScreen) return argb
    val opacity = (opacity * 255).toInt()
    if (opacity == 0) return 0
    return argb and 0x00FFFFFF or (opacity shl 24)
}

fun IntRange.percentage(value: Int): Double {
    return (value - first) / (last - first).toDouble()
}

private val isOnDeathScreen: Boolean
    get() = recentDeath && Minecraft.getInstance().screen is DeathScreen