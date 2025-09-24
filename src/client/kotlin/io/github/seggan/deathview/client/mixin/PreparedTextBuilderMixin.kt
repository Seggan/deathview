@file:Mixin(targets = [$$"net.minecraft.client.gui.Font$PreparedTextBuilder"])
package io.github.seggan.deathview.client.mixin

import io.github.seggan.deathview.client.DeathViewClient
import io.github.seggan.deathview.client.isOnDeathScreen
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject

@Inject(method = ["getTextColor"], at = [At("RETURN")], cancellable = true)
internal fun modifyAlpha(argb: Int): Int {
    if (!isOnDeathScreen) return argb
    val opacity = (DeathViewClient.death!!.opacity * 255).toInt()
    if (opacity == 0) return 0
    return argb and 0x00FFFFFF or (opacity shl 24)
}
