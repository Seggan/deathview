package io.github.seggan.deathview.client.mixin;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
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

    @Redirect(method = "getFov", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isDead()Z"))
    private boolean cancelFovOnDeath(LivingEntity instance) {
        return false;
    }
}
