package bard.db.dictionary

import org.apache.commons.lang.StringUtils

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
//        println(domainObject.dump())
        assert errorCode == (domainObject.errors[fieldName]?.codes as List)?.find {it == errorCode}
        assert domainObject.hasErrors() == !valid
        assert domainObject.errors.hasFieldErrors(fieldName) == !valid
    }

    /**
     * quick utility method to create strings of a given length
     * @param length
     * @return a string where size() == length
     */
    static String createString(int length) {
        return createString(length, 'a')
    }

    /**
     * quick utility method to create strings of a given length with a given char
     * @param length
     * @return a string where size() == length
     */
    static String createString(int length, String aChar) {
        assert length >= 0
        assert StringUtils.isNotBlank(aChar)
        StringBuffer sB = new StringBuffer()
        length.times {sB.append(aChar)}
        return sB.toString()
    }


}
