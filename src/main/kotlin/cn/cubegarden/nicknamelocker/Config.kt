package cn.cubegarden.nicknamelocker

import cn.cubegarden.nicknamelocker.data.LockSet
import cn.cubegarden.nicknamelocker.util.getLongListException
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

        admins = (file.getLongListException("plugin-admins") + botOwners).toSet()

        originalGroups = file.getLongListException("groups").toSet()

        groups = (originalGroups + botGroups).toMutableSet()

        locks = file.getConfigurationSection("locks")?.let { LockManager.read(it) } ?: LockSet()
    }

    fun save() {
        file.set("groups", groupDiffs())
        LockManager.save(file, "locks", locks)
        Main.plugin.saveConfig()
    }

    private fun groupDiffs() =
        groups.filter { it !in originalGroups && it !in botGroups }.union(originalGroups).toSet()
}
