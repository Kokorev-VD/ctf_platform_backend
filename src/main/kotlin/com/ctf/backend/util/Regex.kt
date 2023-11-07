package com.ctf.backend.util

const val EMAIL_REGEX = "[a-zA-Z0-9a-яА-Я\\+\\.\\_\\%\\-\\+]{1,256}" +
        "\\@[a-zA-Z0-9a-яА-Я][a-zA-Z0-9a-яА-Я\\-]{0,64}" +
        "(\\.[a-zA-Z0-9a-яА-Я][a-zA-Z0-9a-яА-Я\\-]{0,25})+"