import org.apache.log4j.Appender
import org.apache.log4j.Logger
import org.apache.log4j.filter.DenyAllFilter
import org.apache.log4j.filter.ExpressionFilter

class BootStrap {

    def init = { servletContext ->
        addLog4jFilters()
    }
    def destroy = {
    }

    /**
     *
     * Allows us to exclude some errors from being sent by email
     * Allows logging of some specific exceptions to specific files
     */
    void addLog4jFilters() {
        Enumeration appenders = ((Logger) Logger.getLogger(BootStrap.class)).getRootLogger().getAllAppenders()
        while (appenders.hasMoreElements()) {
            Appender appender = (Appender) appenders.nextElement();
            if (appender.name == "mySQLAppender") {
                handleMySQLErrorFileLogging(appender)
            } else {
                final List<ExpressionFilter> excludeFilters = getSmtpExcludeFilters()
                excludeFilters.each { excludeFilter ->
                    appender.addFilter(excludeFilter)
                }
            }
        }
    }

    //Handle the BoneCP MySQL errors
    void handleMySQLErrorFileLogging(Appender appender) {

        ExpressionFilter boneCPFilter = new ExpressionFilter()
        boneCPFilter.expression =
            "THREAD ~= 'BoneCP-keep-alive-scheduler'"
        boneCPFilter.acceptOnMatch = true
        boneCPFilter.activateOptions()
        appender.addFilter(boneCPFilter)

        //We use the following filter to deny everything but the one we defined above
        DenyAllFilter denyAllFilter = new DenyAllFilter()
        appender.addFilter(denyAllFilter)
    }

    //add these filters so that we do not send email when they occur
    List<ExpressionFilter> getSmtpExcludeFilters() {
        List<ExpressionFilter> excludeFilters = []

        ExpressionFilter boneCPFilter = new ExpressionFilter()
        boneCPFilter.expression =
            "THREAD ~= 'BoneCP-keep-alive-scheduler'"
        boneCPFilter.acceptOnMatch = true
        boneCPFilter.activateOptions()
        excludeFilters.add(boneCPFilter)
        return excludeFilters
    }
}
