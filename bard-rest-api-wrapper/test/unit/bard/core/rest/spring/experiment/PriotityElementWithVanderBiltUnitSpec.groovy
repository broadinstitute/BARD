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

package bard.core.rest.spring.experiment

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import javax.servlet.ServletContext
import org.codehaus.groovy.grails.commons.spring.GrailsWebApplicationContext
import bard.core.rest.spring.DictionaryRestService
import org.codehaus.groovy.grails.web.context.ServletContextHolder

@Unroll
class PriotityElementWithVanderBiltUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()
    @Shared
    String VANDER_BILT_EXAMPLE_1 = '''
    {
     "displayName":"AvgGluPotency",
     "dictElemId":961,
     "responseUnit":"um",
     "testConcUnit":"uM",
     "value":"4.09e-007",
     "primaryElements":
     [
        {
           "displayName":"GluPotencyExperiment1",
           "dictElemId":961,
           "value":"4.04e-007",
           "concResponseSeries":{
              "testConcUnit":"uM",
              "crSeriesDictId":1016,
              "concRespParams":{
                 "s0":null,
                 "sInf":null,
                 "hillCoef":null,
                 "logEc50":-6.393618634889395
              },
              "concRespPoints":
              [
                 {
                    "testConc":1000.0,
                    "value":"104.181",
                    "childElements":
                    [
                       {
                          "displayName":"Rep2ForExperiment3_1000_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":1000.0,
                          "value":"105.693"
                       },
                       {
                          "displayName":"Rep1ForExperiment3_1000_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":1000.0,
                          "value":"102.669"
                       },
                       {
                          "displayName":"StddevForExperiment3_1000uM",
                          "dictElemId":613,
                          "testConcUnit":"uM",
                          "testConc":1000.0,
                          "value":"2.13829"
                       }
                    ]
                 },
                 {
                    "testConc":100.0,
                    "value":"115.726",
                    "childElements":
                    [
                       {
                          "displayName":"Rep2ForExperiment3_100_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":100.0,
                          "value":"113.668"
                       },
                       {
                          "displayName":"Rep1ForExperiment3_100_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":100.0,
                          "value":"117.784"
                       },
                       {
                          "displayName":"StddevForExperiment3_100uM",
                          "dictElemId":613,
                          "testConcUnit":"uM",
                          "testConc":100.0,
                          "value":"2.91045"
                       }
                    ]
                 },
                 {
                    "testConc":31.6,
                    "value":"124.864",
                    "childElements":
                    [
                       {
                          "displayName":"Rep2ForExperiment3_31.6_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":31.6,
                          "value":"128.757"
                       },
                       {
                          "displayName":"Rep1ForExperiment3_31.6_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":31.6,
                          "value":"120.971"
                       },
                       {
                          "displayName":"StddevForExperiment3_31.6uM",
                          "dictElemId":613,
                          "testConcUnit":"uM",
                          "testConc":31.6,
                          "value":"5.50553"
                       }
                    ]
                 },
                 {
                    "testConc":10.0,
                    "value":"122.099",
                    "childElements":
                    [
                       {
                          "displayName":"Rep2ForExperiment3_10.0_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":10.0,
                          "value":"117.857"
                       },
                       {
                          "displayName":"Rep1ForExperiment3_10.0_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":10.0,
                          "value":"126.34"
                       },
                       {
                          "displayName":"StddevForExperiment3_10.0uM",
                          "dictElemId":613,
                          "testConcUnit":"uM",
                          "testConc":10.0,
                          "value":"5.99839"
                       }
                    ]
                 },
                 {
                    "testConc":3.16,
                    "value":"119.399",
                    "childElements":
                    [
                       {
                          "displayName":"Rep2ForExperiment3_3.16_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":3.16,
                          "value":"122.892"
                       },
                       {
                          "displayName":"Rep1ForExperiment3_3.16_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":3.16,
                          "value":"115.907"
                       },
                       {
                          "displayName":"StddevForExperiment3_3.16uM",
                          "dictElemId":613,
                          "testConcUnit":"uM",
                          "testConc":3.16,
                          "value":"4.93914"
                       }
                    ]
                 },
                 {
                    "testConc":1.0,
                    "value":"101.104",
                    "childElements":
                    [
                       {
                          "displayName":"Rep2ForExperiment3_1.00_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":1.0,
                          "value":"99.219"
                       },
                       {
                          "displayName":"Rep1ForExperiment3_1.00_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":1.0,
                          "value":"102.988"
                       },
                       {
                          "displayName":"StddevForExperiment3_1.00uM",
                          "dictElemId":613,
                          "testConcUnit":"uM",
                          "testConc":1.0,
                          "value":"2.66509"
                       }
                    ]
                 },
                 {
                    "testConc":0.316,
                    "value":"50.192",
                    "childElements":
                    [
                       {
                          "displayName":"Rep2ForExperiment3_0.316_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":0.316,
                          "value":"47.263"
                       },
                       {
                          "displayName":"Rep1ForExperiment3_0.316_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":0.316,
                          "value":"53.121"
                       },
                       {
                          "displayName":"StddevForExperiment3_0.316uM",
                          "dictElemId":613,
                          "testConcUnit":"uM",
                          "testConc":0.316,
                          "value":"4.14223"
                       }
                    ]
                 },
                 {
                    "testConc":0.1,
                    "value":"21.6415",
                    "childElements":
                    [
                       {
                          "displayName":"Rep2ForExperiment3_0.100_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":0.1,
                          "value":"23.679"
                       },
                       {
                          "displayName":"Rep1ForExperiment3_0.100_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":0.1,
                          "value":"19.604"
                       },
                       {
                          "displayName":"StddevForExperiment3_0.100uM",
                          "dictElemId":613,
                          "testConcUnit":"uM",
                          "testConc":0.1,
                          "value":"2.88146"
                       }
                    ]
                 },
                 {
                    "testConc":0.01,
                    "value":"8.129",
                    "childElements":[
                       {
                          "displayName":"Rep2ForExperiment3_0.0100_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":0.01,
                          "value":"12.104"
                       },
                       {
                          "displayName":"Rep1ForExperiment3_0.0100_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":0.01,
                          "value":"4.154"
                       },
                       {
                          "displayName":"StddevForExperiment3_0.0100uM",
                          "dictElemId":613,
                          "testConcUnit":"uM",
                          "testConc":0.01,
                          "value":"5.6215"
                       }
                    ]
                 },
                 {
                    "testConc":0.001,
                    "value":"4.799",
                    "childElements":[
                       {
                          "displayName":"Rep2ForExperiment3_0.00100_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":0.001,
                          "value":"5.02"
                       },
                       {
                          "displayName":"Rep1ForExperiment3_0.00100_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":0.001,
                          "value":"4.578"
                       },
                       {
                          "displayName":"StddevForExperiment3_0.00100uM",
                          "dictElemId":613,
                          "testConcUnit":"uM",
                          "testConc":0.001,
                          "value":"0.312541"
                       }
                    ]
                 }
              ]
           }
        },
        {
           "displayName":"GluPotencyExperiment2",
           "dictElemId":961,
           "value":"4.32e-007",
           "concResponseSeries":{
              "testConcUnit":"uM",
              "crSeriesDictId":1016,
              "concRespParams":{
                 "s0":null,
                 "sInf":null,
                 "hillCoef":null,
                 "logEc50":-6.364516253185088
              },
              "concRespPoints":[
                 {
                    "testConc":1000.0,
                    "value":"104.181",
                    "childElements":[
                       {
                          "displayName":"Rep2ForExperiment3_1000_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":1000.0,
                          "value":"105.693"
                       },
                       {
                          "displayName":"Rep1ForExperiment3_1000_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":1000.0,
                          "value":"102.669"
                       },
                       {
                          "displayName":"StddevForExperiment3_1000uM",
                          "dictElemId":613,
                          "testConcUnit":"uM",
                          "testConc":1000.0,
                          "value":"2.13829"
                       }
                    ]
                 },
                 {
                    "testConc":100.0,
                    "value":"115.726",
                    "childElements":[
                       {
                          "displayName":"Rep2ForExperiment3_100_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":100.0,
                          "value":"113.668"
                       },
                       {
                          "displayName":"Rep1ForExperiment3_100_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":100.0,
                          "value":"117.784"
                       },
                       {
                          "displayName":"StddevForExperiment3_100uM",
                          "dictElemId":613,
                          "testConcUnit":"uM",
                          "testConc":100.0,
                          "value":"2.91045"
                       }
                    ]
                 },
                 {
                    "testConc":31.6,
                    "value":"124.864",
                    "childElements":[
                       {
                          "displayName":"Rep2ForExperiment3_31.6_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":31.6,
                          "value":"128.757"
                       },
                       {
                          "displayName":"Rep1ForExperiment3_31.6_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":31.6,
                          "value":"120.971"
                       },
                       {
                          "displayName":"StddevForExperiment3_31.6uM",
                          "dictElemId":613,
                          "testConcUnit":"uM",
                          "testConc":31.6,
                          "value":"5.50553"
                       }
                    ]
                 },
                 {
                    "testConc":10.0,
                    "value":"122.099",
                    "childElements":[
                       {
                          "displayName":"Rep2ForExperiment3_10.0_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":10.0,
                          "value":"117.857"
                       },
                       {
                          "displayName":"Rep1ForExperiment3_10.0_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":10.0,
                          "value":"126.34"
                       },
                       {
                          "displayName":"StddevForExperiment3_10.0uM",
                          "dictElemId":613,
                          "testConcUnit":"uM",
                          "testConc":10.0,
                          "value":"5.99839"
                       }
                    ]
                 },
                 {
                    "testConc":3.16,
                    "value":"119.399",
                    "childElements":[
                       {
                          "displayName":"Rep2ForExperiment3_3.16_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":3.16,
                          "value":"122.892"
                       },
                       {
                          "displayName":"Rep1ForExperiment3_3.16_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":3.16,
                          "value":"115.907"
                       },
                       {
                          "displayName":"StddevForExperiment3_3.16uM",
                          "dictElemId":613,
                          "testConcUnit":"uM",
                          "testConc":3.16,
                          "value":"4.93914"
                       }
                    ]
                 },
                 {
                    "testConc":1.0,
                    "value":"101.104",
                    "childElements":[
                       {
                          "displayName":"Rep2ForExperiment3_1.00_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":1.0,
                          "value":"99.219"
                       },
                       {
                          "displayName":"Rep1ForExperiment3_1.00_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":1.0,
                          "value":"102.988"
                       },
                       {
                          "displayName":"StddevForExperiment3_1.00uM",
                          "dictElemId":613,
                          "testConcUnit":"uM",
                          "testConc":1.0,
                          "value":"2.66509"
                       }
                    ]
                 },
                 {
                    "testConc":0.316,
                    "value":"50.192",
                    "childElements":[
                       {
                          "displayName":"Rep2ForExperiment3_0.316_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":0.316,
                          "value":"47.263"
                       },
                       {
                          "displayName":"Rep1ForExperiment3_0.316_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":0.316,
                          "value":"53.121"
                       },
                       {
                          "displayName":"StddevForExperiment3_0.316uM",
                          "dictElemId":613,
                          "testConcUnit":"uM",
                          "testConc":0.316,
                          "value":"4.14223"
                       }
                    ]
                 },
                 {
                    "testConc":0.1,
                    "value":"21.6415",
                    "childElements":[
                       {
                          "displayName":"Rep2ForExperiment3_0.100_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":0.1,
                          "value":"23.679"
                       },
                       {
                          "displayName":"Rep1ForExperiment3_0.100_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":0.1,
                          "value":"19.604"
                       },
                       {
                          "displayName":"StddevForExperiment3_0.100uM",
                          "dictElemId":613,
                          "testConcUnit":"uM",
                          "testConc":0.1,
                          "value":"2.88146"
                       }
                    ]
                 },
                 {
                    "testConc":0.01,
                    "value":"8.129",
                    "childElements":[
                       {
                          "displayName":"Rep2ForExperiment3_0.0100_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":0.01,
                          "value":"12.104"
                       },
                       {
                          "displayName":"Rep1ForExperiment3_0.0100_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":0.01,
                          "value":"4.154"
                       },
                       {
                          "displayName":"StddevForExperiment3_0.0100uM",
                          "dictElemId":613,
                          "testConcUnit":"uM",
                          "testConc":0.01,
                          "value":"5.6215"
                       }
                    ]
                 },
                 {
                    "testConc":0.001,
                    "value":"4.799",
                    "childElements":[
                       {
                          "displayName":"Rep2ForExperiment3_0.00100_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":0.001,
                          "value":"5.02"
                       },
                       {
                          "displayName":"Rep1ForExperiment3_0.00100_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":0.001,
                          "value":"4.578"
                       },
                       {
                          "displayName":"StddevForExperiment3_0.00100uM",
                          "dictElemId":613,
                          "testConcUnit":"uM",
                          "testConc":0.001,
                          "value":"0.312541"
                       }
                    ]
                 }
              ]
           }
        },
        {
           "displayName":"GluPotencyExperiment3",
           "dictElemId":961,
           "value":"3.92e-007",
           "concResponseSeries":{
              "testConcUnit":"uM",
              "crSeriesDictId":1016,
              "concRespParams":{
                 "s0":null,
                 "sInf":null,
                 "hillCoef":null,
                 "logEc50":-6.406713932979542
              },
              "concRespPoints":[
                 {
                    "testConc":1000.0,
                    "value":"104.181",
                    "childElements":[
                       {
                          "displayName":"Rep2ForExperiment3_1000_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":1000.0,
                          "value":"105.693"
                       },
                       {
                          "displayName":"Rep1ForExperiment3_1000_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":1000.0,
                          "value":"102.669"
                       },
                       {
                          "displayName":"StddevForExperiment3_1000uM",
                          "dictElemId":613,
                          "testConcUnit":"uM",
                          "testConc":1000.0,
                          "value":"2.13829"
                       }
                    ]
                 },
                 {
                    "testConc":100.0,
                    "value":"115.726",
                    "childElements":[
                       {
                          "displayName":"Rep2ForExperiment3_100_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":100.0,
                          "value":"113.668"
                       },
                       {
                          "displayName":"Rep1ForExperiment3_100_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":100.0,
                          "value":"117.784"
                       },
                       {
                          "displayName":"StddevForExperiment3_100uM",
                          "dictElemId":613,
                          "testConcUnit":"uM",
                          "testConc":100.0,
                          "value":"2.91045"
                       }
                    ]
                 },
                 {
                    "testConc":31.6,
                    "value":"124.864",
                    "childElements":[
                       {
                          "displayName":"Rep2ForExperiment3_31.6_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":31.6,
                          "value":"128.757"
                       },
                       {
                          "displayName":"Rep1ForExperiment3_31.6_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":31.6,
                          "value":"120.971"
                       },
                       {
                          "displayName":"StddevForExperiment3_31.6uM",
                          "dictElemId":613,
                          "testConcUnit":"uM",
                          "testConc":31.6,
                          "value":"5.50553"
                       }
                    ]
                 },
                 {
                    "testConc":10.0,
                    "value":"122.099",
                    "childElements":[
                       {
                          "displayName":"Rep2ForExperiment3_10.0_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":10.0,
                          "value":"117.857"
                       },
                       {
                          "displayName":"Rep1ForExperiment3_10.0_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":10.0,
                          "value":"126.34"
                       },
                       {
                          "displayName":"StddevForExperiment3_10.0uM",
                          "dictElemId":613,
                          "testConcUnit":"uM",
                          "testConc":10.0,
                          "value":"5.99839"
                       }
                    ]
                 },
                 {
                    "testConc":3.16,
                    "value":"119.399",
                    "childElements":[
                       {
                          "displayName":"Rep2ForExperiment3_3.16_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":3.16,
                          "value":"122.892"
                       },
                       {
                          "displayName":"Rep1ForExperiment3_3.16_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":3.16,
                          "value":"115.907"
                       },
                       {
                          "displayName":"StddevForExperiment3_3.16uM",
                          "dictElemId":613,
                          "testConcUnit":"uM",
                          "testConc":3.16,
                          "value":"4.93914"
                       }
                    ]
                 },
                 {
                    "testConc":1.0,
                    "value":"101.104",
                    "childElements":[
                       {
                          "displayName":"Rep2ForExperiment3_1.00_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":1.0,
                          "value":"99.219"
                       },
                       {
                          "displayName":"Rep1ForExperiment3_1.00_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":1.0,
                          "value":"102.988"
                       },
                       {
                          "displayName":"StddevForExperiment3_1.00uM",
                          "dictElemId":613,
                          "testConcUnit":"uM",
                          "testConc":1.0,
                          "value":"2.66509"
                       }
                    ]
                 },
                 {
                    "testConc":0.316,
                    "value":"50.192",
                    "childElements":[
                       {
                          "displayName":"Rep2ForExperiment3_0.316_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":0.316,
                          "value":"47.263"
                       },
                       {
                          "displayName":"Rep1ForExperiment3_0.316_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":0.316,
                          "value":"53.121"
                       },
                       {
                          "displayName":"StddevForExperiment3_0.316uM",
                          "dictElemId":613,
                          "testConcUnit":"uM",
                          "testConc":0.316,
                          "value":"4.14223"
                       }
                    ]
                 },
                 {
                    "testConc":0.1,
                    "value":"21.6415",
                    "childElements":[
                       {
                          "displayName":"Rep2ForExperiment3_0.100_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":0.1,
                          "value":"23.679"
                       },
                       {
                          "displayName":"Rep1ForExperiment3_0.100_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":0.1,
                          "value":"19.604"
                       },
                       {
                          "displayName":"StddevForExperiment3_0.100uM",
                          "dictElemId":613,
                          "testConcUnit":"uM",
                          "testConc":0.1,
                          "value":"2.88146"
                       }
                    ]
                 },
                 {
                    "testConc":0.01,
                    "value":"8.129",
                    "childElements":[
                       {
                          "displayName":"Rep2ForExperiment3_0.0100_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":0.01,
                          "value":"12.104"
                       },
                       {
                          "displayName":"Rep1ForExperiment3_0.0100_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":0.01,
                          "value":"4.154"
                       },
                       {
                          "displayName":"StddevForExperiment3_0.0100uM",
                          "dictElemId":613,
                          "testConcUnit":"uM",
                          "testConc":0.01,
                          "value":"5.6215"
                       }
                    ]
                 },
                 {
                    "testConc":0.001,
                    "value":"4.799",
                    "childElements":
                    [
                       {
                          "displayName":"Rep2ForExperiment3_0.00100_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":0.001,
                          "value":"5.02"
                       },
                       {
                          "displayName":"Rep1ForExperiment3_0.00100_uM",
                          "dictElemId":1016,
                          "testConcUnit":"uM",
                          "testConc":0.001,
                          "value":"4.578"
                       },
                       {
                          "displayName":"StddevForExperiment3_0.00100uM",
                          "dictElemId":613,
                          "testConcUnit":"uM",
                          "testConc":0.001,
                          "value":"0.312541"
                       }
                    ]
                 }
              ]
           }
        }
     ],
     "childElements":
     [
        {
           "displayName":"StddevGluPotency",
           "dictElemId":613,
           "value":"2.06e-008"
        },
        {
           "displayName":"SEMGluPotency",
           "dictElemId":1335,
           "value":"1.19e-008"
        }
     ]
  }
'''
    ServletContext servletContext
    GrailsWebApplicationContext ctx
    DictionaryRestService dictionaryRestService
    void setup() {
        servletContext = Mock(ServletContext)
        ServletContextHolder.metaClass.static.getServletContext = {servletContext}
        ctx = Mock()
        dictionaryRestService =  Mock(DictionaryRestService)
    }

    void cleanup() {
        //Clean up the metaClass mocking we added.
        def remove = GroovySystem.metaClassRegistry.&removeMetaClass
        remove ServletContextHolder
    }
    void assertActivityConcentration(final ActivityConcentration activityConcentration) {
        final ConcentrationResponseSeries concentrationResponseSeries = activityConcentration.concentrationResponseSeries
        assert concentrationResponseSeries
        final List<ConcentrationResponsePoint> concentrationResponsePoints = concentrationResponseSeries.concentrationResponsePoints
        assert concentrationResponsePoints
        assert concentrationResponsePoints.size() > 1
        for (ConcentrationResponsePoint concentrationResponsePoint in concentrationResponsePoints) {
            assert concentrationResponsePoint.testConcentration
            assert concentrationResponsePoint.value
        }
        final CurveFitParameters curveFitParameters = concentrationResponseSeries.curveFitParameters
        assert curveFitParameters
        assert !curveFitParameters.s0
        assert !curveFitParameters.hillCoef
        assert !curveFitParameters.SInf
        assert curveFitParameters.logEc50
        final List<ActivityData> miscDataList = concentrationResponseSeries.miscData

        assert !miscDataList
    }

    void assertChildElements(final List<ActivityData> childElements) {

        for (ActivityData activityData : childElements) {
            assertActivityData(activityData)
        }
    }

    void assertActivityData(final ActivityData activityData) {
        assert activityData.displayName
        assert activityData.dictElemId
        assert activityData.value
    }

    void assertPrimaryElements(final List<ActivityConcentration> primaryElements) {
        for (ActivityConcentration activityConcentration : primaryElements) {
            assertActivityConcentration(activityConcentration)
        }
    }

    void "test all JSON"() {
        when:
        PriorityElement priorityElement = objectMapper.readValue(VANDER_BILT_EXAMPLE_1, PriorityElement.class)
        then:
        servletContext.getAttribute(_)>>{ctx}
        ctx.dictionaryRestService()>>{dictionaryRestService}

        assert priorityElement.displayName == "AvgGluPotency"
        assert priorityElement.dictElemId == 961
        assert priorityElement.responseUnit == "um"
        assert priorityElement.testConcentrationUnit == "uM"
        assert priorityElement.value

        //assert primaryElements
        final List<ActivityConcentration> primaryElements = priorityElement.primaryElements
        assert primaryElements
        assert primaryElements.size() == 3
        assertPrimaryElements(primaryElements)

        //assert childElements
        final List<ActivityData> childElements = priorityElement.childElements
        assert childElements
        assert childElements.size() == 2
        assertChildElements(childElements)
        assert !priorityElement.concentrationResponseSeries
    }
}

