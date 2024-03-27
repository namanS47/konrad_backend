package com.example.konrad.config.jwt


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.stereotype.Component
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.util.*


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
        securedEnabled = false,
        jsr250Enabled = true,
        prePostEnabled = false
)
class WebSecurityConfig() {
    @Autowired
    private val jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint? = null

    @Autowired
    private val jwtRequestFilter: JwtRequestFilter? = null

    @Throws(Exception::class)
    @Bean
    protected fun configure(httpSecurity: HttpSecurity): DefaultSecurityFilterChain {
        httpSecurity
                .csrf{it.disable()}
                .authorizeHttpRequests{
            it.requestMatchers("/authenticate",
                "/authenticate/otp",
                "/authenticate/otp/request",
                "/refreshToken",
                "/fcmToken",
                "/stripe/webhook",
                "/").permitAll()
                    .anyRequest().authenticated()
        }.exceptionHandling{exception ->  exception.authenticationEntryPoint(jwtAuthenticationEntryPoint)}
                .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }

        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter::class.java)
        return httpSecurity.build()
    }
}

@Component
class ServiceInterceptorAppConfig : WebMvcConfigurer {
    //TODO: Change configuration for production
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins("*") // Replace with your actual frontend domain
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
//                .allowCredentials(true)
    }
}