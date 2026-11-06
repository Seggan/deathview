package io.github.seggan.deathview.client.mixin;

import net.minecraft.client.Camera;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Camera.class)
public class CameraMixin {

    @Redirect(
        method = "modifyFovBasedOnDeathOrFluid",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isDeadOrDying()Z")
    )
    private boolean deathview$cancelZoomOnDeath(LivingEntity entity) {
        return false;
    }
}
