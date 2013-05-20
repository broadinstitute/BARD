package bard.auth

import bard.db.people.Person
import bard.db.people.Role
import org.broadinstitute.cbip.crowd.CbipUser
import org.broadinstitute.cbip.crowd.CrowdAuthenticationProviderService
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UsernameNotFoundException

class BardAuthorizationProviderService extends CrowdAuthenticationProviderService {
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
            final Collection<GrantedAuthority> rolesFromCrowd = authenticate.authorities


            final List<GrantedAuthority> roles = []
            if (rolesFromCrowd) {
                roles.addAll(rolesFromCrowd)
            }

            //now lets get the roles for this user in the local database
            final List<GrantedAuthority> rolesFromDatabase = getRolesFromDatabase(cbipUser.username)
            if (rolesFromDatabase) {
                roles.addAll(rolesFromDatabase)
            }

            return new UsernamePasswordAuthenticationToken(cbipUser, credentials, roles);
        } catch (Exception ee) {
            log.error(ee)
            throw ee
        }

    }
    /**
     *
     * @param userName
     * @return {@link List} of {@GrantedAuthority}
     */
    protected List<GrantedAuthority> getRolesFromDatabase(String userName) {
        final List<GrantedAuthority> roles = []
        final Person person = Person.findByUserName(userName)
        if (person) {
            final Set<Role> authorities = person.getRoles()
            if (authorities) {
                for (Role authority : authorities) {
                    final GrantedAuthority role = toApplicationRole(authority.authority)
                    if (role) {
                        roles.add(role)
                    }
                }
            }
        }
        return roles
    }
    /**
     *
     * @param userName
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public CbipUser findByUserName(String userName) throws UsernameNotFoundException {
        try {
            CbipUser user = super.findByUserName(userName)
            final List<GrantedAuthority> roles = []
            final Collection<GrantedAuthority> rolesFromCrowd = user.getAuthorities()
            if (rolesFromCrowd) {
                roles.addAll(rolesFromCrowd)
            }
            final List<GrantedAuthority> rolesFromDatabase = getRolesFromDatabase(user.getUsername())
            if (rolesFromDatabase) {
                roles.addAll(rolesFromDatabase)
            }
            return new CbipUser(user.getUsername(), user.getFullName(), user.getEmail(), user.isActive(), roles);
        } catch (Exception ee) {
            throw new UsernameNotFoundException(ee.getMessage());
        }
    }

    /**
     * Get the names of the CAP supported roles that this user belongs to
     * @param userName
     * @return {@link List} of {@GrantedAuthority}
     */
    @Override
    List<GrantedAuthority> getNamesOfGroupsForUser(String userName) {
        def authorities = super.getNamesOfGroupsForUser(userName)
        authorities.addAll(getRolesFromDatabase(userName))
        return authorities;
    }

    /**
     *
     * @param username
     * @param groupName
     * @return true if this user is a direct member of this group
     */
    @Override
    boolean isUserDirectGroupMember(String username, String groupName) {
        boolean isMember = super.isUserDirectGroupMember(username, groupName)
        if (!isMember) {
            final List<GrantedAuthority> roles = getRolesFromDatabase(username)
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
