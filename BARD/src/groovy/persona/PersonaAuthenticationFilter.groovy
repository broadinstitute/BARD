package persona

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.util.StringUtils

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Most of the code is borrowed from https://github.com/phjardas/spring-security-persona
 *
 * and modified slightly for BARD
 *
 */

public class PersonaAuthenticationFilter extends
        AbstractAuthenticationProcessingFilter {
    public PersonaAuthenticationFilter() {
        super("/j_spring_persona_security_check");
    }

    @Override
    public Authentication attemptAuthentication(
            final HttpServletRequest request, final HttpServletResponse response) {
        final String assertion = request.getParameter("assertion");

        if (StringUtils.hasText(assertion)) {
            final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(null,
                    assertion);
            try {
                return getAuthenticationManager().authenticate(token);
            } catch (final AuthenticationException e) {
                throw e;
            } catch (final RuntimeException e) {
                throw new RuntimeException(
                        "Error authenticating with Mozilla Persona: " + e, e);
            }
        }

        return null;
    }
}
