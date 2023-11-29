package com.ctf.backend.util

fun String.containsAnyPath(vararg paths: String): Boolean {
    return paths.any { Regex("$it(\\W|\$)").containsMatchIn(this) }
}
