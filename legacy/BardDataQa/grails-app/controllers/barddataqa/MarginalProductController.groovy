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

package barddataqa

import grails.validation.Validateable

class MarginalProductController {

    private static final String controllerKey = "extraController"
    private static final String actionKey = "extraAction"
    private static final String descriptionKey = "extraDescription"

    MarginalProductService marginalProductService

    def index() {
        render("Hello, world!")
    }

    def show() {
        Long datasetId = params.get("datasetId").toString().toLong()
        Dataset dataset = Dataset.findById(datasetId)
        [marginalProductList: marginalProductService.calculate(datasetId, new Date()), dataset: dataset]
    }

    def showNeedMaas() {
        Closure<List<Integer>> findNeedMaasClosure = {Long datasetId, Integer projectUid ->
            return marginalProductService.findAidsThatNeedMaas(datasetId, projectUid)
        }

        render(view: "showAids", model: buildMap(params, findNeedMaasClosure, "AID's that need Minimum Assay Annotation"))
    }

    def showNeedRta() {
        Closure<List<Integer>> findNeedRtaClosure = {Long datasetId, Integer projectUid ->
            return marginalProductService.findAidsThatNeedRta(datasetId, projectUid)
        }

        render(view: "showAids", model: buildMap(params, findNeedRtaClosure, "AID's that need Result Type Annotation"))
    }

    def showMissingAid() {
        Closure<List<Integer>> findMissingClosure = {Long datasetId, Integer projectUid ->
            return marginalProductService.findAidsThatAreMissing(datasetId, projectUid)
        }

        render(view: "showAids", model: buildMap(params, findMissingClosure, "AID's that are not in the external_reference table"))
    }

    def showResultMapProblems() {
        Integer projectUid = params.get("projectUid").toString().toInteger()

        return [problemAidMap: marginalProductService.findAidsWithResultMapProblem(projectUid)]
    }

    private Map buildMap(Map params, Closure<List<Integer>> findAidsClosure, String title) {
        Long datasetId = params.get("datasetId").toString().toLong()
        Integer projectUid = params.get("projectUid").toString().toInteger()

        List<Integer> aidList = findAidsClosure(datasetId, projectUid)

        return [title: title, aidList: aidList]
    }
}
