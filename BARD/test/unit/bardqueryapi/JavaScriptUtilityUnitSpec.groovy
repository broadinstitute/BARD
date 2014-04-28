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

import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 10/7/12
 * Time: 10:35 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class JavaScriptUtilityUnitSpec extends Specification {
    /**
     * {@link bardqueryapi.JavaScriptUtility.cleanup ( String )}
     */
    void "test cleanup with String arg"() {
        when:
        String cleanedUp = bardqueryapi.JavaScriptUtility.cleanup(cleanUpString)
        then:
        assert cleanedUp == expectedLabel
        where:
        label                        | cleanUpString | expectedLabel
        "Empty String"               | ""            | ""
        "Null String"                | null          | ""
        "String with no appostrophe" | "Stuff"       | "Stuff"
        "String with appostrophe"    | "Stuff's"     | "Stuff\\'s"
    }

    void "test cleanupForHTML with String arg"() {
        when:
        String cleanedUp = bardqueryapi.JavaScriptUtility.cleanupForHTML(cleanUpString)
        then:
        assert cleanedUp == expectedLabel
        where:
        label                        | cleanUpString                    | expectedLabel
        "Empty String"               | ""                               | ""
        "Null String"                | null                             | ""
        "String with no appostrophe" | "Stuff"                          | "Stuff"
        "String with appostrophe"    | "Stuff's"                        | "Stuff\'s"
        "String with quotation"      | """say "hello" Joe""".toString() | "say &quot;hello&quot; Joe"
    }


    /**
     * {@link bardqueryapi.JavaScriptUtility.cleanup ( Long )}
     */
    void "test cleanup with Null Long arg"() {
        given:
        final Long nullLong = null
        when:
        String cleanedUp = bardqueryapi.JavaScriptUtility.cleanup(nullLong)
        then:
        assert cleanedUp == ""
    }
    /**
     * {@link bardqueryapi.JavaScriptUtility.cleanup ( Long )}
     */
    void "test cleanup with Long arg #label"() {
        when:
        String cleanedUp = bardqueryapi.JavaScriptUtility.cleanup(cleanedUpLong)
        then:
        assert cleanedUp == expectedLabel
        where:
        label    | cleanedUpLong | expectedLabel
        "Null"   | null as Long  | ""
        "Zero"   | 0 as Long     | ""
        "A Long" | 20            | "20"
    }

}
