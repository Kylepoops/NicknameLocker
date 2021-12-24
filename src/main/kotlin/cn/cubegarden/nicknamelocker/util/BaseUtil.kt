package cn.cubegarden.nicknamelocker.util

import cn.cubegarden.nicknamelocker.Main
import cn.cubegarden.nicknamelocker.exception.InvalidConfigException
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration
import java.util.logging.Level

inline fun ConfigurationSection.forEach(block: (String, String) -> Unit) {
    for (key in this.getKeys(false)) {
        this.getString(key)?.let { block(key, it) }
    }
}

fun FileConfiguration.getLongListException(path: String): MutableList<Long> {
    val list = mutableListOf<Long>()
    this.getStringList(path).forEach { element ->
        runCatching { list.add(element.toLong()) }
            .wrapAndPrint("$path: $element is not a long value", ::InvalidConfigException)
    }
    return list
}

inline fun <T> Result<T>.wrapAndPrint(
    message: String,
    wrapper: (String, Throwable) -> Throwable = { _, throwable -> throwable }
) {
    this.onFailure {
        Main.logger.log(Level.WARNING, message, wrapper(message, it))
    }
}
