package com.mdgd.zombot.models.logger

const val TAG_LOGGER = "LOGG"

interface Logger {
    fun log(message: String)

    fun log(error: LogException?)
}
