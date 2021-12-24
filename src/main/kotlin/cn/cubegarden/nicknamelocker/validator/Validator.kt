package cn.cubegarden.nicknamelocker.validator

sealed interface Validator {
    fun validate(name: String, target: String): Boolean

    fun repair(name: String, target: String): String

    object Literal : Validator {
        override fun validate(name: String, target: String) = name == target

        override fun repair(name: String, target: String) = target
    }

    object Prefix : Validator {
        override fun validate(name: String, target: String) = name.startsWith(target)

        override fun repair(name: String, target: String) = "$target | $name"
    }
}
