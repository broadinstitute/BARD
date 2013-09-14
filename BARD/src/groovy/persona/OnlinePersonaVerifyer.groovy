package persona

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

			if (response.getStatusCode() != HttpStatus.OK) {
				// FIXME exception
				throw new BadCredentialsException(
						"Assertion verification failed: "
								+ response.getStatusCode());
			}

			return response.getBody();
		} catch (final RuntimeException e) {
			e.printStackTrace();

			throw new RuntimeException(
					"Error verifying assertion: " + e, e);
		}
	}
}
