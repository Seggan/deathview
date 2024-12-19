package io.github.seggan.deathview.client.config

import me.shedaniel.autoconfig.ConfigData
import me.shedaniel.autoconfig.annotation.Config
import me.shedaniel.autoconfig.annotation.ConfigEntry
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment

@Config(name = "deathview")
class DeathViewConfig : ConfigData {

    @Comment("Death screen settings")
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    val screen = Screen()

    class Screen {
        @Comment("Whether or not to fade in the screen after death")
        val fade: Boolean = true

        @Comment("The delay before the screen starts to fade in")
        val fadeDelay: Int = 30

        @Comment("The duration of the screen fade in")
        val fadeDuration: Int = 40
    }

    @Comment("How far back to check for solid blocks before switching to the front third person view on death")
    val backCheck = 2.5
}