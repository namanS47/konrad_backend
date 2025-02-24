package com.example.konrad.config.jwt

import com.example.konrad.entity.UserDetailsEntity
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.io.Serializable
import java.util.*
import java.util.function.Function


@Component
class JwtTokenUtil : Serializable {
    @Value("\${jwt.secret}")
    private val secret: String? = null

    //retrieve username from jwt token
    fun getUsernameFromToken(token: String?): String {
        var jwtToken = token
        if(jwtToken != null && jwtToken.startsWith("Bearer")) {
            jwtToken = jwtToken.substring(7)
        }
        return getClaimFromToken(jwtToken) { obj: Claims -> obj.subject }
    }

    fun getRoleFromToken(token: String?): String {
        var jwtToken = token
        if(jwtToken != null && jwtToken.startsWith("Bearer")) {
            jwtToken = jwtToken.substring(7)
        }
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(jwtToken).body["roles"] as String
    }

    //retrieve expiration date from jwt token
    fun getExpirationDateFromToken(token: String?): Date {
        return getClaimFromToken(token) { obj: Claims -> obj.expiration }
    }

    fun <T> getClaimFromToken(token: String?, claimsResolver: Function<Claims, T>): T {
        val claims = getAllClaimsFromToken(token)
        return claimsResolver.apply(claims)
    }

    //for retrieveing any information from token we will need the secret key
    private fun getAllClaimsFromToken(token: String?): Claims {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).body
    }

    //check if the token has expired
    private fun isTokenExpired(token: String?): Boolean {
        val expiration = getExpirationDateFromToken(token)
        return expiration.before(Date())
    }

    //generate token for user
    fun generateToken(id: String, role: String): String {
        val claims: Map<String, Any> = hashMapOf("roles" to role)
        return doGenerateToken(claims, id)
    }

    //while creating the token -
    //1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
    //2. Sign the JWT using the HS512 algorithm and secret key.
    //3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
    //   compaction of the JWT to a URL-safe string
    private fun doGenerateToken(claims: Map<String, Any>, subject: String): String {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(Date(System.currentTimeMillis()))
                .setExpiration(Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, secret).compact()
    }

    //validate token
    fun validateToken(token: String?, userDetails: UserDetails): Boolean {
        val username = getUsernameFromToken(token)
        return username == userDetails.username && !isTokenExpired(token)
    }

    companion object {
        private const val serialVersionUID = -2550185165626007488L
        const val JWT_TOKEN_VALIDITY = (20 * 60).toLong()
    }
}