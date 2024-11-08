package dev.jombi.template.core.auth.service

import dev.jombi.template.business.auth.service.AuthService
import dev.jombi.template.common.exception.CustomException
import dev.jombi.template.core.auth.ServletHolder
import dev.jombi.template.core.auth.exception.AuthExceptionDetails
import dev.jombi.template.core.member.entity.Member
import dev.jombi.template.core.member.repository.MemberJpaRepository
import jakarta.servlet.ServletException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthServiceImpl(
    private val memberRepository: MemberJpaRepository,
    private val servletHolder: ServletHolder,
    private val passwordEncoder: PasswordEncoder,
) : AuthService {
    override fun authenticate(credential: String, password: String) {
        try {
            servletHolder.getRequest().login(credential, password)
        } catch (e: ServletException) {
            throw e.rootCause
        }
    }

    override fun createNewMember(name: String, credential: String, password: String) {
        if (memberRepository.existsByCredential(credential))
            throw CustomException(AuthExceptionDetails.USER_ALREADY_EXISTS, credential)

        memberRepository.save(
            Member(
                name = name,
                credential = credential,
                password = passwordEncoder.encode(password),
            )
        )
    }
}
