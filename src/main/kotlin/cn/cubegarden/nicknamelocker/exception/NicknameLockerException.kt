@file:Suppress("unused")

package cn.cubegarden.nicknamelocker.exception

internal sealed class NicknameLockerException : RuntimeException {

    constructor() : super()

    constructor(message: String) : super(message)

    constructor(message: String, cause: Throwable) : super(message, cause)
}

internal class InvalidConfigException : NicknameLockerException {

    constructor() : this("Unable to read the configuration")

    constructor(message: String) : super(message)

    constructor(message: String, cause: Throwable) : super(message, cause)
}
