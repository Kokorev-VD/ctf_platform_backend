package com.ctf.backend.errors

import org.springframework.http.HttpStatus

class UnsupportedFileTypeException : ApiError(
    status = HttpStatus.UNSUPPORTED_MEDIA_TYPE,
    message = "Неподдерживаемый формат файла",
    debugMessage = "Unsupported media file",
)