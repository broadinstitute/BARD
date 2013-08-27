package bard.core.exceptions;


public class RestApiException extends RuntimeException {

    public RestApiException() {
        super();
    }

    public RestApiException(String s) {
        super(s);
    }

    public RestApiException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public RestApiException(Throwable throwable) {
        super(throwable);
    }


}
