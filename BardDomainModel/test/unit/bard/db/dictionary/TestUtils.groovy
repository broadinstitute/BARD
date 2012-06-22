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
    final static String lString40 = lString10+lString10+lString10+lString10
    final static String lString50 = lString40+lString10
    final static String lString100 = lString50+lString50
    final static String lString128 = lString100+lString10+lString10+"12345678"
    final static String lString129 =  lString100+lString10+lString10+"123456789"
    final static String lString256 =  lString128+lString128
    final static String lString500 = lString100+lString100+lString100+lString100+lString100
    final static String lString1000 = lString500+lString500


    static void assertFieldValidationExpectations(Object domainObject, fieldName, Boolean valid, String errorCode) {
//        println("domainObject.errors[fieldName]=${domainObject.errors[fieldName]}")
        assert domainObject.errors[fieldName] == errorCode
        assert domainObject.hasErrors() == !valid
        assert domainObject.errors.hasFieldErrors(fieldName) == !valid
    }

  static String varlength( int x ) {
      def a = new StringBuffer()
      x.times{a.append("a")}
      a.toString()
  }



}
