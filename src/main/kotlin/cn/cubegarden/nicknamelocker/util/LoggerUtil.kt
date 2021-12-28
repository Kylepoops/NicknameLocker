package cn.cubegarden.nicknamelocker.util

import java.util.logging.Level
import java.util.logging.Logger

fun Logger.severe(msg: String, ex: Throwable) {
    this.log(Level.SEVERE, msg, ex)
}
