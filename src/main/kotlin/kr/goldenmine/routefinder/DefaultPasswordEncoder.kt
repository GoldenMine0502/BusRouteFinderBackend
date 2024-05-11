package kr.goldenmine.routefinder

import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class DefaultPasswordEncoder: PasswordEncoder {
    private val defaultPasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()

    override fun encode(rawPassword: CharSequence?): String {
        return defaultPasswordEncoder.encode(rawPassword)
    }

    override fun matches(rawPassword: CharSequence?, encodedPassword: String?): Boolean {
        return defaultPasswordEncoder.matches(rawPassword, encodedPassword)
    }
}