package persona

import bard.auth.BardAuthorizationProviderService
import bard.db.people.Person
import org.broadinstitute.cbip.crowd.CbipUser
import org.broadinstitute.cbip.crowd.Email
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException

public class PersonaAuthenticationProvider {
    OnlinePersonaVerifyer onlinePersonaVerifyer
    BardAuthorizationProviderService bardAuthorizationProviderService

    public PersonaAuthenticationProvider() {

    }

    public Authentication personaAuthentication(String assertion) {

        if (!assertion) {
            throw new AuthenticationCredentialsNotFoundException("No assertion");
        }
        final PersonaVerificationResponse verification = onlinePersonaVerifyer.verify(assertion);

        if (!verification.isOK()) {
            throw new BadCredentialsException("Assertion verification failed: "
                    + verification.getStatus() + " ("
                    + verification.getReason() + ")");
        }

        final String email = verification.getEmail();
        String username = email.substring(0, email.indexOf("@"));

        Person person = Person.findByUserName(username);

        try {
            //if we do not know this person then create them (We are using email here, but what if a person has multiple email addresses?)
            if (!person) {
                CbipUser cbipUser = new CbipUser(username, username, new Email(email), true, [])
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(cbipUser, assertion, []);

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken)
                //create the Person
                person = new Person(userName: username, emailAddress: email, fullName: email)
                person.save(flush: true)
                if (person.hasErrors()) {
                    throw new UsernameNotFoundException("User not found: " + email);
                }
            }
        } catch (Exception ee) {
            throw new UsernameNotFoundException("User not found: " + email);
        }
        final List<GrantedAuthority> roles = bardAuthorizationProviderService.getRolesFromDatabase(person.userName)
        CbipUser cbipUser = new CbipUser(person.userName, person.fullName, new Email(person.emailAddress), true, roles)
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(cbipUser, assertion, roles);

        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken)
        return usernamePasswordAuthenticationToken
    }
}
