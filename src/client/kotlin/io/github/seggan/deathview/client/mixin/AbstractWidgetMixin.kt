@file:Mixin(AbstractWidget::class)

package io.github.seggan.deathview.client.mixin

import io.github.seggan.deathview.client.DeathViewClient
import io.github.seggan.deathview.client.isOnDeathScreen
import net.minecraft.client.gui.components.AbstractWidget
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject

private var oldAlpha = 1f

@Inject(method = ["render"], at = [At("HEAD")])
internal fun AbstractWidgetAccessor.startRender() {
    if (!isOnDeathScreen) return
    val save = DeathViewClient.death!!
    oldAlpha = this.alpha
    this.alpha = save.opacity
}

@Inject(method = ["render"], at = [At("TAIL")])
internal fun AbstractWidgetAccessor.endRender() {
    if (!isOnDeathScreen) return
    this.alpha = oldAlpha
}