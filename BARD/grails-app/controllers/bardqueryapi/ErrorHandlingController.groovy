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

package bardqueryapi

import java.util.regex.Matcher
import java.util.regex.Pattern

@Mixin(InetAddressUtil)
class ErrorHandlingController {

    def handleJsErrors(String error, String url, String line, String browser) {
        String ipAddress = getAddressFromRequest()
        StringBuilder errorMessage = new StringBuilder()
        errorMessage.append("IP:${ipAddress} Message:${error} on line ${line} using browser:${browser} at URL:${url}")


        log.error(errorMessage.toString())
        render text: errorMessage.toString(), contentType: 'text/plain'
    }
}

/**
 * Copied from http://rod.vagg.org/2011/07/handling-x-forwarded-for-in-java-and-tomcat/
 */
class InetAddressUtil {
    private static final String IP_ADDRESS_REGEX = "([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3})";
    private static final String PRIVATE_IP_ADDRESS_REGEX = "(^127\\.0\\.0\\.1)|(^10\\.)|(^172\\.1[6-9]\\.)|(^172\\.2[0-9]\\.)|(^172\\.3[0-1]\\.)|(^192\\.168\\.)";
    private static final Pattern IP_ADDRESS_PATTERN = Pattern.compile(IP_ADDRESS_REGEX)
    private static final Pattern PRIVATE_IP_ADDRESS_PATTERN = Pattern.compile(PRIVATE_IP_ADDRESS_REGEX)

    public String findNonPrivateIpAddress(String s) {
        final Matcher matcher = IP_ADDRESS_PATTERN.matcher(s);
        while (matcher.find()) {
            if (!PRIVATE_IP_ADDRESS_PATTERN.matcher(matcher.group(0)).find()) {
                return matcher.group(0);
            }
            matcher.region(matcher.end(), s.length());
        }
        return "";
    }

    public String getAddressFromRequest() {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && (forwardedFor = findNonPrivateIpAddress(forwardedFor)) != null)
            return forwardedFor;
        return request.getRemoteAddr();
    }
    public String getUserIpAddress(String userName){
        String ipAddress = getAddressFromRequest()
        StringBuilder message = new StringBuilder()
        message.append("  user:")
        message.append(userName)
        message.append("  IP:")
        message.append(ipAddress)
        return message.toString()

    }

//    public String getHostnameFromRequest() {
//        String addr = getAddressFromRequest(request);
//        try {
//            return Inet4Address.getByName(addr).getHostName();
//        } catch (Exception e) {
//        }
//        return addr;
//    }
//
//    public InetAddress getInet4AddressFromRequest () throws UnknownHostException {
//        return Inet4Address.getByName(getAddressFromRequest(request));
//    }
}

