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

package common.tests

import org.apache.commons.lang.BooleanUtils
import org.custommonkey.xmlunit.*
import org.springframework.core.io.Resource

import javax.xml.XMLConstants
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.Schema
import javax.xml.validation.SchemaFactory
import javax.xml.validation.Validator

/**
 * This class will not be packaged into a war file. We only need it for test purposes
 * See comments on top of XmlTestSamples.groovy
 */
class XmlTestAssertions {
    public static void assertResults(final String expectedResults, final String generatedResults) {
        XMLUnit.setIgnoreWhitespace(true)
        Diff xmlDiff = new Diff(expectedResults.trim(), generatedResults.trim())
        if (BooleanUtils.isFalse(xmlDiff.similar())) {
            println("expected: $expectedResults")
            println("actual: $generatedResults")
        }
        assert true == xmlDiff.similar()
    }
    /**
     * If you would like to ignore some attributes
     * For instance a LinkGenerator generates links without the port number in
     * an integration tests, but does so in a functional test
     * You might call this method to ignore the difference in the links generated
     * @param expectedResults
     * @param generatedResults
     */
    public static void assertResultsWithOverrideAttributes(final String expectedResults, final String generatedResults) {
        XMLUnit.setIgnoreWhitespace(true)
        Diff xmlDiff = new Diff(expectedResults, generatedResults)
        xmlDiff.overrideDifferenceListener(new DifferenceListener() {
            public int differenceFound(Difference difference) {
                if (difference.getId()
                        == DifferenceConstants.ATTR_VALUE_ID) {
                    return DifferenceListener.RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
                }
                return DifferenceListener.RETURN_ACCEPT_DIFFERENCE;
            }

            public void skippedComparison(org.w3c.dom.Node control, org.w3c.dom.Node test) {

            }
        });
        if (BooleanUtils.isFalse(xmlDiff.similar())) {
            println("expected: $expectedResults")
            println("actual: $generatedResults")
        }
        assert true == xmlDiff.similar()
    }

    /**
     * @param schemaResource a org.springframework.core.io.Resource pointing to the schema
     * @param xmlToValidate the xml as a string
     */
    static void validate(Resource schemaResource, String xmlToValidate) {
        final SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
        final Schema schema = factory.newSchema(schemaResource.getFile())
        final Validator validator = schema.newValidator()
        validator.validate(new StreamSource(new StringReader(xmlToValidate)))
    }
}
