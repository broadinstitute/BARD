import org.springframework.security.access.AccessDeniedException
import org.springframework.security.acls.model.NotFoundException

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
        "/"(controller: 'bardWebInterface', action: 'redirectToIndex')
        "403"(controller: "errors", action: "error403")

        "500"(controller: "errors", action: "error500")

        "500"(controller: "errors", action: "error403",
                exception: AccessDeniedException)

        "500"(controller: "errors", action: "error403",
                exception: NotFoundException)
	}
}
