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
public class PersonaVerificationResponse implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String STATUS_OK = "okay";
	public static final String STATUS_FAILURE = "failure";
	private String status;
	private String email;
	private Date expires;
	private String issuer;
	private String audience;
	private String reason;

	public PersonaVerificationResponse() {
	}

	private PersonaVerificationResponse(final String status,
			final String email, final Date expires, final String issuer,
			final String audience, final String reason) {
		this.status = status;
		this.email = email;
		this.expires = expires;
		this.issuer = issuer;
		this.audience = audience;
		this.reason = reason;
	}

	static PersonaVerificationResponse ok(final String email,
			final Date expires, final String issuer, final String audience) {
		return new PersonaVerificationResponse(STATUS_OK, email, expires,
				issuer, audience, null);
	}

	static PersonaVerificationResponse failure(final String reason) {
		return new PersonaVerificationResponse(STATUS_FAILURE, null, null,
				null, null, reason);
	}

	public String getStatus() {
		return status;
	}

	public String getEmail() {
		return email;
	}

	public Date getExpires() {
		return expires;
	}

	public String getIssuer() {
		return issuer;
	}

	public String getAudience() {
		return audience;
	}

	public String getReason() {
		return reason;
	}

	public boolean isOK() {
		return STATUS_OK.equals(status);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "<status=" + status + ", email="
				+ email + ", expires=" + expires + ", issuer=" + issuer
				+ ", reason=" + reason + ">";
	}
}
