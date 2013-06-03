// Encapsulate the variables/methods necessary to handle tooltips

// Encapsulate the variables/methods necessary to handle tooltips
var ColorManagementRoutines = function (colorScale) {

    // Safety trick for constructors
    if (!(this instanceof ColorManagementRoutines)) {
        return new ColorManagementRoutines();
    }

    // public methods
    this.colorArcFill = function (d) {
        var returnValue = new String();
        if (d.ac != undefined) {
            if (d.name === "/") { // root is special cased
                return "#fff";
            }
            var actives = parseInt(d.ac);
            var inactives = parseInt(d.inac);
            if ((actives + inactives) === 0) // this should never happen, but safety first!
                return "#fff";
            var prop = actives / (actives + inactives);
            returnValue = colorScale(prop);
        } else {
            returnValue = "#FF00FF";
        }
        return returnValue;
    };

    this.colorText = function (d) {
        return '#000';
    };
};


var TooltipHandler = function ()  {
    // Safety trick for constructors
    if (! (this instanceof TooltipHandler)){
        return new TooltipHandler ();
    }

    var tooltip = d3.select("body")
        .append("div")
        .style("opacity", "0")
        .style("position", "absolute")
        .style("z-index", "10")
        .attr("class", "toolTextAppearance");

    this.mouseOver = function(d) {
        if (d.name != '/') {
            tooltip.transition()
                .duration(200)
                .style("opacity", "1")
        }
//                        if (d.children === undefined)
        if (d.name === '/')  {
            return tooltip.html(null).style("opacity", "0");
        }  else {
            return tooltip.html(d.name + '<br/>' + 'active in ' + d.ac + '<br/>' + 'inactive in ' + d.inac);
        }

    };
    this.mouseMove = function (d) {
        if (d.name === '/')  {
            return tooltip.html(null).style("opacity", "0");
        }  else {
            return tooltip .style("top", (d3.event.pageY - 10) + "px")
                .style("left", (d3.event.pageX + 10) + "px");
        }

    };
    this.mouseOut =  function () {
        return tooltip.style("opacity", "0");
    };
};





