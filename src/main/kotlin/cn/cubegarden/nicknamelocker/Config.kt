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

    lateinit var admins: List<Long>
        private set

    lateinit var groups: MutableList<Long>
        private set

    lateinit var locks: LockSet
        private set

    fun load() {
        if (::file.isInitialized) Main.plugin.reloadConfig()

        file = Main.plugin.config

        treatGroupAdminsAsPluginAdmins = file.getBoolean("treat-group-admins-as-plugin-admins")

        defaultNameCard = file.getString("default-name-card", "[请绑定玩家ID]")!!

        admins = file.getLongListException("plugin-admins") + injectBotOwners()

        groups = (file.getLongListException("groups") + injectBotGroups()).toMutableList()

        locks = file.getConfigurationSection("locks")?.let { LockManager.read(it) } ?: LockSet()
    }

    private fun injectBotOwners() =
        Bukkit.getPluginManager().getPlugin("AmazingBot")!!.config.getLongList("owners")

    private fun injectBotGroups() =
        Bukkit.getPluginManager().getPlugin("AmazingBot")!!.config.getConfigurationSection("groups")!!
            .getKeys(false)
            .mapNotNull { runCatching { it.toLong() }.getOrNull() }

    fun save() {
        file.set("groups", groups)
        LockManager.save(file, "locks", locks)
        Main.plugin.saveConfig()
    }
}
