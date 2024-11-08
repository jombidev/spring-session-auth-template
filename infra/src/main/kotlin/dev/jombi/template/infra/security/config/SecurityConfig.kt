package dev.jombi.template.infra.security.config

import dev.jombi.template.infra.exception.ExceptionHandleFilter
import dev.jombi.template.infra.security.handler.RedisSessionLogoutHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource


@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Bean
    fun filterChain(
        http: HttpSecurity,
        exceptionFilter: ExceptionHandleFilter,
        redisSessionLogoutHandler: RedisSessionLogoutHandler,
        authManager: AuthenticationManager
    ): SecurityFilterChain {
        return http
            .httpBasic { it.disable() }
            .csrf { it.disable() }
            .cors { it.configurationSource(corsConfigurationSource()) }
            .authenticationManager(authManager)
            .sessionManagement {
                it
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                    .sessionFixation { conf -> conf.newSession() }
                    .maximumSessions(1)
                    .maxSessionsPreventsLogin(false)
            }
            .authorizeHttpRequests {
                it
                    .requestMatchers("/auth/logout").authenticated()
                    .requestMatchers("/auth/**").anonymous()
                    .anyRequest().authenticated()
            }
            .logout {
                it
                    .logoutUrl("/auth/logout")
                    .invalidateHttpSession(true)
                    .addLogoutHandler(redisSessionLogoutHandler)
                    .logoutSuccessHandler(redisSessionLogoutHandler)
            }
            .addFilterBefore(exceptionFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()
    }

    @Bean
    fun corsConfigurationSource() = UrlBasedCorsConfigurationSource()
        .apply {
            registerCorsConfiguration("/**",
                CorsConfiguration()
                    .apply { // kotlin style builder
                        addAllowedOriginPattern("*")
                        addAllowedHeader("*")
                        addAllowedMethod("*")
                        allowCredentials = true
                    }
            )
        }
}
