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

        name externalReferences:"/api/externalReferences"(controller: "dictionaryRest") {
            action = [GET: "externalReferences"]
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
        name externalReference:"/api/externalReference/$id"(controller: "externalReferenceRest") {
            action = [GET: "externalReference"]
        }
        name externalSystems:"/api/externalSystems"(controller: "externalReferenceRest") {
            action = [GET: "externalSystems"]
        }
        name externalSystem:"/api/externalSystem/$id"(controller: "externalReferenceRest") {
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