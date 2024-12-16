@file:JvmName("AbstractWidgetMixin")
@file:Mixin(AbstractWidget::class)

package io.github.seggan.deathview.client.mixin

import io.github.seggan.deathview.client.DeathViewClient
import io.github.seggan.deathview.client.isOnDeathScreen
import net.minecraft.client.gui.components.AbstractWidget
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject

@Inject(method = ["render"], at = [At("HEAD")])
private fun AbstractWidgetAccessor.startRender() {
    if (!isOnDeathScreen) return
    val save = DeathViewClient.death!!
    save.oldAlpha = this.alpha
    this.alpha = save.opacity
}

@Inject(method = ["render"], at = [At("TAIL")])
private fun AbstractWidgetAccessor.endRender() {
    if (!isOnDeathScreen) return
    this.alpha = DeathViewClient.death!!.oldAlpha
}