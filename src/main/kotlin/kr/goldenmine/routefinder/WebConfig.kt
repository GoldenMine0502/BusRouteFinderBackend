package kr.goldenmine.routefinder

import kr.goldenmine.routefinder.utils.HeaderJwtUserResolver
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
class WebConfig(
    val headerJwtUserResolver: HeaderJwtUserResolver,
): WebMvcConfigurer {
    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver?>) {
        resolvers.add(headerJwtUserResolver)
    }
}