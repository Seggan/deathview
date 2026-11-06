package io.github.seggan.deathview.client.mixin;

import io.github.seggan.deathview.client.DeathSave;
import io.github.seggan.deathview.client.DeathViewClient;
import io.github.seggan.deathview.client.RenderHandlerKt;
import net.minecraft.network.chat.TextColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.client.gui.Font$PreparedTextBuilder")
public class PreparedTextBuilderMixin {

    @Inject(method = "getTextColor", at = @At("RETURN"), cancellable = true)
    private void deathview$modifyAlpha(TextColor textColor, CallbackInfoReturnable<Integer> cir) {
        if (!RenderHandlerKt.isOnDeathScreen()) return;
        DeathSave save = DeathViewClient.Companion.getDeath();
        if (save == null) return;

        int argb = cir.getReturnValue();
        int opacity = (int) (save.getOpacity() * 255.0F);
        cir.setReturnValue(opacity == 0 ? 0 : (argb & 0x00FFFFFF) | (opacity << 24));
    }
}
