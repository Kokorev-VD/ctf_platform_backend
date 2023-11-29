package com.ctf.backend.logger

import org.slf4j.LoggerFactory

class CTFLogger(cl: Class<*>?) {
    private val logger = LoggerFactory.getLogger(cl)

    fun error(msg: String?) {
        logger.error("\u001B[31m$msg\u001B[0m")
    }
}
