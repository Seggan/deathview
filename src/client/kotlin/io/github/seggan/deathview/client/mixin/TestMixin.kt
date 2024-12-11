@file:JvmName("TestMixin")
@file:Mixin(Minecart::class)

package io.github.seggan.deathview.client.mixin

import net.minecraft.world.entity.vehicle.Minecart
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Inject(method = ["tick"], at = [At("HEAD")])
fun test(ci: CallbackInfo) {
    println("Hello, world!a")
}