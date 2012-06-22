package bard.db.dictionary

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 6/22/12
 * Time: 9:07 AM
 * To change this template use File | Settings | File Templates.
 */
class TestUtils {
    final static String lString10 = "1234567890"
    final static String lString19 =  lString10+"123456789"
    final static String lString29 =  lString19+"123456789"
    static String lString40 = lString10+lString10+lString10+lString10
    static String lString50 = lString40+lString10
    static String lString100 = lString50+lString50
    static String lString500 = lString100+lString100+lString100+lString100+lString100
    static String lString1000 = lString500+lString500


    static void assertFieldValidationExpectations(Object domainObject, fieldName, Boolean valid, String errorCode) {
        println(domainObject.dump())
        println("domainObject.errors[fieldName]=${domainObject.errors[fieldName]}")
        assert domainObject.errors[fieldName] == errorCode
        assert domainObject.hasErrors() == !valid
        assert domainObject.errors.hasFieldErrors(fieldName) == !valid
    }
}
