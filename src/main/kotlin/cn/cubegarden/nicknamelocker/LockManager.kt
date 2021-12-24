package cn.cubegarden.nicknamelocker

import cn.cubegarden.nicknamelocker.data.LockEntry
import cn.cubegarden.nicknamelocker.data.LockSet
import cn.cubegarden.nicknamelocker.exception.InvalidConfigException
import cn.cubegarden.nicknamelocker.util.forEach
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration

object LockManager {

    operator fun get(group: Long, member: Long) = Config.locks[group, member]

    operator fun set(group: Long, member: Long, name: String) {
        if (Config.locks.contains(group, member)) return
        if (group !in Config.groups) Config.groups.add(group)
        Config.locks[group, member] = name
    }

    fun read(section: ConfigurationSection): LockSet {
        val result = LockSet()
        section.forEach { group, path ->
            section.getConfigurationSection(path)?.forEach { member, name ->
                runCatching { result.add(LockEntry(group.toLong(), member.toLong(), name)) }
                    .onFailure { InvalidConfigException("Invalid lock: $group - $path - $name", it).printStackTrace() }
            }
        }

        return result
    }

    fun save(target: FileConfiguration, path: String, locks: LockSet) {
        val section = target.createSection(path)
        locks.forEach {
            section.createSection(it.group.toString()).apply { set(it.member.toString(), it.name) }
        }
    }
}
