package com.ctf.backend.errors

import org.springframework.http.HttpStatus

class DeletedObjectResponse(data: String) : ApiError(
    status = HttpStatus.OK,
    message = "Объект $data успешно удален",
    debugMessage = "Object $data has deleted successfully",
)