function createASunburst(width, height, padding, duration, colorScale, domSelector, cid) {

    var tooltipHandler  = new TooltipHandler ();
    var colorManagementRoutines = new ColorManagementRoutines(colorScale);
    var radius = Math.min(width, height) / 2;


    var SunburstAnimation = function ()  {
            // Safety trick for constructors
            if (! (this instanceof SunburstAnimation)){
                return new SunburstAnimation ();
            }

            this.arcTween = function (d) {
                var my = maxY(d),
                    xd = d3.interpolate(x.domain(), [d.x, d.x + d.dx]),
                    yd = d3.interpolate(y.domain(), [d.y, my]),
                    yr = d3.interpolate(y.range(), [d.y ? 100 : 0, radius]);
                return function (d) {
                    return function (t) {
                        x.domain(xd(t));
                        y.domain(yd(t)).range(yr(t));
                        return arc(d);
                    };
                };
            };

            var maxY = function (d) {
                return d.children ? Math.max.apply(Math, d.children.map(maxY)) : d.y + d.dy;
            }

            var isParentOf = function (p, c) {
                if (p === c) return true;
                if (p.children) {
                    return p.children.some(function (d) {
                        return isParentOf(d, c);
                    });
                }
                return false;
            };

            this.isParentOf = isParentOf;

        },
        sunburstAnimation = SunburstAnimation();

    var pict = d3.select("body")
        .append("div")
        .style("position", "absolute")
        .style("top", "565px")
        .style("border", "1")
        .style("left", "445px")
        .attr("height", "150")
        .attr("width", "150")
        .style("z-index", "100")
        .attr("class", "molstruct")
        .style("pointer-events", "none")
        .append("img")
        .attr("src", "/bardwebclient/chemAxon/generateStructureImageFromCID?cid="+cid+"&width=150&height=150");

    var svg = d3.select(domSelector).append("svg")
        .attr("width", width)
        .attr("height", height )
        .append("g")
        .attr("transform", "translate(" + width / 2 + "," + (height /2 ) + ")");


    var x = d3.scale.linear()
        .range([0, 2 * Math.PI]);

    var y = d3.scale.linear()
        .range([0, radius]);


    var partition = d3.layout.partition()
        .value(function (d) {
            return d.size;
        });

    var arc = d3.svg.arc()
        .startAngle(function (d) {
            return Math.max(0, Math.min(2 * Math.PI, x(d.x)));
        })
        .endAngle(function (d) {
            return Math.max(0, Math.min(2 * Math.PI, x(d.x + d.dx)));
        })
        .innerRadius(function (d) {
            return Math.max(0, y(d.y));
        })
        .outerRadius(function (d) {
            return Math.max(0, y(d.y + d.dy));
        });

    var path = svg.datum($data[0]).selectAll("path")
        .data(partition.nodes)
        .enter().append("path")
        //     .attr("display", function(d) { return (d.depth || d.name!='/') ? null : "none"; }) // hide inner ring
        .attr("d", arc)
        .style("stroke", "#fff")
        .style("fill", function (d) {
            return colorManagementRoutines.colorArcFill(d);
        })
        .on("click", click)
        .on("mouseover", tooltipHandler.mouseOver)
        .on("mousemove", tooltipHandler.mouseMove)
        .on("mouseout",tooltipHandler.mouseOut );

    var text = svg.datum($data[0]).selectAll("text").data(partition.nodes);

    // Interpolate the scales!

    function click(d) {
        path.transition()
            .duration(duration)
            .attrTween("d", sunburstAnimation.arcTween(d));

        // Somewhat of a hack as we rely on arcTween updating the scales.
        text.style("visibility", function (e) {
            return sunburstAnimation.isParentOf(d, e) ? null : d3.select(this).style("visibility");
        })
            .transition()
            .duration(duration)
            .attrTween("text-anchor", function (d) {
                return function () {
                    return x(d.x + d.dx / 2) > Math.PI ? "end" : "start";
                };
            })
            .attrTween("transform", function (d) {
                var multiline = (d.name || "").split(" ").length > 1;
                return function () {
                    var angle = x(d.x + d.dx / 2) * 180 / Math.PI - 90,
                        rotate = angle + (multiline ? -.5 : 0);
                    return "rotate(" + rotate + ")translate(" + (y(d.y) + padding) + ")rotate(" + (angle > 90 ? -180 : 0) + ")";
                };
            })
            .style("fill-opacity", function (e) {
                return sunburstAnimation.isParentOf(d, e) ? 1 : 1e-6;
            })
            .each("end", function (e) {
                d3.select(this).style("visibility", sunburstAnimation.isParentOf(d, e) ? null : "hidden");
            });
    }


    var textEnter = text.enter().append("svg:text")
        .style("fill-opacity", 1)
        .style("fill", function (d) {
            return  colorManagementRoutines.colorText(d);
        })
        .attr("text-anchor", function (d) {
            return x(d.x + d.dx / 2) > Math.PI ? "end" : "start";
        })
        .attr("dy", ".2em")
        .attr("transform", function (d) {
            var multiline = (d.name || "").split(" ").length > 1,
                angle = x(d.x + d.dx / 2) * 180 / Math.PI - 90,
                rotate = angle + (multiline ? -.5 : 0);
            return "rotate(" + rotate + ")translate(" + (y(d.y) + padding) + ")rotate(" + (angle > 90 ? -180 : 0) + ")";
        })
        .on("click", click)
        .on("mouseover", tooltipHandler.mouseOver)
        .on("mousemove", tooltipHandler.mouseMove)
        .on("mouseout",tooltipHandler.mouseOut );

    textEnter.append("tspan")
        .attr("x", 0)
        .text(function (d) {
            return d.depth ? d.name.split(" ")[0] : "";
        });
    textEnter.append("tspan")
        .attr("x", 0)
        .attr("dy", "1em")
        .text(function (d) {
            return d.depth ? d.name.split(" ")[1] || "" : "";
        });
    textEnter.append("tspan")
        .attr("x", 0)
        .attr("dy", "1em")
        .text(function (d) {
            return d.depth ? d.name.split(" ")[2] || "" : "";
        });
    textEnter.append("tspan")
        .attr("x", 0)
        .attr("dy", "1em")
        .text(function (d) {
            return d.depth ? d.name.split(" ")[3] || "" : "";
        });


//            d3.select(self.frameElement).style("height", height + "px");
}
