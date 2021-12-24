package cn.cubegarden.nicknamelocker

import cn.cubegarden.nicknamelocker.data.LockSet
import cn.cubegarden.nicknamelocker.exception.InvalidConfigException
import cn.cubegarden.nicknamelocker.util.getLongListException
import cn.cubegarden.nicknamelocker.validator.Validator
import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import kotlin.properties.Delegates

@Suppress("unused")
object Config {

    private lateinit var file: FileConfiguration

    var treatGroupAdminsAsPluginAdmins by Delegates.notNull<Boolean>()
        private set

    lateinit var defaultNameCard: String
        private set

    lateinit var validator: Validator
        private set

    lateinit var admins: Set<Long>
        private set

    private lateinit var originalGroups: Set<Long>

    lateinit var groups: MutableSet<Long>
        private set

    lateinit var locks: LockSet
        private set

    private val botOwners by lazy {
        Bukkit.getPluginManager().getPlugin("AmazingBot")!!.config.getLongList("owners").toSet()
    }

    private val botGroups by lazy {
        Bukkit.getPluginManager().getPlugin("AmazingBot")!!.config.getConfigurationSection("groups")!!
            .getKeys(false)
            .mapNotNull { runCatching { it.toLong() }.getOrNull() }
            .toSet()
    }

    fun load() {
        if (::file.isInitialized) Main.plugin.reloadConfig()

        file = Main.plugin.config

        treatGroupAdminsAsPluginAdmins = file.getBoolean("treat-group-admins-as-plugin-admins")

        defaultNameCard = file.getString("default-name-card", "[请绑定玩家ID]")!!

        validator = when (file.getString("validator", "literal")?.lowercase()) {
            "literal" -> Validator.Literal
            "prefix" -> Validator.Prefix
            else -> {
                InvalidConfigException.print("Invalid validator type: ${file.getString("validator")}")
                Main.logger.warning("Validator type has automatically been set to literal")
                Validator.Literal
            }
        }

        admins = file.getLongListException("plugin-admins").union(botOwners)

        originalGroups = file.getLongListException("groups").toSet()

        groups = originalGroups.union(botGroups).toMutableSet()

        locks = file.getConfigurationSection("locks")?.let { LockManager.read(it) } ?: LockSet()
    }

    fun save() {
        file.set("groups", groupDiffs())
        LockManager.save(file, "locks", locks)
        Main.plugin.saveConfig()
    }

    private fun groupDiffs() =
        groups.filter { it !in originalGroups && it !in botGroups }.union(originalGroups)
}
