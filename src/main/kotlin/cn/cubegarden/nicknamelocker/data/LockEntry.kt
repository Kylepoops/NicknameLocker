package cn.cubegarden.nicknamelocker.data

import cn.cubegarden.nicknamelocker.util.illegal
import kotlin.properties.Delegates

data class LockEntry(val group: Long, val member: Long, var name: String) {

    companion object {
        fun builder() = LockEntryBuilder()
    }

    class LockEntryBuilder {
        private var group by Delegates.notNull<Long>()
        private var member by Delegates.notNull<Long>()
        private lateinit var name: String

        fun withGroup(group: Long) = apply { this.group = group }

        fun withGroup(group: String): LockEntryBuilder {
            group.toLongOrNull()?.let { this.group = it } ?: illegal("group must be a number")
            return this
        }

        fun withMember(member: Long) = apply { this.member = member }

        fun withMember(member: String): LockEntryBuilder {
            member.toLongOrNull()?.let { this.member = it } ?: illegal("Failed to convert member to long")
            return this
        }

        fun withName(name: String) = apply { this.name = name }

        fun build() = runCatching { LockEntry(group, member, name) }.getOrNull()
    }
}
