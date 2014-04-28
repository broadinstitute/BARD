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

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?" {
            constraints {
                // apply constraints here
            }
        }
        name api: "/api"(controller: "rootRest") {
            action = [GET: "api"]
        }
        name dictionary: "/api/dictionary"(controller: "dictionaryRest") {
            action = [GET: "dictionary"]
        }
        name resultType: "/api/dictionary/resultType/$id"(controller: "dictionaryRest") {
            action = [GET: "resultType"]
        }
        name stage: "/api/dictionary/stage/$id"(controller: "dictionaryRest") {
            action = [GET: "stage"]
        }
        name element: "/api/dictionary/element/$id"(controller: "dictionaryRest") {
            action = [GET: "element", PUT: "updateElement"]
        }
        name externalReferences:"/api/externalReferences"(controller: "externalReferenceRest") {
            action = [GET: "externalReferences"]
        }
        name externalReference:"/api/externalReferences/$id"(controller: "externalReferenceRest") {
            action = [GET: "externalReference"]
        }
        name externalSystems:"/api/externalSystems"(controller: "externalReferenceRest") {
            action = [GET: "externalSystems"]
        }
        name externalSystem:"/api/externalSystems/$id"(controller: "externalReferenceRest") {
            action = [GET: "externalSystem"]
        }
        name assays: "/api/assays"(controller: "assayRest") {
            action = [GET: "assays"]
        }
        name assay: "/api/assays/$id"(controller: "assayRest") {
            action = [GET: "assay", PUT: "updateAssay"]
        }
        name assayDocument: "/api/assayDocument/$id"(controller: "assayRest") {
            action = [GET: "assayDocument"]
        }
        name projects: "/api/projects"(controller: "projectRest") {
            action = [GET: "projects"]
        }
        name project: "/api/projects/$id"(controller: "projectRest") {
            action = [GET: "project", PUT: "updateProject"]
        }
        name projectDocument: "/api/projectDocument/$id"(controller: "projectRest") {
            action = [GET: "projectDocument"]
        }
        name experiments: "/api/experiments"(controller: "experimentRest") {
            action = [GET: "experiments"]
        }
        name experiment: "/api/experiments/$id"(controller: "experimentRest") {
            action = [GET: "experiment", PUT: "updateExperiment"]
        }
        name results: "/api/experiments/$id/results"(controller: "experimentRest") {
            action = [GET: "results"]
        }
        name result: "/api/results/$id"(controller: "experimentRest") {
            action = [GET: "result", PUT: "updateResult"]
        }
        "/"(view: "/index")
        "500"(view: '/error')
    }
}
