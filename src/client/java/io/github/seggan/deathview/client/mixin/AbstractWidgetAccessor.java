package io.github.seggan.deathview.client.mixin;

import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractWidget.class)
public interface AbstractWidgetAccessor {

    @Accessor
    void setAlpha(float alpha);

    @Accessor
    float getAlpha();
}
