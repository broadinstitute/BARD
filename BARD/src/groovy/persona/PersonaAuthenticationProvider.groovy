package persona
/**
 * Most of the code is borrowed from https://github.com/phjardas/spring-security-persona
 *
 * and modified slightly for BARD
 *
 */
import bard.db.people.Person
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import util.BardUser
import util.Email

public class PersonaAuthenticationProvider implements AuthenticationProvider {

    OnlinePersonaVerifyer onlinePersonaVerifyer;

    public PersonaAuthenticationProvider() {

    }

    @Override
    public boolean supports(Class<? extends Object> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
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
                    authorities: person.roles);
        }
        return bardUser
    }

    BardUser findByUserName(String userName) {
        try {
            assert userName, "User Name is required"

            Person person = Person.findByUserNameIlike(userName);
            return new BardUser(username: person.userName, fullName: person.fullName, email: new Email(person.emailAddress), isActive: true,
                    authorities: person.roles);

        } catch (Exception ee) {
            throw new UsernameNotFoundException(ee.getMessage());
        }
    }
}
