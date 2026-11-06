package io.github.seggan.deathview.client.mixin;

import io.github.seggan.deathview.client.DeathSave;
import io.github.seggan.deathview.client.DeathViewClient;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {

    @Inject(method = "respawn", at = @At("TAIL"))
    private void deathview$respawn(CallbackInfo ci) {
        var options = Minecraft.getInstance().options;
        DeathSave save = DeathViewClient.Companion.getDeath();
        options.setCameraType(save == null ? CameraType.FIRST_PERSON : save.getLastCameraType());
        options.textBackgroundOpacity().set(save == null ? 0.5D : save.getOriginalChatOpacity());
        DeathViewClient.Companion.setDeath(null);
    }
}
