function createALegend(legendWidth, legendHeight, numberOfDivisions, colorScale, domSelector) {
    var numberOfTics = 10;
    var arr = Array.apply(null, {length:numberOfDivisions + 1}).map(Number.call, Number);
    var intervals = (legendHeight) / numberOfDivisions;
    var legendHolder = d3.select(domSelector).append("svg")
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
