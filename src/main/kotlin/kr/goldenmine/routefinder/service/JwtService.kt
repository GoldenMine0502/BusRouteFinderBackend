package kr.goldenmine.routefinder.service

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import kr.goldenmine.routefinder.model.User
import org.springframework.stereotype.Service
import java.security.Key
import java.util.*
import kotlin.collections.HashMap
import kotlin.reflect.jvm.reflect


@Service
class JwtService {
    private val secret = "357638792F423F4428472B4B6250655368566D597133743677397A2443264629"

    private val signKey: Key
        get() {
            val keyBytes = Decoders.BASE64.decode(secret)
            return Keys.hmacShaKeyFor(keyBytes)
        }

    fun extractUsername(token: String?): String {
        return extractClaim(token) { obj: Claims -> obj.subject }
    }

    fun extractExpiration(token: String?): Date {
        return extractClaim(token) { obj: Claims -> obj.expiration }
    }

    fun <T> extractClaim(token: String?, claimsResolver: (Claims) -> T): T {
        val claims = extractAllClaims(token)
        return claimsResolver.invoke(claims)
    }

    private fun extractAllClaims(token: String?): Claims {
        return Jwts
            .parserBuilder()
            .setSigningKey(signKey)
            .build()
            .parseClaimsJws(token)
            .body
    }

    fun isTokenExpired(token: String?): Boolean {
        return try {
            extractExpiration(token).before(Date())
        } catch(ex: ExpiredJwtException) {
            true
        }
    }

    fun validateToken(token: String?, user: User): Boolean {
        val username = extractUsername(token)
        return username == user.accountId && !isTokenExpired(token)
    }

    fun generateToken(username: String): String {
        val claims: Map<String, Any?> = HashMap()
        return createToken(claims, username)
    }

    val time = 60 * 60 * 2 * 1000 // 2 hour
//    val time = 60 * 1000 // 1 minute

    private fun createToken(claims: Map<String, Any?>, username: String): String {
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(username)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + time))
            .signWith(signKey, SignatureAlgorithm.HS256).compact()
    }
}