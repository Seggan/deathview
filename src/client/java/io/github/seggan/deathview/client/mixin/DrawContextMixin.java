package io.github.seggan.deathview.client.mixin;

import io.github.seggan.deathview.client.RenderHandler;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(DrawContext.class)
public class DrawContextMixin {

    @ModifyVariable(method = "drawGuiTexture(Ljava/util/function/Function;Lnet/minecraft/util/Identifier;IIIII)V", at = @At("HEAD"), index = 7, argsOnly = true)
    private int modifyColor(int argb) {
        return RenderHandler.modifyAlpha(argb);
    }
}
