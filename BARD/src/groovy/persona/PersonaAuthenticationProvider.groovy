package persona
/**
 * Most of the code is borrowed from https://github.com/phjardas/spring-security-persona
 *
 * and modified slightly for BARD
 *
 */
import bard.auth.BardAuthorizationProviderService
import bard.db.people.Person
import bard.db.people.Role
import org.broadinstitute.cbip.crowd.CbipUser
import org.broadinstitute.cbip.crowd.Email
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UsernameNotFoundException
import util.BardUser

public class PersonaAuthenticationProvider extends BardAuthorizationProviderService {

    OnlinePersonaVerifyer onlinePersonaVerifyer;

    public PersonaAuthenticationProvider() {

    }

    @Override
    public Authentication authenticate(final Authentication authentication)
    throws AuthenticationException {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;

        if (!token.credentials) {
            throw new AuthenticationCredentialsNotFoundException("No token returned by verifier");
        }

        final String assertion = token.credentials.toString();
        final PersonaVerificationResponse verification = onlinePersonaVerifyer.verify(assertion);

        if (!verification.isOK()) {
            throw new BadCredentialsException("Assertion verification failed: "
                    + verification.getStatus() + " ("
                    + verification.getReason() + ")");
        }

        final String email = verification.getEmail();


        final BardUser bardUser = getOrCreateUser(email);

        if (!bardUser) {
            throw new UsernameNotFoundException("User not found: " + email);
        }

        return new UsernamePasswordAuthenticationToken(bardUser, assertion, bardUser.authorities);
    }


    private BardUser getOrCreateUser(final String email) {
        try {
            return findByUserName(email);
        } catch (final UsernameNotFoundException e) {
            log.warn(e.message)
        }
        BardUser bardUser = null
        Person.withTransaction {

            Person person = new Person(userName: email, emailAddress: email, fullName: email, modifiedBy: email)
            person.save(flush: true)

            person = Person.findByUserNameIlike(email)
            bardUser = new BardUser(username: person.userName, fullName: person.fullName, email: new Email(person.emailAddress), isActive: true,
                    authorities: person.roles, owningRole: person.newObjectRole ?: new Role(authority: person.userName));
        }
        return bardUser
    }

    BardUser findByUserName(String userName) {
        try {
            assert userName, "User Name is required"

            Person person = Person.findByUserNameIlike(userName);
            return new BardUser(username: person.userName, fullName: person.fullName, email: new Email(person.emailAddress), isActive: true,
                    authorities: person.roles, owningRole: person.newObjectRole ?: new Role(authority: person.userName));

        } catch (Exception ee) {
            throw new UsernameNotFoundException(ee.getMessage());
        }
    }

    List<CbipUser> findUsersInGroup(String group) {
        return []  //To change body of implemented methods use File | Settings | File Templates.
    }

    List<GrantedAuthority> getNamesOfGroupsForUser(String userName) {
        return []//To change body of implemented methods use File | Settings | File Templates.
    }

    List<CbipUser> getUsersOfGroup(String groupName) {
        return [] //To change body of implemented methods use File | Settings | File Templates.
    }

    boolean isUserDirectGroupMember(String username, String groupName) {
        return false  //To change body of implemented methods use File | Settings | File Templates.
    }
}
