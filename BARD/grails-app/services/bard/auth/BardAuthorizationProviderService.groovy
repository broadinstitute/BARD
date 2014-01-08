package bard.auth

import bard.db.people.Person
import bard.db.people.Role
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UsernameNotFoundException
import util.BardUser

class BardAuthorizationProviderService implements AuthenticationProvider {
    AuthenticationProvider delegate;

    /**
     *
     * @param authentication
     * @return
     */
    @Override
    public Authentication authenticate(Authentication authentication) {
        try {
            UsernamePasswordAuthenticationToken authenticate = (UsernamePasswordAuthenticationToken) delegate.authenticate(authentication);
            final Object principal = authenticate.principal
            BardUser bardUser = null
            if (principal instanceof BardUser || principal == null) {
                bardUser = (BardUser) principal
            } else {
                bardUser = new BardUser(principal)
            }

            def credentials = authenticate.credentials
            addRolesFromDatabase(bardUser)
            return new UsernamePasswordAuthenticationToken(bardUser, credentials, bardUser.authorities);
        } catch (Exception ee) {
            log.error(ee,ee)
            throw ee
        }
    }

    boolean supports(Class<? extends Object> aClass) {
        return delegate.supports(aClass);
    }

    public void addRolesFromDatabase(final BardUser bardUser) {

        final String userName = bardUser.username

        Person.withTransaction {txn ->
        final Person person = Person.findByUserNameIlike(userName)
            if (person) {
                final Set<Role> authorities = person.getRoles()
                bardUser.authorities = authorities
            } else{
                bardUser.authorities = []
            }
        }
     }
}
