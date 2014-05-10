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

var createALegend = function (legendWidth,
                              legendHeight,
                              numberOfDivisions,
                              colorScale,
                              domSelector,
                              minimumValue,
                              maximumValue) {

    var  numberOfTics = 10,
        dynamicRange = maximumValue - minimumValue;

    //
    // First build the core the legend, which is used no matter what the dynamic range
    //

    var rootLegendHolder = d3.select(domSelector).append("div")
        .attr("id", "sunburstlegend")
        .attr("class", "legendHolder")
        .html('<br />Color assignment:<br /> x = active / <br />(active + inactive)');

    rootLegendHolder.append('hr')
        .attr("width", '100%')
        .attr("color", '#000');

    //
    // Define a few private methods that we will use later
    //
    var zeroDynamicRange = function (rootLegendHolder,maximumValue) {
        var  valueAdjustedForPrecision =  0;
        if (!(maximumValue === undefined) &&
            ((typeof maximumValue) ==='number')) {
            valueAdjustedForPrecision =  maximumValue.toPrecision(3);
        }

        rootLegendHolder.append('div')
            .attr('class', 'legendExplanation')
            .html('Dynamic range is 0.   All arcs had value <strong>'+valueAdjustedForPrecision+'</strong> and the color scheme is therefore constant.');
    };


    var nonzeroDynamicRange = function (numberOfTics,rootLegendHolder,legendWidth,legendHeight,colorScale,numberOfDivisions) {
        var arr = Array.apply(null, {length:numberOfDivisions + 1}).map(Number.call, Number),
            intervals = (legendHeight) / numberOfDivisions;

        var legendHolder = rootLegendHolder.append("svg")
            .attr("width", legendWidth)
            .attr("height", legendHeight + 10)
            .attr("transform", "translate(" + legendWidth / 2 + "," + (legendHeight * 0.5 + 5) + ")");

        var theLegend = legendHolder.selectAll('g')
            .data(arr)
            .enter()
            .append('g')
            .attr('class', 'legend');
        theLegend.append('rect')
            .attr('x', legendWidth - 80)
            .attr('y', function (d, i) {
                return (i * intervals) + 6;
            })
            .attr('width', 10)
            .attr('height', intervals)
            .style('fill', function (d, i) {
                return colorScale(i / numberOfDivisions);//color(d.name);
            });

        var textSpacing = (legendHeight) / (numberOfTics * 2);
        theLegend.append('text')
            .attr('x', legendWidth - 60)
            .attr('y', function (d, i) {
                return (i * 2) + 11;
            })
            .text(function (d, i) {
                if ((i % textSpacing) === 0) {
                    var valToWrite = (i / numberOfDivisions);
                    return valToWrite.toString();
                }
                else
                    return '';
            });
    };

    // Finally build the rest of the legends depending on whether the dynamic range
    //  is 0 or nonzero
    if (dynamicRange === 0) {
        zeroDynamicRange(rootLegendHolder,
            maximumValue);
    } else {
        nonzeroDynamicRange ( numberOfTics,
            rootLegendHolder,
            legendWidth,
            legendHeight,
            colorScale,
            numberOfDivisions );
    }

    return rootLegendHolder;
}
