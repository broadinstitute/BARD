/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
        String emailLowercase = email.toLowerCase();
        BardUser bardUser = null

        try {
            bardUser = findByEmail(emailLowercase);
        } catch (final UsernameNotFoundException e) {
            log.warn(e.message)
        }

        if(bardUser == null) {
            println("could not find ${emailLowercase}")
            Person.withTransaction {
                Person person = new Person(userName: emailLowercase, emailAddress: emailLowercase, fullName: emailLowercase, modifiedBy: emailLowercase)
                person.save(flush: true)

                bardUser = findByEmail(emailLowercase);
            }
        }

        return bardUser
    }

    BardUser findByEmail(String emailLowercase) {
        try {
            assert emailLowercase, "email is required"

            Person person = Person.findByEmailAddress(emailLowercase)
            return new BardUser(username: person.userName, fullName: person.fullName, email: new Email(person.emailAddress), isActive: true,
                    authorities: person.roles);
        } catch (Exception ee) {
            throw new UsernameNotFoundException(ee.getMessage());
        }
    }
}
