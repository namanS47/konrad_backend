package com.example.konrad.services.jwtService

import com.example.konrad.entity.UserDetailsEntity
import com.example.konrad.model.ResponseModel
import com.example.konrad.model.jwt_models.UserDetailsConvertor
import com.example.konrad.model.jwt_models.UserDetailsModel
import com.example.konrad.model.jwt_models.UserRoles
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
            if(userDetailsEntity.roles?.get(0) == UserRoles.CUSTOMER) {
                User(userDetailsEntity.username, "",
                        getAuthority(userDetailsEntity.roles))
            } else {
                User(userDetailsEntity.username, userDetailsEntity.password,
                        getAuthority(userDetailsEntity.roles))
            }
        } else {
            throw UsernameNotFoundException("User not found with username: $username")
        }
    }

    private fun getAuthority(roles: List<String>?): Set<SimpleGrantedAuthority> {
        val authorities: MutableSet<SimpleGrantedAuthority> = HashSet()
        roles?.forEach {
            authorities.add(SimpleGrantedAuthority(it))
        }
        return authorities
    }

    // This method returns user details without password
    fun getUserByUserName(username: String): ResponseModel<UserDetailsEntity> {
        val response = userDetailsRepository.findByUsername(username)
        return if(response.isPresent) {
            ResponseModel(success = true, body = response.get())
        } else{
            ResponseModel(success = false, reason = "User doesn't exist")
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
            ResponseEntity.ok().body(ResponseModel(success = false, reason = "username already exist", body = null))
        }
    }

    fun addNewUserAuthenticatedViaOtp(userDetailsModel: UserDetailsModel): ResponseModel<Boolean> {
        return try {
            userDetailsModel.roles = listOf(UserRoles.CUSTOMER)
            userDetailsModel.enabled = true
            userDetailsRepository.save(UserDetailsConvertor.toEntity(userDetailsModel))
            ResponseModel(success = true)
        } catch (e: Exception) {
            ResponseModel(success = false, reason = e.message)
        }
    }
}