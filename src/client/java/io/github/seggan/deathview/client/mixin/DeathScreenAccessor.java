package io.github.seggan.deathview.client.mixin;

import net.minecraft.client.gui.screens.DeathScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DeathScreen.class)
public interface DeathScreenAccessor {

    @Accessor("delayTicker")
    int getTicksSinceDeath();
}
