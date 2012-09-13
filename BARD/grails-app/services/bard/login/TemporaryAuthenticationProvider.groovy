package bard.login


import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 9/11/12
 * Time: 10:57 PM
 * To change this template use File | Settings | File Templates.
 */
class TemporaryAuthenticationProvider implements AuthenticationProvider {

    UserDetailsService userDetailsService

    Authentication authenticate(Authentication authentication) {
        final User user = userDetailsService.loadUserByUsername(authentication.getName())
        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
    }

    boolean supports(Class<? extends Object> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
