package io.github.seggan.deathview.client.mixin;

import io.github.seggan.deathview.client.DeathSave;
import io.github.seggan.deathview.client.DeathViewClient;
import io.github.seggan.deathview.client.RenderHandlerKt;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractWidget.class)
public abstract class AbstractWidgetMixin {

    @Unique
    private float deathview$oldAlpha = 1.0F;

    @Inject(method = "extractRenderState", at = @At("HEAD"))
    private void deathview$startRender(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (!RenderHandlerKt.isOnDeathScreen()) return;
        DeathSave save = DeathViewClient.Companion.getDeath();
        if (save == null) return;
        AbstractWidget widget = (AbstractWidget) (Object) this;
        this.deathview$oldAlpha = widget.getAlpha();
        widget.setAlpha(save.getOpacity());
    }

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void deathview$endRender(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (!RenderHandlerKt.isOnDeathScreen()) return;
        ((AbstractWidget) (Object) this).setAlpha(this.deathview$oldAlpha);
    }
}
