package bardqueryapi

import curverendering.DoseCurveRenderingService
import org.jfree.chart.JFreeChart
import org.jfree.chart.encoders.EncoderUtil
import org.jfree.chart.encoders.ImageFormat

import javax.servlet.http.HttpServletResponse

class DoseResponseCurveController {

    DoseCurveRenderingService doseCurveRenderingService
    /**
     *
     * @return render the jsp
     */
    def doseResponseCurve(DrcCurveCommand drcCurveCommand) {
        //TODO: Tune this as you see fit. Consider adding to the Command object
        Double width = new Double(300);
        Double height = new Double(200);

        if (drcCurveCommand) {
            if (drcCurveCommand.validate()) { //must be valid

                JFreeChart chart =
                    this.doseCurveRenderingService.createDoseCurve(drcCurveCommand.concentrations,
                            drcCurveCommand.activities, drcCurveCommand.ac50, drcCurveCommand.hillSlope, drcCurveCommand.s0, drcCurveCommand.sinf, null, null, null, null)
                // write the image byte array to the binding
                byte[] bytes = "".getBytes();
                try {
                    bytes = EncoderUtil.encode(chart.createBufferedImage(width.intValue(), height.intValue()), ImageFormat.PNG);
                    response.contentType = 'image/png'
                    response.outputStream.setBytes(bytes)
                } catch (Exception exp) {
                    log.error(exp)
                    return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                            "Compound search has encountered an error:\n${exp.message}")
                }

            }else{ //TODO: return a sensible error
                drcCurveCommand.errors.allErrors.each {
                    println it
                }
            }
        } else {
            flash.message = 'Points required in order to draw a DRC'
            return response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    flash.message)
        }
    }
}
