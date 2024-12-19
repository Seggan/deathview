package io.github.seggan.deathview.client

import io.github.seggan.deathview.client.config.DeathViewConfig
import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer
import net.fabricmc.api.ClientModInitializer

class DeathViewClient : ClientModInitializer {

    override fun onInitializeClient() {
        AutoConfig.register(DeathViewConfig::class.java, ::JanksonConfigSerializer)
        config = AutoConfig.getConfigHolder(DeathViewConfig::class.java).config
    }

    companion object {
        var death: DeathSave? = null

        lateinit var config: DeathViewConfig
            private set
    }
}
