package bard.auth

import bard.db.people.Person
import bard.db.people.Role
import org.broadinstitute.cbip.crowd.CbipUser
import org.broadinstitute.cbip.crowd.CrowdAuthenticationProviderService
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UsernameNotFoundException

class BardAuthorizationProviderService extends CrowdAuthenticationProviderService {

    @Override
    public Authentication authenticate(Authentication authentication) {
        try {
            final Authentication authenticate = super.authenticate(authentication)
            authenticate.getAuthorities().addAll(getAdditionalRoles(authentication.getPrincipal()?.username))
            return authenticate
        } catch (Exception ee) {
            log.error(ee)
            throw ee
        }

    }

    protected List<GrantedAuthority> getAdditionalRoles(String userName) {
        List<GrantedAuthority> roles = []
        final Person person = Person.findByUserName(userName)
        if (person) {
            final Set<Role> authorities = person.getRoles()
            if (authorities) {
                for (Role authority : authorities) {
                    roles.add(toApplicationRole(authority.authority))
                }
            }
        }
        return roles
    }

    @Override
    public CbipUser findByUserName(String userName) throws UsernameNotFoundException {
        try {
            CbipUser user = super.findByUserName(userName)
            final Collection<GrantedAuthority> roles = user.authorities
            roles.addAll(getAdditionalRoles(userName))
            return user
        } catch (Exception ee) {
            throw new UsernameNotFoundException(ee.getMessage());
        }
    }



    @Override
    List<GrantedAuthority> getNamesOfGroupsForUser(String userName) {
        def authorities = super.getNamesOfGroupsForUser(userName)
        authorities.addAll(getAdditionalRoles(userName))
        return authorities;
    }



    @Override
    boolean isUserDirectGroupMember(String username, String groupName) {
        boolean isMember = super.isUserDirectGroupMember(username, groupName)
        if (!isMember) {
            final List<GrantedAuthority> roles = getAdditionalRoles(username)
            for (GrantedAuthority role : roles) {
                if (role.authority == groupName) {
                    isMember = true;
                    break;
                }
            }
        }

        return isMember
    }

}
