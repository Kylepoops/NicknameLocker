package cn.cubegarden.nicknamelocker.listener

import cn.cubegarden.nicknamelocker.Config
import cn.cubegarden.nicknamelocker.LockManager
import cn.cubegarden.nicknamelocker.util.isAdmin
import me.albert.amazingbot.bot.Bot
import me.albert.amazingbot.events.GroupMessageEvent
import net.mamoe.mirai.contact.*
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class BotListener : Listener {

    private val botApi = Bot.getApi()!!
    private val bot = botApi.bot!!

    private val errorMsg = """无法获取群成员:
                          |群号: %s
                          |QQ号: %s
                           """.trimMargin()

    @EventHandler
    fun onMessageReceived(event: GroupMessageEvent) {
        val group = bot.getGroup(event.groupID)
        val member = group?.getMember(event.userID)

        if (group == null || member == null) {
            event.response(errorMsg.format(group?.id, event.userID))
            return
        }

        if (member.isAdmin && handleCommand(event)) {
            return
        }

        if (event.groupID in Config.groups) {
            changeNameCard(member)
        }
    }

    @Suppress("MagicNumber", "ReturnCount")
    private fun handleCommand(event: GroupMessageEvent): Boolean {
        val originalArgs = event.msg.split(" ")
        if (!originalArgs[1].equals("nnl", ignoreCase = true)) return false

        val args = originalArgs.drop(1)
        val group = bot.getGroup(event.groupID)!!

        if (args.size == 3 && args[0] == ("锁定名片")) {
            args[1].toLongOrNull()
                ?.let {
                    LockManager[event.groupID, it] = args[2]
                    event.response("已绑定 $it 的名片至 ${args[2]}")
                }
                ?: event.response("参数不合法: ${args[1]}")
            return true
        } else if (args.size == 1 && args[0] == ("reload")) {
            event.response("重载配置文件...")
            Config.load()
            event.response("配置文件已重载")
            return true
        } else if (args.size == 1 && args[0] == "检测名片") {
            val frequency = changeAllNameCard(group)
            event.response(if (frequency != 0) "已修正${frequency}个名片" else "未检测到不合规名片")
            return true
        }

        return false
    }

    private fun changeAllNameCard(group: Group) =
        group.members.map { changeNameCard(it) }.count { it /* == true */ }

    private fun changeNameCard(member: NormalMember): Boolean {
        val nameCard = Config.locks[member.group.id, member.id]
            ?: botApi.getPlayer(member.id)?.let { Bukkit.getOfflinePlayer(it).name }
            ?: Config.defaultNameCard

        val needRepair = !Config.validator.validate(member.nameCard, nameCard)
        if (needRepair) member.nameCard = Config.validator.repair(member.nameCard, nameCard)

        return needRepair
    }
}
