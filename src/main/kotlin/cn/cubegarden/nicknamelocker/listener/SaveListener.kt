package cn.cubegarden.nicknamelocker.listener

import cn.cubegarden.nicknamelocker.Config
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.WorldSaveEvent

class SaveListener : Listener {

    @Suppress("UnusedPrivateMember")
    @EventHandler
    fun onWorldSave(event: WorldSaveEvent) {
        Config.save()
    }
}
