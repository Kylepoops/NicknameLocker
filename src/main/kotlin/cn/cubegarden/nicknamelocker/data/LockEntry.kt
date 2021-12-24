package cn.cubegarden.nicknamelocker.data

import cn.cubegarden.nicknamelocker.util.wrapAndPrint
import kotlin.properties.Delegates

data class LockEntry(val group: Long, val member: Long, var name: String) {

    companion object {
        fun builder(exceptionWrapper: (String, Throwable) -> Throwable) = LockEntryBuilder(exceptionWrapper)
    }

    class LockEntryBuilder(
        private val exceptionWrapper: (String, Throwable) -> Throwable
    ) {
        private var group by Delegates.notNull<Long>()
        private var member by Delegates.notNull<Long>()
        private lateinit var name: String

        fun withGroup(group: Long) = apply { this.group = group }

        fun withGroup(group: String): LockEntryBuilder {
            runCatching { this.group = group.toLong() }
                .wrapAndPrint("Failed to convert group to long", exceptionWrapper)
            return this
        }

        fun withMember(member: Long) = apply { this.member = member }

        fun withMember(member: String): LockEntryBuilder {
            runCatching { this.member = member.toLong() }
                .wrapAndPrint("Failed to convert member to long", exceptionWrapper)
            return this
        }

        fun withName(name: String) = apply { this.name = name }

        fun build() = runCatching { LockEntry(group, member, name) }.getOrNull()
    }
}
