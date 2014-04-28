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
    static String cleanup(String incoming) {
        String returnValue = ""
        if (incoming) {
            returnValue =  incoming.replace("'", "\\'")
        }
        return returnValue
    }


    static String cleanupForHTML(String incoming) {
        String returnValue = ""
        if (incoming) {
            returnValue =  incoming.replace("'", "\'").replace("\"", "&quot;")
        }
        return returnValue
    }

    static String cleanup(Long incoming) {
        if (incoming) {
            return incoming.toString()
        }
        return ""
    }


}
