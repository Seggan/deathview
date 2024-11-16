package io.github.seggan.deathview.client.mixin;

import io.github.seggan.deathview.client.RenderHandler;
import net.minecraft.client.gui.widget.ClickableWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClickableWidget.class)
public abstract class ClickableWidgetMixin {

    @Inject(method = "render", at = @At("HEAD"))
    private void startRender(CallbackInfo ci) {
        RenderHandler.startRender((ClickableWidgetAccessor) this);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void endRender(CallbackInfo ci) {
        RenderHandler.endRender((ClickableWidgetAccessor) this);
    }
}
