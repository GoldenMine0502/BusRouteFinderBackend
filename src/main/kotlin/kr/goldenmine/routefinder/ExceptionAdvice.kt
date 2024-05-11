package kr.goldenmine.routefinder

import kr.goldenmine.dowayobackend.util.impl.BadRequestException
import kr.goldenmine.dowayobackend.util.impl.ConflictException
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.*

@ControllerAdvice
class ExceptionAdvice {
    @ExceptionHandler(BadCredentialsException::class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    @ResponseBody
    fun handleBadCredentialsException(e: BadCredentialsException): Map<String, String> {
        val retMessages = HashMap<String, String>()

        retMessages["message"] = e.message ?: "null"
        return retMessages
    }

    @ExceptionHandler(BadRequestException::class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    fun handleBadRequestException(e: BadRequestException): Map<String, String> {
        val retMessages = HashMap<String, String>()
        retMessages["message"] = e.message ?: "null"
        return retMessages
    }

    @ExceptionHandler(ConflictException::class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    @ResponseBody
    fun handleConflictException(e: ConflictException): Map<String, String> {
        val retMessages = HashMap<String, String>()
        retMessages["message"] = e.message ?: "null"
        return retMessages
    }
}