package cn.cubegarden.nicknamelocker.util

import cn.cubegarden.nicknamelocker.exception.InvalidConfigException
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration

inline fun ConfigurationSection.forEach(block: (String, String) -> Unit) {
    for (key in this.getKeys(false)) {
        this.getString(key)?.let { block(key, it) }
    }
}

fun FileConfiguration.getLongListException(path: String): MutableList<Long> {
    val list = mutableListOf<Long>()
    this.getStringList(path).forEach { element ->
        runCatching { list.add(element.toLong()) }
            .onFailure { InvalidConfigException("$path: $element is not a long value", it).printStackTrace() }
    }
    return list
}
