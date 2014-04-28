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

package dataexport.util

import javax.servlet.http.HttpServletRequest

class AuthenticationService {

    def grailsApplication

    /**
     * Used by the DataExportFilters filter to check authentication of the incoming request
     * against an IP address whitelist and an APIKEY hash code.
     *
     * Get the APIKEY from the incoming request's header (custom key).
     *
     * @param request
     * @return
     */
    Boolean authenticate(HttpServletRequest request) {
        String apiKey = grailsApplication.config.dataexport.externalapplication.apiKey.hashed
        String apiKeyHeader = grailsApplication.config.dataexport.externalapplication.apiKey.header
        String requestApiKey = request.getHeader(apiKeyHeader)
        final String requestParamsString = "URL: '${request.getRequestURL()}'; Remote address: '${request.getRemoteAddr()}'"

        if (apiKey != requestApiKey) {
            log.info("Failed authentication - invalid api-key: ${requestParamsString}")
            return false
        }
        log.info("Successful authentication: ${requestParamsString}")
        return true
    }

    /**
     * Compares two IP addresses to check if they match. Accepts wildcard ('*') as part(s) of an IP address.
     * For example, '1.*.3.4' would compare true both with '1.2.3.4' and '1.5.3.4'
     * There is also a support for IPv6 with wildcards (e.g., '1:2:3:4:5:6:7:*')
     *
     * @param rhs
     * @param lhs
     * @return
     */
    Boolean doIpAddressesMatch(String rhs, String lhs) {
        String[] rhsStringArray = rhs.split(/[\.:]/) //IPv6 uses ':' instead of '.'
        String[] lhsStringArray = lhs.split(/[\.:]/) //IPv6 uses ':' instead of '.'
        if (rhsStringArray.size() != lhsStringArray.size())
            return false

        for (int i in 0..<rhsStringArray.size()) { //IPv6 allows larger UP address fields. For example, when running locally we receive 0:0:0:0:0:0:0:1 as the remote address.
            if (rhsStringArray[i] == '*' || lhsStringArray[i] == '*')
                continue

            Integer lhsIntVal = new Integer(lhsStringArray[i])
            Integer rhsIntVal = new Integer(rhsStringArray[i])
            if (rhsIntVal != lhsIntVal)
                return false
        }

        return true
    }
}
