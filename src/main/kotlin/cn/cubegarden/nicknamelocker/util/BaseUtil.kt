package cn.cubegarden.nicknamelocker.util

import cn.cubegarden.nicknamelocker.Config
import cn.cubegarden.nicknamelocker.Main
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.contact.isOperator
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration

val Member.isAdmin: Boolean
    get() {
        val pluginAdmin = id in Config.admins
        val groupAdmin = Config.treatGroupAdminsAsPluginAdmins && isOperator()

        return pluginAdmin or groupAdmin
    }

inline fun ConfigurationSection.forEach(block: (String, String) -> Unit) {
    for (key in this.getKeys(false)) {
        this.getString(key)?.let { block(key, it) }
    }
}

fun FileConfiguration.getLongListException(path: String): MutableList<Long> {
    val list = mutableListOf<Long>()
    this.getStringList(path).forEach { element ->
        element.toLongOrNull()?.let { list.add(it) } ?: illegal("$path: $element is not a long value")
    }
    return list
}

inline fun submit(crossinline block: () -> Unit) {
    Main.plugin.server.scheduler.runTask(Main.plugin, Runnable { block() })
}

fun illegal(message: String, throwing: Boolean = false) {
    val ex = IllegalArgumentException(message)

    if (throwing) {
        throw ex
    } else {
        Main.plugin.logger.severe(message, ex)
    }
}
