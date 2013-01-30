package bard.db.project

/**
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 1/17/13
 * Time: 4:06 PM
 * To change this template use File | Settings | File Templates.
 *
 * Customized exception class to handle exception need to send back to view from service layer.
 */
class UserFixableException extends RuntimeException{
    public UserFixableException(){super()}
    public UserFixableException(String message, Throwable cause){super(message, cause)}
    public UserFixableException(String message){super(message)}
    public UserFixableException(Throwable cause){super(cause)}
}
