package bardqueryapi

import curverendering.DoseCurveRenderingService
import grails.plugins.springsecurity.Secured

import javax.servlet.http.HttpServletResponse

@Mixin(InetAddressUtil)
@Secured(['isFullyAuthenticated()'])
class DoseResponseCurveController {

    DoseCurveRenderingService doseCurveRenderingService
    BardUtilitiesService bardUtilitiesService

    /**
     *
     * @return render the gsp
     */
    def doseResponseCurve(DrcCurveCommand drcCurveCommand) {
        Double width = new Double(300);
        Double height = new Double(200);
        try {
            if (drcCurveCommand) {
                drcCurveCommand.width = width
                drcCurveCommand.height = height
                byte[] bytes = this.doseCurveRenderingService.createDoseCurve(drcCurveCommand)
                response.contentType = 'image/png'
                response.outputStream.setBytes(bytes)
            } else {
                flash.message = 'Points required in order to draw a Dose Response Curve'
                log.error(flash.message + getUserIpAddress(bardUtilitiesService.username))

                //if we get here then it is an error
                return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        flash.message)
            }
        } catch (Exception exp) {
            //if we get here then it is an error
            final String errorMessage = "Could not draw a Dose Response Curve. Please try again"
            log.error(errorMessage + getUserIpAddress(bardUtilitiesService.username), exp)
            return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    errorMessage)
        }

    }
    /**
     *
     * @return render the gsp
     */
    def doseResponseCurves(DrcCurveCommand drcCurveCommand) {
        Double width = new Double(300);
        Double height = new Double(200);
        try {
            if (drcCurveCommand) {
                drcCurveCommand.width = width
                drcCurveCommand.height = height
                byte[] bytes = this.doseCurveRenderingService.createDoseCurves(drcCurveCommand.curves)
                response.contentType = 'image/png'
                response.outputStream.setBytes(bytes)
            } else {
                flash.message = 'Points required in order to draw a Dose Response Curve'
                log.error(flash.message + getUserIpAddress(bardUtilitiesService.username))

                //if we get here then it is an error
                return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Could not draw a Dose Response Curve. Please try again")
            }
        } catch (Exception exp) {
            //if we get here then it is an error
            final String errorMessage = "Could not draw a Dose Response Curve. Please try again"
            log.error(errorMessage + getUserIpAddress(bardUtilitiesService.username), exp)

            return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    errorMessage)
        }

    }
}
