package com.example.konrad.config.jwt


import com.example.konrad.services.jwtService.JwtUserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
        securedEnabled = false,
        jsr250Enabled = true,
        prePostEnabled = false
)
class WebSecurityConfig(
        @Autowired private val jwtUserDetailsService: JwtUserDetailsService,
        @Autowired private val passwordEncoder: PasswordEncoder
) {
    @Autowired
    private val jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint? = null

    @Autowired
    private val jwtRequestFilter: JwtRequestFilter? = null

    @Autowired
    @Throws(Exception::class)
    fun configureGlobal(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder)
    }

    @Throws(Exception::class)
    @Bean
    protected fun configure(httpSecurity: HttpSecurity): DefaultSecurityFilterChain {
        httpSecurity
                .csrf{it.disable()}
                .authorizeHttpRequests{
            it.requestMatchers("/authenticate", "/api/auth").permitAll()
                    .anyRequest().authenticated()
        }.exceptionHandling{exception ->  exception.authenticationEntryPoint(jwtAuthenticationEntryPoint)}
                .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }

        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter::class.java)
        return httpSecurity.build()
    }
}