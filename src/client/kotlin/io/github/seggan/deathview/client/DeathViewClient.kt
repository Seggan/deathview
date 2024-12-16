package io.github.seggan.deathview.client

import net.fabricmc.api.ClientModInitializer

class DeathViewClient : ClientModInitializer {

    override fun onInitializeClient() {
    }

    companion object {
        var death: DeathSave? = null
    }
}
