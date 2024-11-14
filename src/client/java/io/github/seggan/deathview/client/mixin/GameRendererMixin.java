package io.github.seggan.deathview.client.mixin;

import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(
            method = "tiltViewWhenHurt",
            at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;deathTime:I", shift = At.Shift.BEFORE),
            cancellable = true
    )
    private void cancelOnDeath(CallbackInfo ci) {
        ci.cancel();
    }
}
