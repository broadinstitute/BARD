package bard.core;

public class ServiceException extends Exception {
    private static final long serialVersionUID = 0x79c2305d79147a69l;

    public ServiceException () {}
    public ServiceException (Throwable t) {
        super (t);
    }
    public ServiceException (String message) {
        super (message);
    }
    public ServiceException (String message, Throwable t) {
        super (message, t);
    }
}
