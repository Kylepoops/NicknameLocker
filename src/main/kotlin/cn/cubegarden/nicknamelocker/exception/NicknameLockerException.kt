@file:Suppress("unused")

package cn.cubegarden.nicknamelocker.exception

import cn.cubegarden.nicknamelocker.Main
import java.util.logging.Level

internal sealed class NicknameLockerException : RuntimeException {

    constructor() : super()

    constructor(message: String) : super(message)

    constructor(message: String, cause: Throwable) : super(message, cause)
}

internal class InvalidConfigException : NicknameLockerException {

    constructor() : this("Unable to read the configuration")

    constructor(message: String) : super(message)

    constructor(message: String, cause: Throwable) : super(message, cause)

    companion object {

        fun print(message: String, level: Level = Level.WARNING) {
            Main.logger.log(level, message, InvalidConfigException(message))
        }

        fun print(message: String, cause: Throwable, level: Level = Level.WARNING) {
            Main.logger.log(level, message, InvalidConfigException(message, cause))
        }
    }
}
