package bard

import org.apache.log4j.MDC

class StashUrlFilters {
    public static final String REQUEST_KEY = "request";

    def filters = {
        all(uri: '/**') {
            before = {
                MDC.put(REQUEST_KEY, "${request.getMethod()} ${request.getRequestURI()}");
            }

            afterView = { Exception e ->
                MDC.remove(REQUEST_KEY);
            }
        }
    }
}
