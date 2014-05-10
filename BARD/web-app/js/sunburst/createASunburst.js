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

/***
 *  This code is used to create the sunburst, so it gets called repeatedly ( every time you need to make a new sunburst)
 *  from the higher-level portions of the Linked Hierarchy code.
 *
 *  This was some of the early d3 code that I wrote and I guess it shows. Instead of having modules that
 *  encapsulate modules, instead I use high-level functions that encapsulate series of statements. We still
 *  avoid any of the great evil of global variables, but I no longer think this approach is as maintainable
 *  as the nested modules that characterize the majority of the remainder of the Linked Hierarchies implementation.
 *  Note also that I use basic JavaScript constructors here instead of the JavaScript modules which are by their
 *  nature only executed once, and which provide a clean way to identify internal methods, externally visible methods,
 *  and dependencies.
 *
 *  Note also that the exceedingly nifty sunburst drill down ( with its attendant animation) is implemented in this
 *  section of the code.  Credit has to be given to Jason Davies for figuring out the intricacies of this implementation.
 *  My zooming in/out code borrows quite shamelessly from his.  Look up the "Coffee Flavour Wheel" Which is widely
 *  available on the net and you'll see where I drew those aspects of the code from.
 *
 */






/***
 * The color management routines are used to a color to the different arcs within a sunburst based on the ratio
 * of active to inactive assays that tested the compound. This is meant to be a singleton,, so it is implemented
 * as a JavaScript constructor.  Note the little trick from JavaScript ninja (" Secrets of a JavaScript Ninja",
 * John Resig, 2013) that ensures that the singleton is never implemented more than once.
 * @param colorScale
 * @returns {ColorManagementRoutines}
 * @constructor
 */


