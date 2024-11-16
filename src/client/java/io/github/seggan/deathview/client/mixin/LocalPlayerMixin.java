package io.github.seggan.deathview.client.mixin;

import io.github.seggan.deathview.client.PlayerDeathHandler;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {

    @Inject(method = "respawn", at = @At("TAIL"))
    private void respawn(CallbackInfo ci) {
        PlayerDeathHandler.onRespawn();
    }
}
