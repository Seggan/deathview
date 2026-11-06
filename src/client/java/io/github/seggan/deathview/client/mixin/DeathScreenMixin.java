package io.github.seggan.deathview.client.mixin;

import io.github.seggan.deathview.client.DeathSave;
import io.github.seggan.deathview.client.DeathViewClient;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DeathScreen.class)
public class DeathScreenMixin {

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void deathview$tickDeathScreen(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        DeathSave save = DeathViewClient.Companion.getDeath();
        if (save == null) return;

        Minecraft client = Minecraft.getInstance();
        Screen currentScreen = client.gui.screen();
        if (!(currentScreen instanceof DeathScreen)) return;

        int ticksSinceDeath = ((DeathScreenAccessor) currentScreen).deathview$getTicksSinceDeath();
        save.setOpacity(ticksSinceDeath <= 30
            ? 0.0F
            : (float) Math.min(1.0D, Math.max(0.0D, (ticksSinceDeath - 30) / 40.0D)));
        client.options.textBackgroundOpacity().set(save.getOriginalChatOpacity() * save.getOpacity());
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void deathview$onDeath(Component message, boolean hardcore, LocalPlayer player, CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        double originalChatOpacity = client.options.textBackgroundOpacity().get();
        CameraType lastCameraType = client.options.getCameraType();
        DeathViewClient.Companion.setDeath(new DeathSave(lastCameraType, originalChatOpacity));

        if (client.player == null) {
            client.options.setCameraType(CameraType.THIRD_PERSON_BACK);
            return;
        }

        var headPos = client.player.getEyePosition(1.0F);
        var cameraDir = client.player.getViewVector(1.0F);
        var twoBlocksBack = headPos.subtract(cameraDir.scale(2.5D));
        var clipContext = new ClipContext(
            headPos,
            twoBlocksBack,
            ClipContext.Block.VISUAL,
            ClipContext.Fluid.NONE,
            client.player
        );
        var hit = client.player.level().clip(clipContext);
        client.options.setCameraType(hit.getType() == HitResult.Type.MISS
            ? CameraType.THIRD_PERSON_BACK
            : CameraType.THIRD_PERSON_FRONT);
    }
}
