package cn.cubegarden.nicknamelocker.listener

import cn.cubegarden.nicknamelocker.Config
import cn.cubegarden.nicknamelocker.LockManager
import me.albert.amazingbot.bot.Bot
import me.albert.amazingbot.events.GroupMessageEvent
import net.mamoe.mirai.contact.*
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class BotListener : Listener {

    private val botApi = Bot.getApi()!!
    private val bot = botApi.bot!!

    @EventHandler
    fun onMessageReceived(event: GroupMessageEvent) {
        val group = bot.getGroup(event.groupID)
        val member = group?.getMember(event.userID)

        if (group == null || member == null) {
            event.response(
                """无法获取群成员:
                  |群: $group
                  |群号: ${event.groupID}
                  |成员: $member
                  |QQ号: ${event.userID}
                """.trimMargin()
            )
            return
        }

        val groupAdmin = Config.treatGroupAdminsAsPluginAdmins && member.isOperator()
        if (event.userID in Config.admins || groupAdmin) {
            @Suppress("CollapsibleIfStatements")
            if (handleCommand(event)) return
        }

        if (event.groupID in Config.groups) {
            changeNameCard(member)
        }
    }

    @Suppress("MagicNumber", "ReturnCount")
    private fun handleCommand(event: GroupMessageEvent): Boolean {
        val group = bot.getGroup(event.groupID)!!
        val args = event.msg.split(" ")
        if (args.size == 3 && args[0] == ("锁定名片")) {
            runCatching { args[1].toLong() }
                .onSuccess {
                    LockManager[event.groupID, it] = args[2]
                    event.response("已绑定 $it 的名片至 ${args[2]}")
                }
                .onFailure { event.response("参数不合法: ${args[1]}") }
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

    @Suppress("SimplifyBooleanWithConstants")
    private fun changeAllNameCard(group: Group) =
        group.members.map { changeNameCard(it) }.count { it == true }

    private fun changeNameCard(member: NormalMember): Boolean {
        val nameCard = Config.locks[member.group.id, member.id]
            ?: botApi.getPlayer(member.id)?.let { Bukkit.getOfflinePlayer(it).name }
            ?: Config.defaultNameCard

        val needRepair = !Config.validator.validate(member.nameCard, nameCard)
        if (needRepair) member.nameCard = Config.validator.repair(member.nameCard, nameCard)

        return needRepair
    }
}
