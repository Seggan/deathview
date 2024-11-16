package io.github.seggan.deathview.client.mixin;

import io.github.seggan.deathview.client.RenderHandler;
import net.minecraft.client.gui.Font;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Font.class)
public class FontMixin {

    @Inject(method = "adjustColor", at = @At("RETURN"), cancellable = true)
    private static void adjustColor(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(RenderHandler.modifyAlpha(cir.getReturnValue()));
    }
}
