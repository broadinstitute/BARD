package bard.core.helper

import org.apache.commons.lang3.time.StopWatch

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
    public void stopStopWatch(StopWatch sw, String loggingString) {
        sw.stop()
        Date now = new Date()
        Map loggingMap = [time: now.format('MM/dd/yyyy  HH:mm:ss.S'), responseTimeInMilliSeconds: sw.time, info: loggingString]
        Date start = new Date(sw.getStartTime())
        String logInfo = "start:\t${start.format('MM/dd/yyyy  HH:mm:ss.S')}\tduration:\t${sw.toString()}\t${loggingString}"
        log.info(logInfo)
    }

    /**
     * Stops the stop-watch, logs the time and an exception message.
     *
     * @param sw
     * @param loggingString
     * @param throwable
     */
    public void stopStopWatchError(StopWatch sw, String loggingString, Throwable throwable) {
        sw.stop()
        Date now = new Date()
        Map loggingMap = [time: now.format('MM/dd/yyyy  HH:mm:ss.S'), responseTimeInMilliSeconds: sw.time, info: loggingString]
        Date start = new Date(sw.getStartTime())
        String logInfo = "start:\t${start.format('MM/dd/yyyy  HH:mm:ss.S')}\tduration:\t${sw.toString()}\t${loggingString}"
        log.error(logInfo, throwable)
    }
}
