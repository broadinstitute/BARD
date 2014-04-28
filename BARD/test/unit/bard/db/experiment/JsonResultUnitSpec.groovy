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

package bard.db.experiment

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectWriter
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 2/26/13
 * Time: 6:10 PM
 * To change this template use File | Settings | File Templates.
 */
class JsonResultUnitSpec extends Specification {
    String expected = """
{
  "sid" : 100,
  "rootElem" :
  [
    {
        "resultId" : 100,
        "resultTypeId" : 101,
        "statsModifierId" : 15,
        "resultType" : "AC50",
        "valueNum" : 10.0,
        "valueMin" : 0.0,
        "valueMax" : 1000.0,
        "replicateNumber" : 1,
        "valueDisplay" : "valueDisplay",
        "qualifier" : "=",
        "related" :
        [
            {
              "resultId" : 100,
              "resultTypeId" : 101,
              "statsModifierId" : 15,
              "resultType" : "AC50",
              "valueNum" : 10.0,
              "valueMin" : 0.0,
              "valueMax" : 1000.0,
              "replicateNumber" : 1,
              "valueDisplay" : "valueDisplay",
              "qualifier" : "=",
              "relationship" : "Derives"
            }
        ],
        "contextItems" :
        [
            {
              "itemId" : 100,
              "attribute" : "attribute",
              "attributeId": 53,
              "qualifier" : "<",
              "valueNum" : 102.0,
              "valueMin" : -200.0,
              "valueMax" : 200.0,
              "valueDisplay" : "value",
              "valueElementId" : 100
            }
        ]
    }
  ]
}
"""
    def 'test serialization'() {
        when:
        def contextItem = new JsonResultContextItem(qualifier: "<", valueMax: 200, valueMin: -200, valueNum: 102, valueDisplay: "value", itemId: 100, attribute: "attribute", attributeId: 53, valueElementId: 100);
        def child = new JsonResult(qualifier: "=", replicateNumber: 1, resultId: 100, resultTypeId: 101, resultType: "AC50", valueDisplay: "valueDisplay", valueMax: 1000, valueMin: 0, valueNum: 10, relationship: "Derives", statsModifierId: 15)
        def parent = new JsonResult(qualifier: "=", replicateNumber: 1, resultId: 100, resultTypeId: 101, resultType: "AC50", valueDisplay: "valueDisplay", valueMax: 1000, valueMin: 0, valueNum: 10, related: [child], contextItems: [contextItem], statsModifierId: 15)
        def substanceResults = new JsonSubstanceResults(sid: 100, rootElem: [parent])

        ObjectMapper mapper = new ObjectMapper()
        def serialized = mapper.writeValueAsString(substanceResults)

        // parse the json into simple java types (Map, List, primitives) and then do the comparison to compare insensitive
        // to key order and spacing
        def parsed = mapper.readValue(serialized, Object.class)
        def expectedAsJson = mapper.readValue(serialized, Object.class)

        then:
        parsed == expectedAsJson
    }
}
