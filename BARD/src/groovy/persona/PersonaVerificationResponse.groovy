package persona

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
