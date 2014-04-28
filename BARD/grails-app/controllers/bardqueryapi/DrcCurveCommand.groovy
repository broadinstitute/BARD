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

import org.apache.commons.collections.Factory
import org.apache.commons.collections.ListUtils
import curverendering.Curve
import org.apache.commons.collections.FactoryUtils
import grails.plugins.springsecurity.Secured

/**
 * Command object used to parse all the search parameters coming in from the client.
 */
@grails.validation.Validateable
class DrcCurveCommand {
    String xAxisLabel
    String yAxisLabel
    Double width
    Double height
    Double s0
    Double sinf
    Double slope
    Double hillSlope
    Double yNormMin
    Double yNormMax
    List<Double> concentrations = ListUtils.lazyList([], new ListUtilsFactory())
    List<Double> activities = ListUtils.lazyList([], new ListUtilsFactory())
    // List<Curve> curves = ListUtils.lazyList([], new ListCurveFactory())

    List curves = ListUtils.lazyList([], FactoryUtils.instantiateFactory(Curve))

    String toString(){
        String cmdParams = ""
        this.properties.each { cmdParams += "$it.key = $it.value\n" }
        cmdParams += "concentrations size: " + concentrations?.size() + "\n"
        cmdParams += "activities size: " + activities?.size() + "\n"
        cmdParams += "curves size: " + concentrations?.size() + "\n"
        return cmdParams
    }
}
class ListUtilsFactory implements Factory {
    public Object create() {
        return new Double(0);
    }
}
class ListCurveFactory implements Factory {
    public Object create() {
        return new Curve();
    }
}

