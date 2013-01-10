package bardqueryapi

import curverendering.DoseCurveRenderingService

import javax.servlet.http.HttpServletResponse

class DoseResponseCurveController {

    DoseCurveRenderingService doseCurveRenderingService
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
                log.error(flash.message)
                //if we get here then it is an error
                return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Could not draw a Dose Response Curve. Please try again")
            }
        } catch (Exception exp) {

            //if we get here then it is an error
            final String errorMessage = "Could not draw a Dose Response Curve. Please try again"
            log.error(errorMessage,exp)
            return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    errorMessage)
        }

    }
}