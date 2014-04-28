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

package bardqueryapi

import curverendering.DoseCurveRenderingService

import javax.servlet.http.HttpServletResponse

@Mixin(InetAddressUtil)
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
                //Remove zero-concentrations since this causes log(conc) to be negative-infinity and creates an empty plot.
                List<Double> concList = drcCurveCommand.concentrations.collect()
                concList.eachWithIndex { Double conc, int i ->
                    if (!conc) {
                        drcCurveCommand.concentrations.remove(i)
                        drcCurveCommand.activities.remove(i)
                    }
                }

                byte[] bytes
                if (drcCurveCommand.curves?.size() > 0) {
                    bytes = this.doseCurveRenderingService.createDoseCurves(drcCurveCommand)
                } else {
                    bytes = this.doseCurveRenderingService.createDoseCurve(drcCurveCommand)
                }
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
            String cmdParams = drcCurveCommand ? drcCurveCommand.toString() : "DrcCurveCommand is NULL"
            log.error(errorMessage + getUserIpAddress(bardUtilitiesService.username) + "\n$cmdParams", exp)
            return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    errorMessage + "\n$cmdParams")
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
                byte[] bytes = this.doseCurveRenderingService.createDoseCurves(drcCurveCommand)
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
            String cmdParams = drcCurveCommand ? drcCurveCommand.toString() : "DrcCurveCommand is NULL"
            log.error(errorMessage + getUserIpAddress(bardUtilitiesService.username) + "\n$cmdParams", exp)

            return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    errorMessage + "\n$cmdParams")
        }

    }
}
