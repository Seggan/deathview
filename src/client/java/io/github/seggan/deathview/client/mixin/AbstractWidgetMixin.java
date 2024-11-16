package io.github.seggan.deathview.client.mixin;

import io.github.seggan.deathview.client.RenderHandler;
import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractWidget.class)
public abstract class AbstractWidgetMixin {

    @Inject(method = "render", at = @At("HEAD"))
    private void startRender(CallbackInfo ci) {
        RenderHandler.startRender((AbstractWidgetAccessor) this);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void endRender(CallbackInfo ci) {
        RenderHandler.endRender((AbstractWidgetAccessor) this);
    }
}
