package cn.cubegarden.nicknamelocker

import cn.cubegarden.nicknamelocker.listener.BotListener
import cn.cubegarden.nicknamelocker.listener.SaveListener
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger

@Suppress("RedundantCompanionReference")
class Main : JavaPlugin() {

    override fun onEnable() {
        PluginInstanceHolder.let {
            it.plugin = this
            it.logger = this.logger
        }
        this.saveDefaultConfig()
        Config.load()
        this.server.pluginManager.apply {
            registerEvents(BotListener(), plugin)
            registerEvents(SaveListener(), plugin)
        }
    }

    override fun onDisable() {
        HandlerList.unregisterAll(this)
        Config.save()
    }

    companion object PluginInstanceHolder {
        lateinit var plugin: JavaPlugin
            private set
        lateinit var logger: Logger
            private set
    }
}
