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

package curverendering

import org.jfree.chart.axis.NumberAxis
import org.jfree.chart.axis.ValueAxis
import org.jfree.chart.plot.PlotOrientation
import org.jfree.ui.RectangleEdge
import spock.lang.Specification
import spock.lang.Unroll

import java.awt.Color
import java.awt.Rectangle
import java.awt.geom.Rectangle2D

@Unroll
class ConfidenceBoundAnnotationUnitSpec extends Specification {
    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test compute2DEdges With Vertical Orientation"() {
        given: " A ConfidencBoundAnnotation Object"
        ConfidenceBoundAnnotation confidenceBoundAnnotation =
            new ConfidenceBoundAnnotation(0.3f, 0.5f, 0.2f, Color.BLACK)
        RectangleEdge domainEdge = RectangleEdge.RIGHT
        RectangleEdge rangeEdge = RectangleEdge.TOP
        Rectangle2D dataArea = new Rectangle(2, 2, 2, 2)
        ValueAxis domainAxis = new NumberAxis()
        ValueAxis rangeAxis = new NumberAxis()

        when: "We call the expandToContain() method on the given Boundss object"
        Map<String, Float> map = confidenceBoundAnnotation.compute2DEdges(PlotOrientation.VERTICAL, domainEdge, rangeEdge, dataArea, domainAxis, rangeAxis)
        then: "The expected to get back the expected warning level"
        assert map
        assert map.j2DX1
        assert map.j2DX2
        assert map.j2DY1
    }

    void "test compute2DEdges With Horizontal Orientation"() {
        given: " A ConfidencBoundAnnotation Object"
        ConfidenceBoundAnnotation confidenceBoundAnnotation =
            new ConfidenceBoundAnnotation(0.3f, 0.5f, 0.2f, Color.BLACK)
        RectangleEdge domainEdge = RectangleEdge.RIGHT
        RectangleEdge rangeEdge = RectangleEdge.TOP
        Rectangle2D dataArea = new Rectangle(2, 2, 2, 2)
        ValueAxis domainAxis = new NumberAxis()
        ValueAxis rangeAxis = new NumberAxis()

        when: "We call the expandToContain() method on the given Boundss object"
        confidenceBoundAnnotation.compute2DEdges(PlotOrientation.HORIZONTAL, domainEdge, rangeEdge, dataArea, domainAxis, rangeAxis)
        then: "The expected to get back the expected warning level"
        Exception ee = thrown()
        assert ee.message == "Orientation PlotOrientation.HORIZONTAL, Not Yet Implemented"
    }

}

