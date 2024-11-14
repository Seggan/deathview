package io.github.seggan.deathview.client.mixin;

import io.github.seggan.deathview.client.PlayerDeathHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerMixin {

    @Inject(method = "onDeath", at = @At("TAIL"))
    private void onDeath(CallbackInfo ci) {
        PlayerDeathHandler.onDeath();
    }
}
