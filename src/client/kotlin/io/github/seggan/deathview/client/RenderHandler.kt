package io.github.seggan.deathview.client

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.DeathScreen

fun IntRange.percentage(value: Int): Double {
    return (value - first) / (last - first).toDouble()
}

val isOnDeathScreen: Boolean
    get() = DeathViewClient.death != null && Minecraft.getInstance().screen is DeathScreen