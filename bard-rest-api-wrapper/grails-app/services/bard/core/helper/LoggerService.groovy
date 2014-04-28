/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
