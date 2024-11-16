package io.github.seggan.deathview.client.mixin;

import io.github.seggan.deathview.client.RenderHandler;
import net.minecraft.client.gui.screens.DeathScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DeathScreen.class)
public class DeathScreenMixin {

    @Inject(method = "render", at = @At("TAIL"))
    private void tickDeathScreen(CallbackInfo ci) {
        RenderHandler.tickChat();
    }
}
