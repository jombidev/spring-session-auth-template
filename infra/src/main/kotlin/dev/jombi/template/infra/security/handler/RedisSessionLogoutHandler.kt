package dev.jombi.template.infra.security.handler

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import org.springframework.session.data.redis.RedisIndexedSessionRepository
import org.springframework.stereotype.Component

@Component
class RedisSessionLogoutHandler(
    private val redisIndexedSessionRepository: RedisIndexedSessionRepository,
) : LogoutHandler, LogoutSuccessHandler {
    override fun logout(request: HttpServletRequest, response: HttpServletResponse, authentication: Authentication?) {
        val id = request.getSession(false)?.id?.let { redisIndexedSessionRepository.findById(it) } ?: return

        redisIndexedSessionRepository.deleteById(id.id) // invalidate session if exists
    }

    override fun onLogoutSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication?,
    ) {
        SecurityContextHolder.clearContext()
    }
}
