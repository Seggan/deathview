package io.github.seggan.deathview.client.mixin;

import io.github.seggan.deathview.client.PlayerDeathHandler;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {

    @Inject(method = "die", at = @At("HEAD"))
    private void onDeath(CallbackInfo ci) {
        PlayerDeathHandler.onDeath();
    }
}
