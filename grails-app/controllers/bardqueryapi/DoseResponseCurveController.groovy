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
        if (drcCurveCommand==null)   then    drcCurveCommand=    DoseResponseCurveController.testPostBody()
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


    private static Map testPostBody() {
        def requestMap = [:]
        //== Create Concentration values
        List<Double> concentrations = [0.0000000005000000413701855f,
                0.0000000005000000413701855f,
                0.000000002099999951710174f,
                0.000000002099999951710174f,
                0.000000008000000661922968f,
                0.000000008000000661922968f,
                0.00000002999999892949745f,
                0.00000002999999892949745f,
                0.0000001350000076172364f,
                0.0000001350000076172364f,
                0.0000004999999987376214f,
                0.0000004999999987376214f,
                0.0000020999998469051206f,
                0.0000020999998469051206f,
                0.000007999999979801942f,
                0.000007999999979801942f,
                0.000035000000934815034f,
                0.000035000000934815034f]
        int index = 0
        def concentrationMap = [:]
        for(Double concentration:concentrations){
            String key = "concentrations[" + index + "]"
            Double value = concentration
            concentrationMap.put(key,value)
            ++index
        }
        requestMap.putAll(concentrationMap)
        //== Create activity values
        def activityMap = [:]
        index = 0
        List<Double> activities = [2.4638421535491943f,
                -1.2127126455307007f,
                1.3271921873092651f,
                -1.7350740432739258f,
                -0.6476028561592102f,
                -1.4504003524780273f,
                -0.31541985273361206f,
                -6.233550071716309f,
                -6.293687343597412f,
                -14.393035888671875f,
                -23.312355041503906f,
                -36.71405029296875f,
                -60.78630447387695f,
                -59.16990661621094f,
                -82.43856811523438f,
                -85.94784545898438f,
                -92.01828002929688f,
                -95.36405181884766f]
        for(Double activity:activities){
            String key = "activities[" + index + "]"
            Double value =activity
            activityMap.put(key,value)
            ++index
        }
        requestMap.putAll(activityMap)


        Double ac50 = 0.00000124934001632937;
        requestMap.put("ac50",ac50)



        Double hillSlope = 0.9267182946205139;
        requestMap.put("hillSlope",hillSlope)

        Double s0 = 0.15947775542736053;
        requestMap.put("s0",s0)

        Double sinf = -98.35183715820312;
        requestMap.put("sinf",sinf)

        return requestMap
    }


}
