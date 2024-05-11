package kr.goldenmine.routefinder.controller

import jakarta.servlet.http.HttpServletRequest
import kr.goldenmine.dowayobackend.auth.models.RequestLogin
import kr.goldenmine.dowayobackend.auth.models.RequestLoginRefreshToken
import kr.goldenmine.dowayobackend.auth.models.ResponseLogin
import kr.goldenmine.routefinder.model.RefreshToken
import kr.goldenmine.routefinder.model.User
import kr.goldenmine.routefinder.request.RegisterRequest
import kr.goldenmine.routefinder.service.JwtService
import kr.goldenmine.routefinder.service.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController(
    val userService: UserService,
    val jwtService: JwtService,
) {
    private val logger: Logger = LoggerFactory.getLogger(UserController::class.java)

    @PutMapping("/register")
    fun register(@RequestBody registerRequest: RegisterRequest): ResponseEntity<String> {
        val user = User(0, registerRequest.id, registerRequest.password, registerRequest.nickname, false)

        userService.register(user)
        logger.info("user created: ${user.accountId} ${user.nickname} ")

        return ResponseEntity.status(HttpStatus.CREATED).body("OK")
    }


    @PostMapping("/login")
    fun login(@RequestBody requestLogin: RequestLogin, request: HttpServletRequest): ResponseLogin {
        val user = userService.authenticate(requestLogin.id, requestLogin.password) // if there's no exception, the login succeed

        val accessToken = jwtService.generateToken(requestLogin.id)

        return ResponseLogin(
            accessToken = accessToken,
            refreshToken = "",
        )
    }
}