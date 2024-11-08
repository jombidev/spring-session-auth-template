package dev.jombi.template.core.auth

import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Component
class ServletHolder {
    private fun getAttributes() = RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes
    fun getRequest() = getAttributes().request
}
