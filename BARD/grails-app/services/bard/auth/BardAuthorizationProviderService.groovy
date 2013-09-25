package bard.auth

import bard.db.people.Person
import bard.db.people.Role
import org.broadinstitute.cbip.crowd.CbipUser
import org.broadinstitute.cbip.crowd.CrowdAuthenticationProviderService
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UsernameNotFoundException
import util.BardUser

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
            final Object principal = authenticate.principal
            BardUser bardUser = null
            if (principal instanceof CbipUser) {
                bardUser = new BardUser((CbipUser) principal)
            } else {//it must be a BardUser
                bardUser = (BardUser) principal
            }

            def credentials = authenticate.credentials
            addRolesFromDatabase(bardUser)
            return new UsernamePasswordAuthenticationToken(bardUser, credentials, bardUser.authorities);
        } catch (Exception ee) {
            log.error(ee)
            throw ee
        }

    }
    /**
     *
     * @param bardUser
     */
    public void addRolesFromDatabase(final BardUser bardUser) {

        final String userName = bardUser.username

        Person.withTransaction {txn ->
        final Person person = Person.findByUserNameIlike(userName)
            bardUser.owningRole = new Role(authority:userName)
            if (person) {
                if (person.newObjectRole) {
                    bardUser.owningRole = person.newObjectRole
                }
                final Set<Role> authorities = person.getRoles()
                bardUser.authorities = authorities
            } else{
                bardUser.authorities = []
            }
        }
     }
    /**
     *
     * @param userName
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public BardUser findByUserName(String userName) throws UsernameNotFoundException {
        try {
            final Object user = super.findByUserName(userName)
            BardUser bardUser = null
            if(user instanceof CbipUser){
                bardUser = new BardUser((CbipUser)user)
            }
            else if(user instanceof BardUser){
                bardUser = (BardUser)user
            }
            addRolesFromDatabase(bardUser)
            return bardUser
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

        return []
    }

    /**
     *
     * @param username
     * @param groupName
     * @return true if this user is a direct member of this group
     */
    @Override
    boolean isUserDirectGroupMember(String username, String groupName) {

        return false
    }

}
