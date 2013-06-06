var createALegend = function (legendWidth, legendHeight, numberOfDivisions, colorScale, domSelector, minimumValue, maximumValue) {
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
        rootLegendHolder.append('div')
            .attr('class', 'legendExplanation')
            .html('Dynamic range is 0.   All arcs had value <strong>'+maximumValue+'</strong> and the color scheme is therefore constant.');
    }


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
    }

    // Finally build the rest of the legends depending on whether the dynamic range
    //  is 0 or nonzero
    if (dynamicRange === 0) {
        zeroDynamicRange(rootLegendHolder,maximumValue);

    } else {
        nonzeroDynamicRange (numberOfTics,rootLegendHolder,legendWidth,legendHeight,colorScale,numberOfDivisions);

    }
}
