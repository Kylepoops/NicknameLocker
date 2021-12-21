package cn.cubegarden.nicknamelocker.data

@Suppress("unused")
class LockSet private constructor(private val delegate: MutableSet<LockEntry>) : MutableSet<LockEntry> by delegate {

    constructor() : this(mutableSetOf())

    fun contains(group: Long, member: Long) = delegate.any { it.group == group && it.member == member }

    fun remove(group: Long, member: Long) = delegate.removeIf { it.group == group && it.member == member }

    operator fun get(group: Long, member: Long) =
        delegate.firstOrNull { it.group == group && it.member == member }?.name

    operator fun set(group: Long, member: Long, name: String) {
        delegate.firstOrNull { it.group == group && it.member == member }?.apply { this.name = name }
            ?: delegate.add(LockEntry(group, member, name))
    }
}
