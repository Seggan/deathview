package io.github.seggan.deathview.client.mixin;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(
            method = "bobHurt",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/entity/LivingEntity;deathTime:I",
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    private void cancelOnDeath(CallbackInfo ci) {
        ci.cancel();
    }

    @Redirect(
            method = "getFov",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isDeadOrDying()Z")
    )
    private boolean cancelFovOnDeath(LivingEntity instance) {
        return false;
    }
}
