function createASunburst(width, height, padding, duration, colorScale, domSelector) {

    var radius = Math.min(width, height) / 2;

    color = d3.scale.category10().domain(["/",
        "cytoskeletal protein",
        "ligase"
    ]);


    function colorArcFill(d) {
//                return colorByRandomMap(d)
        return colorByActivity(d)
    }


    function colorByRandomMap(d) {
        var returnValue = d3.scale.category10().domain(["nucleic acid binding", "ligase", "nuclear hormone receptor"]);
        return returnValue(d.name);
    }


    function colorByActivity(d) {
        var returnValue = new String();
        if (d.ac != undefined) {
            var actives = parseInt(d.ac);
            var inactives = parseInt(d.inac);
            var prop = actives / (actives + inactives);
            returnValue = colorScale(prop);
        } else {
            returnValue = "#FF00FF";
        }
        return returnValue;
    }
    function colorFill(d) {
        return colorByRandomMap(d);
    }
    function colorText(d) {
        return '#000';
    }
    var svg = d3.select(domSelector).append("svg")
        .attr("width", width)
        .attr("height", height + 10)
        .append("g")
        .attr("transform", "translate(" + width / 2 + "," + (height * .5 + 5) + ")");

    var x = d3.scale.linear()
        .range([0, 2 * Math.PI]);

    var y = d3.scale.sqrt()
        .range([0, radius]);


    var partition = d3.layout.partition()
        .sort(function (d) {
            return d.size;
        })
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

    var tooltip = d3.select("body")
        .append("div")
        .style("opacity", "0")
        .style("position", "absolute")
        .style("z-index", "10")
        .style("visibility", "visible")
        .attr("class", "toolTextAppearance");

    function tooltipContent(d) {
        if (d.name != '/') {
            tooltip.style("visibility", "visible").style("opacity", "0").transition()
                .duration(200).style("opacity", "1")
        }
        if (d.children == undefined)
            return tooltip.html(d.name + '<br/>' + 'active in ' + d.ac + '<br/>' + 'inactive in ' + d.inac);
        else
            return tooltip.html(d.name);
    }

    var path = svg.datum($data[0]).selectAll("path")
        .data(partition.nodes)
        .enter().append("path")
        // .attr("display", function(d) { return (d.depth || d.name!='/') ? null : "none"; }) // hide inner ring
        .attr("d", arc)
        .style("stroke", "#fff")
        .style("fill", function (d) {
            return colorArcFill(d);
        })
        .on("click", click)
        .on("mouseover", tooltipContent)
        .on("mousemove", function () {
            return tooltip.style("top", (event.pageY - 10) + "px").style("left", (event.pageX + 10) + "px");
        })
        .on("mouseout", function () {
            return tooltip.style("visibility", "hidden");
        });

    var text = svg.datum($data[0]).selectAll("text").data(partition.nodes);

    function brightness(rgb) {
        return rgb.r * .299 + rgb.g * .587 + rgb.b * .114;
    }
    // Interpolate the scales!
    function arcTween(d) {
        var my = maxY(d),
            xd = d3.interpolate(x.domain(), [d.x, d.x + d.dx]),
            yd = d3.interpolate(y.domain(), [d.y, my]),
            yr = d3.interpolate(y.range(), [d.y ? 20 : 0, radius]);
        return function (d) {
            return function (t) {
                x.domain(xd(t));
                y.domain(yd(t)).range(yr(t));
                return arc(d);
            };
        };
    }

    function maxY(d) {
        return d.children ? Math.max.apply(Math, d.children.map(maxY)) : d.y + d.dy;
    }

    function isParentOf(p, c) {
        if (p === c) return true;
        if (p.children) {
            return p.children.some(function (d) {
                return isParentOf(d, c);
            });
        }
        return false;
    }
    function mouseover(d) {
        return d.name;
    }

    function click(d) {
        path.transition()
            .duration(duration)
            .attrTween("d", arcTween(d));

        // Somewhat of a hack as we rely on arcTween updating the scales.
        text.style("visibility", function (e) {
            return isParentOf(d, e) ? null : d3.select(this).style("visibility");
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
                return isParentOf(d, e) ? 1 : 1e-6;
            })
            .each("end", function (e) {
                d3.select(this).style("visibility", isParentOf(d, e) ? null : "hidden");
            });
    }


    var textEnter = text.enter().append("svg:text")
        .style("fill-opacity", 1)
        .style("fill", function (d) {
            return  colorText(d);
//                                    return brightness(d3.rgb(color(d))) < 125 ? "#eee" : "#000";
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
        .on("mouseover", tooltipContent)
        .on("mousemove", function () {
            return tooltip.style("top", (event.pageY - 10) + "px").style("left", (event.pageX + 10) + "px");
        })
        .on("mouseout", function () {
            return tooltip.style("visibility", "hidden");
        });

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


    d3.select(self.frameElement).style("height", height + "px");
}
