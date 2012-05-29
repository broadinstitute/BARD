class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{
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
            action = [GET: "element"]
        }
        name cap: "/api/cap"(controller: "capRest") {
            action = [GET: "cap"]
        }
        name projects: "/api/cap/projects"(controller: "capRest") {
            action = [GET: "projects"]
        }
        name project: "/api/cap/project/$id"(controller: "capRest") {
            action = [GET: "project", PATCH: "updateProject"]
        }
        name assay: "/api/cap/assay/$id"(controller: "capRest") {
            action = [GET: "assay", PATCH: "updateAssay"]
        }
        name assayDocument: "/api/cap/assayDocument/$id"(controller: "capRest") {
            action = [GET: "assayDocument"]
        }
        name experiments: "/api/data"(controller: "experimentRest") {
            action = [GET: "experiments"]
        }
        name experiment: "/api/data/experiment/$id"(controller: "experimentRest") {
            action = [GET: "experiment", PATCH:"updateExperiment"]
        }
        name results: "/api/data/experiment/$id/results"(controller: "experimentRest") {
            action = [GET: "results"]
        }
        name result: "/api/data/result/$id"(controller: "experimentRest") {
            action = [GET: "result", PATCH: "updateResult"]
        }
        "/"(view:"/index")
		"500"(view:'/error')
	}
}
