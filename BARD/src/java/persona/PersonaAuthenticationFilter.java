package persona;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PersonaAuthenticationFilter extends
        AbstractAuthenticationProcessingFilter {
	public PersonaAuthenticationFilter() {
		super("/signin/persona");
	}

	@Override
	public Authentication attemptAuthentication(
			final HttpServletRequest request, final HttpServletResponse response) {
		final String assertion = request.getParameter("assertion");

		if (StringUtils.hasText(assertion)) {
			final PersonaAuthenticationToken token = new PersonaAuthenticationToken(
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
