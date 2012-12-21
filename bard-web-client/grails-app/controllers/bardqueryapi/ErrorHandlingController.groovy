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

