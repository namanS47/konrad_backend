package com.example.konrad.services.jwtService

import com.example.konrad.entity.UserDetailsEntity
import com.example.konrad.model.ResponseModel
import com.example.konrad.model.jwt_models.UserDetailsConvertor
import com.example.konrad.model.jwt_models.UserDetailsModel
import com.example.konrad.repositories.UserDetailsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service


@Service
class JwtUserDetailsService(
        @Autowired val userDetailsRepository: UserDetailsRepository,
        @Autowired private val passwordEncoder: PasswordEncoder
) : UserDetailsService {
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        val response = userDetailsRepository.findByUsername(username)
        return if (response.isPresent) {
            val userDetailsEntity = response.get()
            User(userDetailsEntity.username, userDetailsEntity.password,
                    getAuthority())
        } else {
            throw UsernameNotFoundException("User not found with username: $username")
        }
    }

    private fun getAuthority(): Set<SimpleGrantedAuthority> {
        val authorities: MutableSet<SimpleGrantedAuthority> = HashSet()
        authorities.add(SimpleGrantedAuthority("ROLE_ADMIN"))
        return authorities
    }

    // This method returns user details without password
    fun getUserByUserName(username: String): UserDetailsEntity {
        val response = userDetailsRepository.findByUsername(username)
        return if(response.isPresent) {
            response.get()
        } else {
            throw UsernameNotFoundException("User not found with username: $username")
        }
    }

    fun addUser(userDetailsModel: UserDetailsModel): ResponseEntity<*> {
        val userDetailsEntity = UserDetailsConvertor.toEntity(userDetailsModel)
        val response = userDetailsEntity.username?.let { userDetailsRepository.findByUsername(it) }
        return if(response?.isPresent != true) {
            userDetailsEntity.password = passwordEncoder.encode(userDetailsEntity.password)
            userDetailsRepository.save(userDetailsEntity)
            ResponseEntity.ok().body(ResponseModel(success = true, body = null))
        } else {
            ResponseEntity.ok().body(ResponseModel(success = false, reason = "user already exist", body = null))
        }
    }
}