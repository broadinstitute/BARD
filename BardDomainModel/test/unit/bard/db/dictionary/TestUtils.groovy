package bard.db.dictionary

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 6/22/12
 * Time: 9:07 AM
 * To change this template use File | Settings | File Templates.
 */
class TestUtils {


    static void assertFieldValidationExpectations(Object domainObject, fieldName, Boolean valid, String errorCode) {
//        println("domainObject.errors[fieldName]=${domainObject.errors[fieldName]}")
        //println((domainObject.errors[fieldName]?.codes as List))
        assert errorCode ==  (domainObject.errors[fieldName]?.codes as List)?.find{it == errorCode}
        println(domainObject.dump())
        assert domainObject.hasErrors() == !valid
        assert domainObject.errors.hasFieldErrors(fieldName) == !valid
    }

    /**
     * quick utility method to create strings of a given length
     * @param length
     * @return a string where size() == length
     */
    static String createString(int length) {
        assert length >= 0
        def a = new StringBuffer()
        length.times {a.append("a")}
        a.toString()
    }


}
