@file:JvmName("RenderHandler")

package io.github.seggan.deathview.client

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.DeathScreen

fun modifyAlpha(argb: Int): Int {
    if (!isOnDeathScreen) return argb
    val opacity = (DeathViewClient.death!!.opacity * 255).toInt()
    if (opacity == 0) return 0
    return argb and 0x00FFFFFF or (opacity shl 24)
}

fun IntRange.percentage(value: Int): Double {
    return (value - first) / (last - first).toDouble()
}

val isOnDeathScreen: Boolean
    get() = DeathViewClient.death != null && Minecraft.getInstance().screen is DeathScreen