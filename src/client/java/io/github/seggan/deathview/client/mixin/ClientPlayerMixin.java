package io.github.seggan.deathview.client.mixin;

import io.github.seggan.deathview.client.PlayerDeathHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerMixin {

    @Inject(method = "requestRespawn", at = @At("TAIL"))
    private void requestRespawn(CallbackInfo ci) {
        PlayerDeathHandler.onRespawn();
    }
}
