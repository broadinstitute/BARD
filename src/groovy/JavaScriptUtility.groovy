package bardqueryapi

/**
 * A place to put utility routines relevant to manipulating JavaScript
 */
class JavaScriptUtility {

    /**
     * Right now we have only this completely trivial JavaScript cleanup. We may well have more one day, however,
     * which is why it makes sense to have a method here instead of simply performing the substitution in line
     * in the GSP.  Should this functionality be put in a service?  I think not, since this isn't real business
     *  logics, and this way we don't have to inject the bean and all we want is a light weight utility call.
     * @param incoming
     * @return
     */
    static String cleanup (String incoming){
        if (incoming==null)
            return ""
        else
            return incoming.replace("'","\\'")
    }
    static String cleanup (Long incoming){
        return incoming?.toString()
    }



}
