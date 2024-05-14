package kr.goldenmine.routefinder.utils

import kr.goldenmine.routefinder.model.User
import kr.goldenmine.routefinder.service.JwtService
import kr.goldenmine.routefinder.service.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.MethodParameter
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer


@Component
class HeaderJwtUserResolver(
    val userService: UserService,
    val jwtService: JwtService,
) : HandlerMethodArgumentResolver {
    private val logger: Logger = LoggerFactory.getLogger(HeaderJwtUserResolver::class.java)

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return User::class.java.isAssignableFrom(parameter.parameterType)
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): User? {
        val header = webRequest.getHeader("Authorization")
        logger.info("Authorization header: $header")

        if(header != null) {
            try {
                if (!header.startsWith("Bearer ")) {
                    throw BadCredentialsException("token is not started with Bearer")
                }

                val accessTokenWithoutBearer = header.substring(7)
                if (jwtService.isTokenExpired(accessTokenWithoutBearer)) {
                    throw BadCredentialsException("token is expired")
                }

                val user = userService.getUserByAccountId(jwtService.extractUsername(accessTokenWithoutBearer))
                    ?: throw BadCredentialsException("the user does not exist")

                return user
            } catch(ex: Exception) {
                // ignore
            }
        }

        return null
//        return User(0, "", "", "", false)
    }
}