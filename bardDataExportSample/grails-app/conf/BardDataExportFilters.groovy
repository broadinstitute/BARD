import barddataexport.util.AuthenticationService
import javax.servlet.http.HttpServletResponse

class BardDataExportFilters {

    AuthenticationService authenticationService

    def filters = {
        authentication(controller: '*', action: '*') {
            before = {
                Boolean isAuthenticated = authenticationService.authenticate(request)
                if (isAuthenticated) {
                    return true
                }

                response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
                return false
            }
        }
    }
}
