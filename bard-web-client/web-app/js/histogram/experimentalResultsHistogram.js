function drawHistogram(domMarker, oneHistogramsData) {
    "use strict";
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // This D3 graphic is implemented in three sections: definitions, tools, and then building the DOM
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Check your data before going any further.  If something is wrong than abandon the whole process up front
    if ((oneHistogramsData=== undefined) ||
        (oneHistogramsData.histogram=== undefined) ||
        (oneHistogramsData.histogram.length <= 0)) {
        return;
    }

    //
    // Part 1: definitions
    //

    // Size definitions go here
    var container_dimensions = {width: 800, height: 270},
        margin = {top:30, right:20, bottom:30, left:68},
        chart_dimensions = {
            width: container_dimensions.width - margin.left - margin.right,
            height: container_dimensions.height - margin.top - margin.bottom
        },

    // adjustable parameters
        barPaddingPercent = 12,
        ticksAlongHorizontalAxis = 5,
        numberOfHorizontalGridlines = 10,
        paddingOnTopForTitle = 10,
        yLabelProportion = 1, /* implies (1-yLabelProportion is) reserved for y axis labels  */
        minXValue = d3.min(oneHistogramsData.histogram, function (d) {return d[1];}),
        maxXValue = d3.max(oneHistogramsData.histogram, function (d) {return d[2];}),

    // D3 scaling definitions
        xScale = d3.scale.linear()
            .domain([minXValue, maxXValue])
            .range([0, chart_dimensions.width]),
        yScale = d3.scale.linear()
            .domain([0, d3.max(oneHistogramsData.histogram, function (d) {return d[0];})])
            .range([chart_dimensions.height, margin.bottom]),
        histogramBarWidth = xScale(maxXValue - minXValue) / oneHistogramsData.histogram.length,
        adjustedHistogramBarWidth =  histogramBarWidth-((barPaddingPercent/100)*histogramBarWidth),

    //
    // Part 2: tools
    //


    // D3 axis definitions
        xAxis = d3.svg.axis()
            .scale(xScale)
            .orient("bottom")
            .ticks(ticksAlongHorizontalAxis),
        yAxis = d3.svg.axis()
            .scale(yScale)
            .orient("left")
            .ticks(numberOfHorizontalGridlines)
            .tickSize(-chart_dimensions.width*yLabelProportion),

    // Encapsulate the variables/methods necessary to handle tooltips
    TooltipHandler = function ()  {

        // Safety trick for constructors
        if (! (this instanceof TooltipHandler)){
            return new TooltipHandler ();
        }

        // private variable =  tooltip
        var tooltip = d3.select("body")
            .append("div")
            .style("position", "absolute")
            .style("opacity", "0")
            .attr("class", "toolTextAppearance");

        // public methods
        this.respondToBarChartMouseOver = function(d) {
            var stringToReturn = tooltip.html('Compounds in bin: ' + d[0] +
                '<br/>' + 'Minimim bin value: ' + d[1].toPrecision(3) +
                '<br/>' + 'Maximum bin value:' + d[2].toPrecision(3));
            tooltip
                .transition()
                .duration(500)
                .style("opacity", "1");
            d3.select(this)
                .transition()
                .duration(10)
                .attr('fill', '#FFA500');
            return stringToReturn;
        };
        this.respondToBarChartMouseOut =  function(d) {
            var returnValue = tooltip
                .transition()
                .duration(500)
                .style("opacity", "0");
            d3.select(this)
                .transition()
                .duration(250)
                .attr('fill', 'steelblue');
            return returnValue;
        };
        this.respondToBarChartMouseMove =  function(d) {
            return tooltip.style("top", (d3.event.pageY - 10) + "px").style("left", (d3.event.pageX + 10) + "px");
        };
    },
    tooltipHandler  = new TooltipHandler ();

    //
    //  part 3:  Build up the Dom
    //

    // Create a div for each histogram we make. All of those divs are held within the div with ID = histogramHere
    var histogramDiv = domMarker
        .append("div");

    // Create an SVG to hold the graphics
    var svg = histogramDiv
        .attr("class","histogramDiv")
        .append("svg")
        .attr("width", chart_dimensions.width + margin.left + margin.right)
        .attr("height", chart_dimensions.height + margin.top + margin.bottom)
        .append("g")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

    // Create grid lines
    svg.append("g")
        .attr("class", "yaxis")
        .attr("transform", "translate("+ chart_dimensions.width*(1-yLabelProportion)+ ",0)")
        .attr("x", "30px")
        .call(yAxis);

    // Create the rectangles that make up the histogram
    var bar = svg.selectAll("rect")
        .data(oneHistogramsData.histogram)
        .enter()
        .append("g")
        .attr("class", "bar")
        .attr("fill", "steelblue")
        .append("rect")
        .attr("x", function (d, i) {
            return xScale(d[1]);
        })
        .attr("y", function (d) {
            return yScale(d[0]);
        })
        .attr("width", adjustedHistogramBarWidth )
        .attr("height", function (d) {
            return chart_dimensions.height-yScale(d[0]);
        })
        .on("mouseover", tooltipHandler.respondToBarChartMouseOver)
        .on("mousemove", tooltipHandler.respondToBarChartMouseMove)
        .on("mouseout", tooltipHandler.respondToBarChartMouseOut);

    // Create horizontal axis
    svg.append("g")
        .attr("class", "xaxis")
        .attr("transform", "translate(0," + chart_dimensions.height + ")")
        .call(xAxis);

    // Create title  across the top of the graphic
    svg.append("text")
        .attr("x", (chart_dimensions.width / 2))
        .attr("y", -(margin.top / 2)+paddingOnTopForTitle)
        .attr("text-anchor", "middle")
        .attr("class", "histogramTitle")
        .text("Distribution of '" +oneHistogramsData.name + "'");

    // Create title  across the top of the graphic
    svg
        .append("text")
        .attr("x", (4* chart_dimensions.width / 5))
        .attr("y", -(margin.top / 2)+paddingOnTopForTitle)
        .attr("text-anchor", "right")
        .attr("class", "histogramMouseInfo")
        .text("Mouse-over bars for more information");


}
