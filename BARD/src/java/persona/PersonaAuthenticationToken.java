package persona;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class PersonaAuthenticationToken extends AbstractAuthenticationToken {
	private static final long serialVersionUID = 1L;
	private Object principal;
	private String assertion;

	public PersonaAuthenticationToken(final String assertion) {
		super(null);
		this.assertion = assertion;
		setAuthenticated(false);
	}

	public PersonaAuthenticationToken(final Object principal,
			final Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.principal = principal;
		setAuthenticated(true);
	}


	public Object getPrincipal() {
		return principal;
	}


	public Object getCredentials() {
		return assertion;
	}
}
