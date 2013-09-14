package bard.auth

import bard.db.people.Person
import bard.db.people.Role
import org.broadinstitute.cbip.crowd.CbipUser
import org.broadinstitute.cbip.crowd.CrowdAuthenticationProviderService
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.GrantedAuthorityImpl
import org.springframework.security.core.userdetails.UsernameNotFoundException

class PersonaAuthorizationProviderService extends BardAuthorizationProviderService {
    /**
     *
     * @param authentication
     * @return
     */
    @Override
    public Authentication authenticate(Authentication authentication) {
        try {
            //get the authenticated user from crowd
            UsernamePasswordAuthenticationToken authenticate = (UsernamePasswordAuthenticationToken) super.authenticate(authentication)
            CbipUser cbipUser = (CbipUser) authenticate.principal
            def credentials = authenticate.credentials
            final List<GrantedAuthority> roles = super.getRolesFromDatabase(cbipUser.username)
            return new UsernamePasswordAuthenticationToken(cbipUser, credentials, roles);
        } catch (Exception ee) {
            log.error(ee)
            throw ee
        }

    }
}
