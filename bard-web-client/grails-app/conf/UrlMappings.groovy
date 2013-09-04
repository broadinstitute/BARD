class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}
        "/bbgrid/$id?"(controller: "grid") {
            action = [GET:"list", POST: "save", DELETE: "delete", PUT: "edit"]
        }

       "/" (controller: 'bardWebInterface', action: 'redirectToIndex')


		"500"(view:'/error')
	}
}
