package bard.core.helper

import org.apache.commons.lang3.time.StopWatch
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

class LoggerService {
    def transactional=false
    /**
     * Start the stop-watch that measures network traffic time for any of NCGC API.
     * @return StopWatch
     */
    public StopWatch startStopWatch() {
        StopWatch sw = new StopWatch()
        sw.start()
        return sw
    }

    /**
     * Stop the stop-watch and log the time.
     * @param sw
     */
    public void stopStopWatch(StopWatch sw, HttpStatus status, HttpMethod method, String loggingString) {
        sw.stop()
        Date start = new Date(sw.getStartTime())
        Date end = new Date(sw.getStartTime() + sw.getTime())
        String message = createMessage(start, end, sw.toString(), status, method, loggingString)
        log.info(message)
    }

    /**
     * Stops the stop-watch, logs the time and an exception message.
     *
     * @param sw
     * @param loggingString
     * @param throwable
     */
    public void stopStopWatchError(StopWatch sw, HttpStatus status, HttpMethod method, String loggingString, Throwable throwable) {
        sw.stop()
        Date start = new Date(sw.getStartTime())
        Date end = new Date(sw.getStartTime() + sw.getTime())
        String message = createMessage(start, end, sw.toString(), status, method, loggingString)
        log.error(message, throwable)
    }

    private static String createMessage(Date start, Date end, String duration, HttpStatus status, HttpMethod method, String loggingString) {
        String message = "s: ${start.format('HH:mm:ss.SSS')} e: ${end.format('HH:mm:ss.SSS')} d: ${duration} ${method.toString()} ";
        if (status != null) {
            message += "[${status.toString()} - ${status.reasonPhrase}] "
        }
        message += "${loggingString}"
        return message
    }
}
