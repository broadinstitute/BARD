package persona
/**
 * Most of the code is borrowed from https://github.com/phjardas/spring-security-persona
 *
 * and modified slightly for BARD
 *
 */
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate

public class OnlinePersonaVerifyer{
	RestTemplate restTemplate;
	String audience; //your url
    String verificationUrl

	public OnlinePersonaVerifyer() {

	}


	@Override
	public PersonaVerificationResponse verify(final String assertion) {

		final LinkedMultiValueMap<String, String> request = new LinkedMultiValueMap<String, String>();
		request.put("assertion", Arrays.asList(assertion));
		request.put("audience", Arrays.asList(audience));

		try {
			final ResponseEntity<PersonaVerificationResponse> response = restTemplate
					.postForEntity(this.verificationUrl,
							request, PersonaVerificationResponse.class);

			if (response.getStatusCode() != HttpStatus.OK) { //If status is not OK then throw exception
				String errorMessage ="Assertion verification failed: ${response.getStatusCode()}"
                log?.error(errorMessage)
				throw new BadCredentialsException(errorMessage);
			}
			return response.getBody();
		} catch (final RuntimeException e) {
            log.error("Error verifying assertion: " + e,e)
			e.printStackTrace();

			throw new RuntimeException(
					"Error verifying assertion: " + e, e);
		}
	}
}
