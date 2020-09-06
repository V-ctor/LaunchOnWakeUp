package com.victorsmind.launchonwakeup

data class Settings(
    val autoStartApp: String?,
    val launcherApp: String?,
    val timeDelay: Int = DEFAULT_TIME_DELAY
) {
    companion object {
        const val DEFAULT_TIME_DELAY = 1000
    }
}