var ColorManagementRoutines = function (colorScale) {

    // Safety trick for constructors
    if (!(this instanceof ColorManagementRoutines)) {
        return new ColorManagementRoutines();
    }

    // public methods
    this.colorArcFill = function (d) {
        var returnValue = new String();
        if (d.ac != undefined) {
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


/***
 *  The tooltip handlor is Also meant as a singleton for a single HTML page, So again we use Resig's trick from
 *  JavaScript ninja. Note also, however, that in later code I've come to prefer the JavaScript module pattern
 *  promoted by Stefanov (JavaScript Patterns, 2010), so maybe I'll come back and update this code at some point.
 *
 * @returns {TooltipHandler}
 * @constructor
 */

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
            tooltip.html(d.name + '<br/>' + 'active in ' + d.ac + '<br/>' + 'inactive in ' + d.inac)
                .transition()
                .duration(200)
                .style("opacity", "1");
            return;
        }
        else {
            return tooltip.html(null).style("opacity", "0");
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


/***
 *  createASunburst makes one sunburst each time it is called
 * @param width
 * @param height
 * @param padding
 * @param duration
 * @param colorScale
 * @param domSelector
 * @param cid
 * @param widgetIndex
 */

function createASunburst(width, height, padding, duration, colorScale, domSelector, cid, widgetIndex) {

    var tooltipHandler  = new TooltipHandler (),
        colorManagementRoutines = new ColorManagementRoutines(colorScale),
        radius = Math.min(width, height) / 2;


    var SunburstAnimation = function ()  {
            // Safety trick for constructors
            if (! (this instanceof SunburstAnimation)){
                return new SunburstAnimation ();
            }

            // Need to keep track of how Zoomed we are
            var currentZoomLevel = 0;
            this.zoomLevel = function (newZoomLevel){
                if (newZoomLevel === undefined){
                    return  currentZoomLevel;
                }  else {
                    currentZoomLevel =  newZoomLevel;
                }
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

    var pict = d3.select("#linkVisualizationContainer")
        .append("div")
        .style("position", "absolute")
        .style("top", "565px")
        .style("border", "1")
        .style("left", "445px")
        .attr("height", "150")
        .attr("width", "150")
        .style("z-index", "100")
        .attr("class", "molstruct")
        .style("pointer-events","none")
        .append("img")
        .attr("src", bardAppContext + "/chemAxon/generateStructureImageFromCID?cid="+cid+"&width=150&height=150");

    var svg = d3.select(domSelector).append("svg")
        .attr("width", width)
        .attr("height", height )
        .attr("id", 'sunburst_graphics_holder')
        .append("g")
        .attr("transform", "translate(" + width / 2 + "," + (height /2 ) + ")");


    var x = d3.scale.linear()
        .range([0, 2 * Math.PI]);

    var y = d3.scale.linear()
        .range([0, radius]);


    var partition = d3.layout.partition()
            .value(function (d) {
                return linkedVizData.adjustedPartitionSize(d);
            }).sort(function(a,b){
                return d3.descending(a.name, b.name);
            })
        ;
    var outerRadius = function (d) {
        return Math.max(0, y(d.y + d.dy));
    } ;
    var innerRadius = function (d) {
        return Math.max(0, y(d.y));
    };

    var arc = d3.svg.arc()
        .startAngle(function (d) {
            return Math.max(0, Math.min(2 * Math.PI, x(d.x)));
        })
        .endAngle(function (d) {
            return Math.max(0, Math.min(2 * Math.PI, x(d.x + d.dx)));
        })
        .innerRadius(innerRadius)
        .outerRadius(outerRadius);

    // Method local to createASunburst to keep track of our depth
    var createIdForNode = function (incomingName) {
        var returnValue = 'null';
        var preliminaryGeneratedId = String(incomingName).replace(/\s/g,'_');
        if (preliminaryGeneratedId === '/') {
            returnValue = 'root';
        } else {
            returnValue = preliminaryGeneratedId;
        }
        return returnValue;
    }

    //
    // Change the cursor to zoom-in or zoom-out or nothing, all depending on the current expansion
    //  level of the sunburst.
    //
    var adjustSunburstCursor = function (d) {
        //
        // first deal with all non-root arcs
        //
        if ( !(d.parent  === undefined) &&
            !(d.parent.name  === undefined) )  {
            sunburstAnimation.zoomLevel(d.depth);
            var parentName =  d.parent.name;
            var nodeName =  d.name;
            // reset the cursor for the last center of the sunburst, since it is no longer
            // ready to support a zoom out.  Note that this select statement will also grab
            // nny other stray classes indicating zoom out.
            var previousCenterpiece = d3.select('.indicateZoomOut');
            if (!(previousCenterpiece === undefined)){
                previousCenterpiece.classed('indicateZoomIn', true)
                    .classed('indicateZoomOut', false)
                    .classed('indicateNoZoomingPossible', false);
            }
            var arcThatWasLastZoomed = d3.selectAll('.indicateNoZoomingPossible');
            if (!(arcThatWasLastZoomed === undefined)){
                arcThatWasLastZoomed.classed('indicateNoZoomingPossible', function(d){
                    return (d.name === "/");
                });
                arcThatWasLastZoomed.classed('indicateZoomIn',  function(d){
                    return (!(d.name === "/"));
                });
            }
            // Now deal with the parent node, which DOES need to adopt
            // a cursor indicating that a zoom out is possible.
            var parentNode =  d3.select('#'+createIdForNode(parentName));
            if (sunburstAnimation.zoomLevel()>0)   {
                parentNode.classed('indicateZoomOut', true)
                    .classed('indicateZoomIn', false)
                    .classed('indicateNoZoomingPossible', false);
            }
            // Take the current arc ( the one that was clicked ) and
            // turn off any mouse handling at all, since After clicking an arc
            // it becomes fully expanded, and there is no purpose to clicking it again.
            var currentNode =  d3.select('#'+createIdForNode(nodeName));
            currentNode.classed('indicateZoomOut', false)
                .classed('indicateZoomIn', false)
                .classed('indicateNoZoomingPossible', true);

        }  // next deal with the root arc, in case the user clicked it.
        else if ( !(d  === undefined) &&
            !(d.name  === undefined) ) {  // Root node clicked -- reset mouse ptr
            sunburstAnimation.zoomLevel(d.depth);
            var nodeName =  d.name;
            // whatever had no cursor needs to be turned on
            var arcThatWasLastZoomed = d3.selectAll('.indicateNoZoomingPossible');
            if (!(arcThatWasLastZoomed === undefined)){
                arcThatWasLastZoomed.classed('indicateNoZoomingPossible', function(d){
                    return (d.name === "/");
                });
                arcThatWasLastZoomed.classed('indicateZoomIn',  function(d){
                    return (!(d.name === "/"));
                });
            }
            // take the current arc and turn the cursor off
            var currentNode =  d3.select('#'+createIdForNode(nodeName));
            currentNode.classed('indicateZoomOut', false)
                .classed('indicateZoomIn', false)
                .classed('indicateNoZoomingPossible', true);
        }
    }

    /***
     * When a user clicks on a segment of the expanded sunburst (thus 'zooming in' on a portion of the underlying data hierarchy)
     *  we need to provide a reset button so that they can later zoom back out. They won't need this button right away, since
     *  they could zoom back out by clicking on the Sunburst as long as it is expanded. After their current Sunburst is
     *  contracted, however, then they will need this button.
     *
     * @param drillDownName
     * @param d
     * @param treeNumber
     */
    var markDrillDown = function (drillDownName,d,treeNumber){
        var currentContainer = d3.select('#a4');// where to put the buttons
        var drillDownId =  'drilldownBut'+treeNumber;
        var drillDownLabel =  currentContainer.select ('#'+drillDownId);
        if (!(drillDownLabel.empty())) { // there is already a button to contract this node
            if (drillDownName === '/') {   // we are in the root remove the name
                drillDownLabel.remove ();
            } else {
                drillDownLabel.text(drillDownName);
            }
        } else {
            if (!(drillDownName === '/')) {   // we only need a node if we are outside of the root
                //  We have at least one drill down in place. Make sure that people can reset it.
                var resetDrillDownName = '/';
                var originalD = d;
                var originalTreeId =  d.treeid;
                // Add some explanation, as long as it doesn't exist already
                var drillLabel = d3.select('.drillLabel');
                if (drillLabel.empty ()) {
                    currentContainer.append('div')
                        .text('Click below to reset drilldown')
                        .attr('id','drillLabel')
                        .attr('class','drillLabel')
                        .style('opacity', 0.5);
                }
                currentContainer.append('div')
                    .text(drillDownName)
                    .attr('id',drillDownId)
                    .attr('class','drill')
                    .style('pointer-events', 'none')
                    .style('opacity', 0.5)
                    .on('click',function(d){
                        // the code below is executed when the user clicks the button ( thus requesting that
                        //  the system undo the zoom in performed by the code above).
                        var drillDownLabel =  currentContainer.select ('#'+drillDownId);
                        linkedVizData.adjustMembershipBasedOnSunburstClick (resetDrillDownName, originalD,originalTreeId);
                        linkedVizData.resetDerivedHierarchyRouteToOriginalRoot (originalTreeId) ;
                        drillDownLabel.remove ();
                        // Did we remove the last drill down? If so then remove the label as well
                        var anyRemainingDrillDownLabels =  currentContainer.selectAll ('.drill');
                        if(anyRemainingDrillDownLabels.empty()) {
                            var drillLabelExplanation = d3.select('.drillLabel');
                            if (!(drillLabelExplanation.empty ())) {
                                drillLabelExplanation.remove () ;
                            }
                        }
                    });
            }

        }
    }



    var hierarchyData = linkedVizData.filteredHierarchyData(widgetIndex);

    var path = svg.datum(hierarchyData).selectAll("path")
        .data(partition.nodes)
        .enter().append("path")
        .attr("d", arc)
        .attr("id", function (d) {
            return createIdForNode(d.name);
        })
        .classed('indicateZoomIn', function(d) { return (d.depth || d.name!='/');} )
        .classed('indicateNoZoomingPossible', function(d) { return (!(d.depth || d.name!='/'));} )
        .style("stroke", "#fff")
        .style("fill", function (d) {
            return colorManagementRoutines.colorArcFill(d);
        })
        .on("click", click)
        .on("mouseover", tooltipHandler.mouseOver)
        .on("mousemove", tooltipHandler.mouseMove)
        .on("mouseout",tooltipHandler.mouseOut );


    var text = svg.datum(hierarchyData)
        .selectAll("text")
        .data(partition.nodes);

    // Interpolate the scales!
    function click(d) {
        adjustSunburstCursor(d);
        markDrillDown (d.name,d, d.treeid);
        linkedVizData.adjustMembershipBasedOnSunburstClick (d.name, d, d.treeid);
        linkedVizData.resetRootForHierarchy (d, d.treeid) ;
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
        .style("pointer-events", "none")
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
        .on("click", click);


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

    // Need to make some adjustments to fit the Sunburst in with the linked pies
    var adjustSunburstToFitInWithThePies = (function () {
        var sunburstContainer = d3.selectAll('#suburst_container')
            .style('left', '-10px')
            .style('top', '320px')
            .style('pointer-events', 'none')
            .style('opacity', '0');
        var molecularStructure = d3.selectAll('.molstruct')
            .style('top', '765px')
            .style('opacity', '0');
    })();
}

