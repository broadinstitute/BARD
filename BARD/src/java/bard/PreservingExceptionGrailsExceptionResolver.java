package bard;

import org.codehaus.groovy.grails.web.errors.GrailsExceptionResolver;
import org.codehaus.groovy.grails.web.servlet.mvc.exceptions.GrailsMVCException;
import org.codehaus.groovy.runtime.InvokerInvocationException;

import java.lang.reflect.InvocationTargetException;

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 12/4/13
 * Time: 4:52 PM
 *
 * Identical behavior to the default GrailsExceptionResolver, except does not throw out nested exceptions.
 */
public class PreservingExceptionGrailsExceptionResolver extends GrailsExceptionResolver {

    public PreservingExceptionGrailsExceptionResolver() {
        super();
    }

    // instead of returning the root exception, only unwrap the outermost layers
    protected Exception findWrappedException(Exception e) {

        while ((e instanceof InvokerInvocationException)||(e instanceof InvocationTargetException)||(e instanceof GrailsMVCException)) {
            Throwable t = e.getCause();
            if (t instanceof Exception) {
                e = (Exception) t;
            } else {
                break;
            }
        }
        return e;
    }
}
