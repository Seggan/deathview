package io.github.seggan.deathview.client.mixin

import net.minecraft.client.gui.components.AbstractWidget
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.gen.Accessor

@Mixin(AbstractWidget::class)
interface AbstractWidgetAccessor {

    @get:Accessor
    @set:Accessor
    var alpha: Float
}