package io.github.seggan.deathview.client.mixin;

import io.github.seggan.deathview.client.RenderHandler;
import net.minecraft.client.font.TextRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TextRenderer.class)
public class TextRendererMixin {

    @Inject(method = "tweakTransparency", at = @At("RETURN"), cancellable = true)
    private static void tweakTransparency(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(RenderHandler.modifyAlpha(cir.getReturnValue()));
    }
}
