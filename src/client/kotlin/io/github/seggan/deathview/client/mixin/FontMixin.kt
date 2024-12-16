@file:JvmName("FontMixin")
@file:Mixin(Font::class)

package io.github.seggan.deathview.client.mixin

import io.github.seggan.deathview.client.DeathViewClient
import io.github.seggan.deathview.client.isOnDeathScreen
import net.minecraft.client.gui.Font
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject

@Inject(method = ["adjustColor"], at = [At("RETURN")], cancellable = true)
private fun modifyAlpha(argb: Int): Int {
    if (!isOnDeathScreen) return argb
    val opacity = (DeathViewClient.death!!.opacity * 255).toInt()
    if (opacity == 0) return 0
    return argb and 0x00FFFFFF or (opacity shl 24)
}