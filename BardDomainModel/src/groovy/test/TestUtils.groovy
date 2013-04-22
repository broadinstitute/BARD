package test

import org.apache.commons.lang.StringUtils
import org.springframework.validation.FieldError
import org.apache.commons.lang.BooleanUtils

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 6/22/12
 * Time: 9:07 AM
 * To change this template use File | Settings | File Templates.
 */
class TestUtils {

    /**
     * Group of assertions to check for field validations to ensure that a validation
     * passes or fails for the expected reasons
     * @param domainObject
     * @param fieldName
     * @param valid
     * @param errorCode
     */
    static void assertFieldValidationExpectations(Object domainObject, String fieldName, Boolean valid, String errorCode) {
        String foundErrorCode = getErrorCode(domainObject, fieldName, errorCode)
        logUnexpectedValidationErrors(domainObject, valid, errorCode, foundErrorCode)
        assert errorCode == foundErrorCode
        assert domainObject.hasErrors() == !valid
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

    /**
     * // TODO look into why this is different and which is "correct" and fix or submit a bug
     *  vanilla grails domainObject.errors returns GrailsMockErrors which in turn returns
     *  String error codes for validation failures.
     *
     *  When using the build-test-data plugin @Build annotation domainObject.errors returns
     *  ValidationErrors which in term returns a FieldError and has a list of codes
     *  so we try to find one that matches and return it
     * @param domainObject
     * @param fieldName
     * @param errorCode
     * @return a String representing the error code
     */
    private static String getErrorCode(domainObject, String fieldName, String errorCode) {

        String foundErrorCode = null
        if (domainObject.errors instanceof org.grails.datastore.mapping.validation.ValidationErrors ||
                domainObject.errors instanceof grails.validation.ValidationErrors) {
            FieldError fieldError = domainObject.errors[fieldName]
            foundErrorCode = fieldError?.codes?.find {it == errorCode}
        }
        else if (domainObject.errors instanceof org.codehaus.groovy.grails.plugins.testing.GrailsMockErrors) {
            foundErrorCode = domainObject.errors[fieldName]
        }
        return foundErrorCode
    }

    /**
     * print any unexpected errors to make tracking them down easier
     *
     * @param domainObject
     * @param valid
     */

    /**
     * print errors when we didn't expect any validation errors or
     * when we were looking for a specific failure and didn't find it
     * in order to may fixing it easier
     *
     * @param domainObject
     * @param valid
     * @param errorCode
     * @param foundErrorCode
     */
    private static void logUnexpectedValidationErrors(domainObject, boolean valid, String errorCode, String foundErrorCode) {
        if (BooleanUtils.isFalse(domainObject.hasErrors() == !valid) ||
           errorCode != foundErrorCode) {
            println("**************** Unexpected Validation Errors **********************")
            println(domainObject.errors)
            println("********************************************************************")
        }
    }


}
