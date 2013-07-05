<!doctype html>
<!--[if IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
<head>
    <title>Sunburst</title>
    <title>BARD : Compound Bio-Activity Summary</title>
    <r:require modules="sunburst"/>
    <r:layoutResources/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon">

    <script>
        window.onload = function () {
            $('#activity').change(function () {
                if (this.value == "1") {
                    location.href = "./bigSunburst?actives=t&inactives=f";
                }
                if (this.value == "2") {
                    location.href = "./bigSunburst?actives=f&inactives=t";
                }
                if (this.value == "3") {
                    location.href = "./bigSunburst?actives=t&inactives=t";
                }

            });
            $('#coloringOptions').change(function () {
                if (this.value == "1") {
                    location.href = "./bigSunburst?colorOption=1";
                }
                if (this.value == "2") {
                    location.href = "./bigSunburst?colorOption=2";
                }
                if (this.value == "3") {
                    location.href = "./bigSunburst?colorOption=3";
                }
            });
        }
    </script>
















</head>

<body>
<r:layoutResources/>









<script>
    var assay = {};
    var assayIndex = {};


    var linkedVisualizationModule = (function () {
        //
        //  Variables to describe the layout of the whole page, with special attention
        //   to the unexpanded widgets
        //
        var grandWidth = 1052,// width of the entire display
                totalWidgetNumber = 4, // how many widgets are we dealing with
                widgetHeight = 270, // how tall is each individual widget
                widgetSpacing = 7, // how much vertical space between widgets
                margin = {top: 30, right: 20, bottom: 30, left: 10},  // boundaries of displayable area
                width = grandWidth - margin.left - margin.right, // displayable width
                height = widgetHeight - margin.top - margin.bottom, // displayable height
                widgetWidth = grandWidth / totalWidgetNumber,   // each individual widget width
                quarterWidgetWidth = widgetWidth / 4,   // useful spacer
                allowThisMuchExtraSpaceInWidgetForATitle = 30, // the title in your widget
                widgetWidthWithoutSpacing = widgetWidth - (widgetSpacing * 0.5),
                widgetHeightWithTitle = widgetHeight + allowThisMuchExtraSpaceInWidgetForATitle, // final widget width

        // We have to explicitly pass in the size of the pie charts, so describe those here
                pieChartWidth = widgetWidth - 13,  // how wide is the pie chart
                pieChartRadius = pieChartWidth / 2, // pie chart reuse
                innerRadius = 30, // open circle in pie
                innerRadiusWhenExpanded = 100, // open circle in pie

        // The expanded widgets are described below. These numbers can't be derived from anything else, because you could
        //  in principle put this display anywhere.
                displayWidgetX = 10,// expanded widget X location.
                displayWidgetY = 320, // expanded widget Y location.
                displayWidgetWidth = 1000, // expanded widget Y width.
                displayWidgetHeight = 1000, // expanded widget Y height.
                bigPie = (displayWidgetWidth / 2) - displayWidgetX, // size of pie in display mode

        //  I need to have a color for every possible pie slice. Since the color map is largely arbitrary, d3 does not
        //   seem to provide An easy way to say "please give me another color unlike when you've given me before".  Note
        //   that the colors I want are not _entirely_ arbitrary because really light colors don't seem to look good in
        //   a pie chart filled with brighter colors, so I can't simply use randomizing function with a seed
                colors = [ '#393b79', '#5254a3', '#6b6ecf', '#9c9ede', '#637939', '#8ca252', '#b5cf6b', '#cedb9c', '#8c6d31', '#bd9e39',
                    '#e7ba52', '#e7cb94', '#843c39', '#ad494a', '#d6616b', '#e7969c', '#7b4173', '#a55194', '#ce6dbd', '#de9ed6',
                    '#1f77b4', '#aec7e8', '#ff7f0e', '#ffbb78', '#2ca02c', '#98df8a', '#d62728', '#ff9896', '#9467bd', '#c5b0d5',
                    '#8c564b', '#c49c94', '#e377c2', '#f7b6d2', '#7f7f7f', '#c7c7c7', '#bcbd22', '#dbdb8d', '#17becf', '#9edae5',
                    '#1f77b4', '#ff7f0e', '#2ca02c', '#d62728', '#9467bd', '#8c564b', '#e377c2', '#7f7f7f', '#bcbd22', '#17becf',
                    '#393b79', '#5254a3', '#6b6ecf', '#9c9ede', '#637939', '#8ca252', '#b5cf6b', '#cedb9c', '#8c6d31', '#bd9e39',
                    '#e7ba52', '#e7cb94', '#843c39', '#ad494a', '#d6616b', '#e7969c', '#7b4173', '#a55194', '#ce6dbd', '#de9ed6',
                    '#1f77b4', '#aec7e8', '#ff7f0e', '#ffbb78', '#2ca02c', '#98df8a', '#d62728', '#ff9896', '#9467bd', '#c5b0d5',
                    '#8c564b', '#c49c94', '#e377c2', '#f7b6d2', '#7f7f7f', '#c7c7c7', '#bcbd22', '#dbdb8d', '#17becf', '#9edae5',
                    '#1f77b4', '#ff7f0e', '#2ca02c', '#d62728', '#9467bd', '#8c564b', '#e377c2', '#7f7f7f', '#bcbd22', '#17becf',
                    '#5254a3', '#6b6ecf', '#9c9ede', '#637939', '#8ca252', '#b5cf6b', '#cedb9c', '#8c6d31', '#bd9e39',
                    '#e7ba52', '#e7cb94', '#843c39', '#ad494a', '#d6616b', '#e7969c', '#7b4173', '#a55194', '#ce6dbd', '#de9ed6',
                    '#1f77b4', '#aec7e8', '#ff7f0e', '#ffbb78', '#2ca02c', '#98df8a', '#d62728', '#ff9896', '#9467bd', '#c5b0d5',
                    '#8c564b', '#c49c94', '#e377c2', '#f7b6d2', '#7f7f7f', '#c7c7c7', '#bcbd22', '#dbdb8d', '#17becf', '#9edae5',
                    '#1f77b4', '#ff7f0e', '#2ca02c', '#d62728', '#9467bd', '#8c564b', '#e377c2', '#7f7f7f', '#bcbd22', '#17becf',
                    '#393b79', '#5254a3', '#6b6ecf', '#9c9ede', '#637939', '#8ca252', '#b5cf6b', '#cedb9c', '#8c6d31', '#bd9e39',
                    '#e7ba52', '#e7cb94', '#843c39', '#ad494a', '#d6616b', '#e7969c', '#7b4173', '#a55194', '#ce6dbd', '#de9ed6',
                    '#1f77b4', '#aec7e8', '#ff7f0e', '#ffbb78', '#2ca02c', '#98df8a', '#d62728', '#ff9896', '#9467bd', '#c5b0d5',
                    '#8c564b', '#c49c94', '#e377c2', '#f7b6d2', '#7f7f7f', '#c7c7c7', '#bcbd22', '#dbdb8d', '#17becf', '#9edae5',
                    '#1f77b4', '#ff7f0e', '#2ca02c', '#d62728', '#9467bd', '#8c564b', '#e377c2', '#7f7f7f', '#bcbd22', '#17becf'];

        // below are some names and text strings
        piename = ['a0', 'a1', 'a2', 'a3'], // internal names for the widgets
                textForExpandingButton = 'click to expand', // text on button to expand to full display
                textForContractingButton = 'click to contract', //text on button to contract unexpended widget

            //  This next set of variables are only for convenience.  They are derived strictly from those above,
            //   and they are consumed below in preference to those above.  The idea was to conceptually simplify
            //   some of the variables above and to those that describe either compressed or uncompressed widgets.
                compressedPos = [
                    {'x': margin.left + ((widgetWidth + widgetSpacing) * 0), 'y': 10},
                    {'x': margin.left + ((widgetWidth + widgetSpacing) * 1), 'y': 10},
                    {'x': margin.left + ((widgetWidth + widgetSpacing) * 2), 'y': 10},
                    {'x': margin.left + ((widgetWidth + widgetSpacing) * 3), 'y': 10}
                ],
                expandedPos = [
                    {'x': (widgetWidth * 0) + (quarterWidgetWidth * 1), 'y': 10},
                    {'x': (widgetWidth * 1) + (quarterWidgetWidth * 2), 'y': 10},
                    {'x': (widgetWidth * 2) + (quarterWidgetWidth * 3), 'y': 10}
                ],

            //  dc vars
                maximumRowsInTable = 150,


            //-------widgetPosition------
            // JavaScript module. This portion of the code allows us to keep track of which widgets are expanded
            // and which remain in their original positions. There are a functions that allow you to ask the constructor
            // about its status ( examples:  isAnyWidgetExpanded() returns a Boolean to tell you if anything's expanded,
            // while expandedWidget () returns a number to tell you which widget has been expanded.
            //---------------------------
                widgetPosition = (function () {
                    // private property
                    var currentWidgetPosition = { 'up': [0, 1, 2, 3],
                                'down': [] },

                            isAnyWidgetExpanded = function () {   // returns a Boolean
                                return currentWidgetPosition.down.length > 0;
                            },

                            expandedWidget = function () {   // returns a number
                                if (currentWidgetPosition.down.length == 1) {
                                    return currentWidgetPosition.down[0];
                                } else {
                                    return -1;
                                }
                            },

                            unexpandedWidgets = function () {   // returns an array
                                return currentWidgetPosition.up;
                            },

                    // the main action routine.
                            expandThisWidget = function (widgetToBeExpanded) {  // number: 1 = success, 0 = failure
                                var indexOfDesiredWidget = 0;
                                // first make sure the incoming argument is inside the acceptable range
                                if ((widgetToBeExpanded < 0) || (widgetToBeExpanded > 3)) {
                                    return -1;
                                }
                                // another way to go wrong is to try to expand a widget that isn't in the top row to begin with
                                indexOfDesiredWidget = currentWidgetPosition.up.indexOf(widgetToBeExpanded);
                                if (indexOfDesiredWidget == -1) {
                                    return indexOfDesiredWidget;
                                }
                                // you can also go wrong if there is already a widget expanded
                                if (currentWidgetPosition.down.length != 0) {
                                    indexOfDesiredWidget = -1;
                                }

                                if (indexOfDesiredWidget > -1) {
                                    // everything looks good. Let's do what the caller has asked us to do.
                                    //First copy the widget to the down position
                                    currentWidgetPosition.down.push(currentWidgetPosition.up[indexOfDesiredWidget]);
                                    // Now remove it from the top row and collapse those around it
                                    currentWidgetPosition.up = currentWidgetPosition.up.slice(0, indexOfDesiredWidget).concat(
                                            currentWidgetPosition.up.slice(indexOfDesiredWidget + 1, 4));
                                }
                                return indexOfDesiredWidget;
                            },

                    // the other action routine, though this one is much simpler since there's only one choice
                            unexpandAllWidgets = function () {
                                currentWidgetPosition.up.push(currentWidgetPosition.down.pop());
                                currentWidgetPosition.up.sort(function (a, b) {
                                    return a - b;
                                });
                                currentWidgetPosition.down = [];
                            };
                    // end var

                    // now present the public API
                    return {
                        unexpandAllWidgets: unexpandAllWidgets,
                        expandThisWidget: expandThisWidget,
                        unexpandedWidgets: unexpandedWidgets,
                        expandedWidget: expandedWidget,
                        isAnyWidgetExpanded: isAnyWidgetExpanded
                    };


                }());


        var displayManipulator = (function () {
            var rememberClickEffectors = [],
                    disableAllPieClickEffectors = function () {
                        for (var widgetCounter = 0; widgetCounter < totalWidgetNumber; widgetCounter++) {
                            rememberClickEffectors [widgetCounter] = d3.selectAll('#a' + widgetCounter + '-chart>svg>g>.pie-slice>path').on('click');
                            d3.selectAll('#a' + widgetCounter + '-chart>svg>g>.pie-slice>path').on('click', null);
                        }
                    },
                    reenableAllPieClickEffectors = function () {
                        if (rememberClickEffectors === undefined) {
                            alert(' JavaScript error--an attempt was made to reenableAllPieClickEffectors before originally disabling those ClickEffectors');
                        } else {
                            for (var widgetCounter = 0; widgetCounter < totalWidgetNumber; widgetCounter++) {
                                d3.selectAll('#a' + widgetCounter + '-chart>svg>g>.pie-slice>path').on('click', rememberClickEffectors [widgetCounter]);
                            }
                        }
                    } ,
                    eraseAnyOrphanedTooltips = function () {
                        var orphanedTooltips = d3.selectAll('.toolTextAppearance');
                        if (!(orphanedTooltips === undefined)) {
                            orphanedTooltips.style('opacity', 0);
                        }
                    },
                    cleanUpAnyGraphicsWeAreDoneWith = function () {
                        var potentialOrphanedGraphics = d3.select('div#sunburstdiv>svg');
                        if ((!(potentialOrphanedGraphics === null))&&
                                (!(potentialOrphanedGraphics[0] === null))&&
                                (!(potentialOrphanedGraphics[0][0] === null)))       {
                            potentialOrphanedGraphics.remove();
                        }

                        var potentialOrphanedLegend = d3.select('div#sunburstlegend');
                        if ((!(potentialOrphanedLegend === null))&&
                                (!(potentialOrphanedLegend[0] === null))&&
                                (!(potentialOrphanedLegend[0][0] === null))) {
                            potentialOrphanedLegend.remove();
                        }
                    },
                    createAResetButtonIfWeSwitchedARoot = function () {
                        // Instead of looking to see which widget is closing let's just check them all, and create a reset button
                        // whenever the root is not equal to the user-specified Global parent
                        for( var i=0 ; i<X ; i++ ) {

                        }
                    },

            // convenience routine for adding a pie chart
                    addPieChart = function (crossFilterVariable, id, key, colors, localPieChartWidth, localPieChartRadius, localInnerRadius) {
                        var dimensionVariable = crossFilterVariable.dimension(function (d) {
                                    return d[key];
                                }),
                                dimensionVariableGroup = dimensionVariable.group().reduceSum(function (d) {
                                    return 1;
                                }),
                                displayDataGroup = function (d) {
                                    var returnValue = d.data.key.toString() + " (" + d.data.value + ")";
                                    returnValue = returnValue.replace("http://amigo.geneontology.org/cgi-bin/amigo/term_details?term=", "[GO]");
                                    returnValue = returnValue.replace("http://www.ncbi.nlm.nih.gov/gquery/?term=", "[NCBI]");
                                    return returnValue;
                                };


                        return dc.pieChart("#" + id)
                                .width(localPieChartWidth)
                                .height(localPieChartWidth)
                                .transitionDuration(200)
                                .radius(localPieChartRadius)
                                .innerRadius(localInnerRadius)
                                .dimension(dimensionVariable)
                                .group(dimensionVariableGroup)
                                .colors(colors)
                                .label(displayDataGroup);
                    },


                    addDcTable = function (crossFilterVariable, id, key) {
                        var dimensionVariable = crossFilterVariable.dimension(function (d) {
                                    return d[key];
                                }),
                                dimensionVariableGroup = function (d) {
                                    return "";
                                },
                                dimensionVariableGroupTotal = function (d) {
                                    return dimensionVariable.groupAll();
                                };

                        var theTable = dc.dataTable("#" + id)
                                .dimension(dimensionVariable)
                                .group(dimensionVariableGroup)
                                .size(maximumRowsInTable)
                                .columns([
                                    function (d) {
                                        return d.GO_biological_process_term;
                                    },
                                    function (d) {
                                        return d.assay_format;
                                    },
                                    function (d) {
                                        return d.protein_target;
                                    },
                                    function (d) {
                                        return d.assay_type;
                                    },
                                    function(d) {
                                        return d.assayId;
                                    }
                                ])
                                .order(d3.ascending)
                                .sortBy(function (d) {
                                    return d.GO_biological_process_term;
                                });
                        //                        This datecount  function below doesn't seem to work for me. What's wrong?
                        //                        dc.dataCount("#data-count")
                        //                                .dimension(dimensionVariable)
                        //                                .group(dimensionVariableGroupTotal);

                        return  theTable;
                    },


                    expandDataAreaForAllPieCharts = function (pieChartHolderElement) {
                        pieChartHolderElement.attr('height', displayWidgetY);
                    },


                    moveDataTableOutOfTheWay = function (dataTable) {
                        dataTable.transition()
                                .duration(500)
                                .style("top", 50 + displayWidgetY + displayWidgetHeight + "px");  // Extra spaces for 'click to contract' button
                    },

                    moveDataTableBackToItsOriginalPosition = function (dataTable) {
                        dataTable.transition()
                                .delay(1000)
                                .duration(500)
                                .style("top", "300px");  // Extra spaces for 'click to contract' button
                    },


                    shiftBackgroundWidgets = function (domDescription, horizontalPosition) {
                        domDescription
                                .transition()
                                .duration(1000)
                                .style("left", horizontalPosition + "px");
                    },


                    // buttons are supplied to allow the user to reset their hierarchy drill down, but the buttons only work
                    // from the pie mode
                    deactivateDrillDownResetButtons = function () {

                        // turn off the reset buttons, and they are deactivated when the Sunburst is visible
                        var drillDownButtons = d3.selectAll('.drill');
                        if (!(drillDownButtons.empty())){
                            drillDownButtons.style('pointer-events', 'none')
                                            .style('opacity', 0.5)
                                            .style('cursor','pointer');
                        }

                        // If there is a drill down explanation then fade it  out
                        var drillLabelExplanation = d3.select('.drillLabel');
                        if (!(drillLabelExplanation.empty ())) {
                            drillLabelExplanation.style('opacity', 0.5);
                        }

                    },

                    // buttons are supplied to allow the user to reset their hierarchy drill down. These were deactivated when a sunburst
                    //   was displayed, but they need to be reactivated again when we get back to pie mode
                    activateDrillDownResetButtons = function () {

                        // do we have any drill down reset buttons to deal with
                        var drillDownResetButtonsExist = false;
                        var drillDownButtons = d3.selectAll('.drill');
                        if (!(drillDownButtons.empty())){

                            drillDownResetButtonsExist = true;
                            drillDownButtons.style('pointer-events', 'auto')
                                            .style('opacity', 1)
                                            .style('cursor','pointer');

                        }

                        var drillLabelExplanation = d3.select('.drillLabel');
                        if (!(drillLabelExplanation.empty ())) {
                            // we have an explanation line

                            if (drillDownResetButtonsExist){   // if buttons exist
                                drillLabelExplanation.style('opacity', 1); ;
                            }  else {    // if no buttons exist
                                // this should never happen ( an explanation exists without any buttons ), but
                                // but if it should happen somehow then let's clean it up
                                drillLabelExplanation.remove();
                            }

                        }


                    },


                    spotlightOneAndBackgroundThree = function (d, spotlight, background1, background2, background3, origButton, expandedPos) {

                        deactivateDrillDownResetButtons () ;

                        // first handle the spotlight element and then the three backup singers
                        spotlight
                                .style('padding-left', 10 + "px")
                                .transition()
                                .duration(200)
                                .style("top", d.display.coords.y + "px")
                                .transition()
                                .duration(400)
                                .style("left", d.display.coords.x + "px")
                                .style('height', d.display.size.height + "px")
                                .style('width', d.display.size.width + "px");
                        shiftBackgroundWidgets(background1, expandedPos[0].x);
                        shiftBackgroundWidgets(background2, expandedPos[1].x);
                        shiftBackgroundWidgets(background3, expandedPos[2].x);

                        //   Turn off the text label based on click event for background widgets
                        background1.selectAll('.pieChart>svg>g>.pie-slice').style('pointer-events', 'none');
                        background2.selectAll('.pieChart>svg>g>.pie-slice').style('pointer-events', 'none');
                        background3.selectAll('.pieChart>svg>g>.pie-slice').style('pointer-events', 'none');

                        //  Turn off the expander button, since the user needs to contract the expanded
                        //  widget before they try to expand the new one. It would be nice to click that
                        //  button for them, but D three does not support that sort of activation is you are
                        //  using bound data. I should probably connect to those data dynamically to get around
                        //  this problem.
                        background1.selectAll('.expandButton').style('pointer-events', 'none').style('opacity', 0.5);
                        background2.selectAll('.expandButton').style('pointer-events', 'none').style('opacity', 0.5);
                        background3.selectAll('.expandButton').style('pointer-events', 'none').style('opacity', 0.5);


                        origButton
                                .text(textForContractingButton)
                                .attr('class', 'contractButton')
                                .transition()
                                .delay(1000)
                                .duration(500)
                                .style('opacity', 1);
                    },

                    resetOneAndResettleThree = function (d, spotlight, background1, background2, background3, origButton, expandedPos) {
                        // first handle the spotlight element and then the three backup singers
                        spotlight.transition()
                                .duration(500)
                                .style('height', d.orig.size.height + "px")
                                .style('width', d.orig.size.width + "px")
                                .style('padding-left', '5px')
                                .transition()
                                .duration(500)
                                .style("left", d.orig.coords.x + "px")
                                .transition()
                                .duration(500)
                                .style("top", d.orig.coords.y + "px");

                        shiftBackgroundWidgets(background1, background1.data()[0].orig.coords.x);
                        shiftBackgroundWidgets(background2, background2.data()[0].orig.coords.x);
                        shiftBackgroundWidgets(background3, background3.data()[0].orig.coords.x);
                        background1.selectAll('.pieChart>svg>g>.pie-slice').style('pointer-events', 'auto');
                        background2.selectAll('.pieChart>svg>g>.pie-slice').style('pointer-events', 'auto');
                        background3.selectAll('.pieChart>svg>g>.pie-slice').style('pointer-events', 'auto');
                        //  Turn back on the expander buttons
                        //  widget before they try to expand the new one. It would be nice to click that
                        //  button for them, but D three does not support that sort of activation is you are
                        //  using bound data. I should probably connect to those data dynamically to get around
                        //  this problem.
                        var dataSetNumber=parseInt(background1.attr('id').match(/\d/));
                        if (!(linkedVizData.extendedHierarchyDataExists(dataSetNumber)))  {
                            d3.select('#expbutton'+dataSetNumber).style('pointer-events', 'none').style('opacity', 0.5);
                        } else {
                            d3.select('#expbutton'+dataSetNumber).style('pointer-events', 'auto').style('opacity', 1);
                        }
                        dataSetNumber=parseInt(background2.attr('id').match(/\d/));
                        if (!(linkedVizData.extendedHierarchyDataExists(dataSetNumber)))  {
                            d3.select('#expbutton'+dataSetNumber).style('pointer-events', 'none').style('opacity', 0.5);
                        } else {
                            d3.select('#expbutton'+dataSetNumber).style('pointer-events', 'auto').style('opacity', 1);
                        }
                        dataSetNumber=parseInt(background3.attr('id').match(/\d/));
                        if (!(linkedVizData.extendedHierarchyDataExists(dataSetNumber)))  {
                            d3.select('#expbutton'+dataSetNumber).style('pointer-events', 'none').style('opacity', 0.5);
                        } else {
                            d3.select('#expbutton'+dataSetNumber).style('pointer-events', 'auto').style('opacity', 1);
                        }

//                        background1.selectAll('.expandButton').style('pointer-events', 'auto').style('opacity', 1);
//                        background2.selectAll('.expandButton').style('pointer-events', 'auto').style('opacity', 1);
//                        background3.selectAll('.expandButton').style('pointer-events', 'auto').style('opacity', 1);

                        var x = origButton
                                .text(textForExpandingButton)
                                .attr('class', 'expandButton')
                                .transition()
                                .delay(1000)
                                .duration(500)
                                .style('opacity', 1);

                        // turn back on this reset buttons
                        activateDrillDownResetButtons ();
                     },

                    expandGraphicsArea = function (graphicsTarget, graphicsTitle) {

                        var bigarc = d3.svg.arc()
                                .innerRadius(innerRadiusWhenExpanded)
                                .outerRadius(bigPie);

                        var nullarc = d3.svg.arc()
                                .innerRadius(innerRadiusWhenExpanded)
                                .outerRadius(innerRadiusWhenExpanded+1);

                        graphicsTarget
                                .attr('width', displayWidgetWidth)
                                .attr('height', displayWidgetHeight + 50); // Extra room for the 'click to contract' button

                        graphicsTarget
                                .select('g')
                                .selectAll('text')
                                .remove();

                        graphicsTarget
                                .selectAll('g')
                                .select('path')
                                .transition()
                                .duration(1500)
                                .attr("d", bigarc)  // pie size easily adjusted here
                                .attr("transform", "translate(368,375)");    // We need use explicit numbers here, not variables. This would be something to fix

                        graphicsTitle.attr('class', 'expandedGraphTitle');
                    },

                    contractGraphicsArea = function (graphicsTarget, graphicsTitle) {

                        var arc = d3.svg.arc()
                                .innerRadius(innerRadius)
                                .outerRadius(pieChartRadius);

                        graphicsTarget
                                .transition()
                                .duration(1500)
                                .attr('width', pieChartWidth)
                                .attr('height', pieChartWidth);

                        graphicsTarget
                                .select('g')
                                .selectAll('text')
                                .remove();

                        graphicsTarget
                                .selectAll('g')
                                .select('path')
                                .transition()
                                .duration(500)
                                .attr("d", arc)
                                .attr("transform", "translate(0,0)");

                        graphicsTitle.attr('class', 'graphTitle');

                    },
                    removeTheSun = function () {
                        d3.selectAll('#suburst_container').style('pointer-events', 'none')
                                .transition()
                                .delay(0)
                                .duration(500)
                                .style('opacity', '0')
                    }

            swapAPieForTheSun = function (pieDiv, sunburstContainer, expandedButtonNum, callbackToExpandOrContractOnButtonClick) {
                pieDiv.style('pointer-events', 'none')
                        .transition()
                        .delay(1000)
                        .duration(500)
                        .style('opacity', '0');
                sunburstContainer.style('pointer-events', null)
                        .transition()
                        .delay(1000)
                        .duration(500)
                        .style('opacity', '1');
                if (linkedVizData.retrieveCurrentHierarchicalData(expandedButtonNum).children !== undefined) {
                    createASunburst( 1000, 1000,5,1000,continuousColorScale,'div#sunburstdiv', 672376, expandedButtonNum );
                    createALegend(120, 200,100,continuousColorScale,'div#legendGoesHere',minimumValue, maximumValue);
                    d3.selectAll('#suburst_container').style('pointer-events', null);
                } else {
                    d3.select('div#sunburstdiv')
                            .append('div')
                            .attr("width", 1000)
                            .attr("height", 1000 )
                            .style("padding-top", '200px' )
                            .style("text-align", 'center' )
                            .append("h1")
                            .html("No off-embargo assay data are  available for this compound." +
                                    "Please either choose a different compound, or else come" +
                                    " back later when more assay data may have accumulated.");
                }
                d3.select('#sunburstContractor')
                    // This next step gets a little bit ugly.  What we want to do is make the
                    // Sunburst disappear, and then have the pie charts rearrange themselves like always.
                    // Unfortunately D3 does not provide a standardized way to execute a click ( thereby
                    // Initiating the associated callback ) if you have data associated with your object,
                    // Which we most certainly do. Therefore I have to mix up my own copy of the data
                    // that the callback routine for one of the pie charts would receive, and then explicitly
                    // execute the callback method. There has to be a better way to get the desired effect,
                    // though for what it's worth this approach is fully functional ( just butt-ugly, that's all)
                        .on('click', function (d) {
                            sunburstContainer.style('pointer-events', 'none')
                                    .style('opacity', '0');
                            pieDiv.style('pointer-events', null)
                                    .style('opacity', '1');
                            var molecularStructure = d3.selectAll('.molstruct')
                                    .style('opacity', '0');
                            var substituteData = {    index: expandedButtonNum,
                                orig: {
                                    coords: {
                                        x: compressedPos[expandedButtonNum].x,
                                        y: compressedPos[expandedButtonNum].y },
                                    size: {
                                        width: widgetWidthWithoutSpacing,
                                        height: widgetHeightWithTitle }
                                },
                                display: {
                                    coords: {
                                        x: displayWidgetX,
                                        y: displayWidgetY },
                                    size: {
                                        width: displayWidgetWidth,
                                        height: displayWidgetHeight }
                                }
                            }
                            callbackToExpandOrContractOnButtonClick(substituteData, expandedButtonNum);
                        });
                var molecularStructure = d3.selectAll('.molstruct')
                        .transition()
                        .delay(1000)
                        .duration(500)
                        .style('opacity', '1');

            };

            // end var

            // Public API for this module
            return {
                disableAllPieClickEffectors: disableAllPieClickEffectors,
                reenableAllPieClickEffectors: reenableAllPieClickEffectors,
                eraseAnyOrphanedTooltips: eraseAnyOrphanedTooltips,
                cleanUpAnyGraphicsWeAreDoneWith: cleanUpAnyGraphicsWeAreDoneWith,
                contractGraphicsArea: contractGraphicsArea,
                expandGraphicsArea: expandGraphicsArea,
                resetOneAndResettleThree: resetOneAndResettleThree,
                spotlightOneAndBackgroundThree: spotlightOneAndBackgroundThree,
                expandDataAreaForAllPieCharts: expandDataAreaForAllPieCharts,
                moveDataTableOutOfTheWay: moveDataTableOutOfTheWay,
                moveDataTableBackToItsOriginalPosition: moveDataTableBackToItsOriginalPosition,
                addDcTable: addDcTable,
                addPieChart: addPieChart,
                swapAPieForTheSun: swapAPieForTheSun
            };
        }() );

        //
        //   Get the data and make the plots using dc.js.  Use this as an opportunity to encapsulate any methods that are
        //    used strictly locally
        //
        var generateLinkedPies = (function () {


            var buttondata = [
                {    index: 0,
                    orig: {
                        coords: {
                            x: compressedPos[0].x,
                            y: compressedPos[0].y },
                        size: {
                            width: widgetWidthWithoutSpacing,
                            height: widgetHeightWithTitle }
                    },
                    display: {
                        coords: {
                            x: displayWidgetX,
                            y: displayWidgetY },
                        size: {
                            width: displayWidgetWidth,
                            height: displayWidgetHeight }
                    }
                },
                {    index: 1,
                    orig: {
                        coords: {
                            x: compressedPos[1].x,
                            y: compressedPos[1].y },
                        size: {
                            width: widgetWidthWithoutSpacing,
                            height: widgetHeightWithTitle }
                    },
                    display: {
                        coords: {
                            x: displayWidgetX,
                            y: displayWidgetY },
                        size: {
                            width: displayWidgetWidth,
                            height: displayWidgetHeight }
                    }
                },
                {    index: 2,
                    orig: {
                        coords: {
                            x: compressedPos[2].x,
                            y: compressedPos[2].y },
                        size: {
                            width: widgetWidthWithoutSpacing,
                            height: widgetHeightWithTitle }
                    },
                    display: {
                        coords: {
                            x: displayWidgetX,
                            y: displayWidgetY },
                        size: {
                            width: displayWidgetWidth,
                            height: displayWidgetHeight }
                    }
                },
                {   index: 3,
                    orig: {
                        coords: {
                            x: compressedPos[3].x,
                            y: compressedPos[3].y },
                        size: {
                            width: widgetWidthWithoutSpacing,
                            height: widgetHeightWithTitle }
                    },
                    display: {
                        coords: {
                            x: displayWidgetX,
                            y: displayWidgetY },
                        size: {
                            width: displayWidgetWidth,
                            height: displayWidgetHeight }
                    }
                }
            ];


            // Private method used to pull the data in from the remote site
            var readInData = function () {

                        return    linkedVizData.retrieveLinkedData();

                    },


            // Our main button handler callback
                    handleExpandOrContractClick = function (d, x) {
                        // we better decide whether where you want to expand or contract
                        var origButton = d3.select('#expbutton' + d.index)
                                        .style('opacity', 0),
                                expandedWidget,
                                unexpandedWidget,
                                expandContractButton;

                        if (!widgetPosition.isAnyWidgetExpanded()) {
                            displayManipulator.disableAllPieClickEffectors();
                            displayManipulator.expandDataAreaForAllPieCharts(d3.select('.pieCharts'));
                            displayManipulator.moveDataTableOutOfTheWay(d3.select('#data-table'));
                            widgetPosition.expandThisWidget(d.index);
                            expandedWidget = widgetPosition.expandedWidget();
                            unexpandedWidget = widgetPosition.unexpandedWidgets();
                            displayManipulator.spotlightOneAndBackgroundThree(d, d3.select('#a' + expandedWidget),
                                    d3.select('#a' + unexpandedWidget[0]),
                                    d3.select('#a' + unexpandedWidget[1]),
                                    d3.select('#a' + unexpandedWidget[2]),
                                    origButton,
                                    expandedPos);
                            expandContractButton = d3.select('#a' + expandedWidget + '-chart>.graphTitle')
                            displayManipulator.expandGraphicsArea(d3.select('#a' + expandedWidget).select('.pieChart>svg'),
                                    expandContractButton);
                            if (linkedVizData.extendedHierarchyDataExists(expandedWidget)){
                                displayManipulator.swapAPieForTheSun(d3.select('#a' + expandedWidget), d3.selectAll('#suburst_container'), expandedWidget, handleExpandOrContractClick);
                            }

                        }

                        else if (widgetPosition.expandedWidget() == d.index) {
                            expandedWidget = widgetPosition.expandedWidget();
                            unexpandedWidget = widgetPosition.unexpandedWidgets();
                            displayManipulator.contractGraphicsArea(d3.select('#a' + x).select('.pieChart>svg'),
                                    d3.select('#a' + expandedWidget + '-chart>.expandedGraphTitle'));
                            displayManipulator.resetOneAndResettleThree(d, d3.select('#a' + expandedWidget),
                                    d3.select('#a' + unexpandedWidget[0]),
                                    d3.select('#a' + unexpandedWidget[1]),
                                    d3.select('#a' + unexpandedWidget[2]),
                                    origButton,
                                    expandedPos);
                            widgetPosition.unexpandAllWidgets();
                            displayManipulator.moveDataTableBackToItsOriginalPosition(d3.select('#data-table'));
                            displayManipulator.reenableAllPieClickEffectors();
                            displayManipulator.eraseAnyOrphanedTooltips();
                            displayManipulator.cleanUpAnyGraphicsWeAreDoneWith();
                        }

                    },

                    attachButtonsToThePieContainers = function (classOfPieContainers, callbackToExpandOrContractOnButtonClick, buttondata, sunburstContainer) {
                        var placeButtonsHere = d3.selectAll(classOfPieContainers)
                                .data(buttondata);

                        placeButtonsHere.append("div")
                                .text(textForExpandingButton)
                                .attr('class', 'expandButton')
                                .attr('id', function (d) {
                                    return 'expbutton' + d.index;
                                })
                                .on('click', callbackToExpandOrContractOnButtonClick);

                        // deactivate button if we have no data
                        for ( var i=0 ; i<4 ; i++ ) {
                            if (!(linkedVizData.extendedHierarchyDataExists(i))) {
                                var expandedButton = d3.select('#expbutton'+i);
                                if (!(expandedButton.empty())) {
                                    expandedButton.style('pointer-events', 'none').style('opacity', 0.5);
                                }

                            } else {
                                var expandedButton = d3.select('#expbutton'+i);
                                if (!(expandedButton.empty())) {
                                    expandedButton.style('pointer-events', 'auto').style('opacity', 1);
                                }

                            }
                        }


                        // Add a button for causing misunderstood disappear
                        sunburstContainer.append("div")
                                .text(textForContractingButton)
                                .attr('class', 'contractButton')
                                .attr('id', 'sunburstContractor')
                                .data(buttondata);

                    },

                    verifyLinkedData = function () {
                        console.log('hi ho');
                        d3.json("/bardwebclient/bardWebInterface/linkedData/${cid}", function (incomingData) {
                            // create an empty list, Just in case we get null data
                            linkedVizData.parseData(incomingData);
                            if (!linkedVizData.validateLinkedData()){
                                console.log(' we have trouble with incoming linked data');
                                throw new Exception ('bad data');
                            }  else {
                                linkedVizData.appendConditionalStatusFields()
                            }
                            presentLinkedData();
                            linkedVizData.cleanupOriginalHierarchyData(2);
                        });// d3.json

                    },


                    presentLinkedData = function () {
                        // create an empty list, Just in case we get null data
                        var assays = [];

                        // Clean up the data.  De-dup, and assign
                        assays = readInData();

                        // Create the crossfilter for the relevant dimensions and groups.
                        assay = crossfilter(assays);

                        // Build everything were going to display
                        allDataDcTable = displayManipulator.addDcTable(assay, 'data-table', 'assayId');
                        biologicalProcessPieChart = displayManipulator.addPieChart(assay, 'a0-chart', 'GO_biological_process_term', colors, pieChartWidth, pieChartRadius, innerRadius);
                        assayFormatPieChart = displayManipulator.addPieChart(assay, 'a1-chart', 'assay_format', colors, pieChartWidth, pieChartRadius, innerRadius);
                        assayIdDimensionPieChart = displayManipulator.addPieChart(assay, 'a2-chart', 'protein_target', colors, pieChartWidth, pieChartRadius, innerRadius);
                        assayTypePieChart = displayManipulator.addPieChart(assay, 'a3-chart', 'assay_type', colors, pieChartWidth, pieChartRadius, innerRadius);

                        assayIndex = assay.dimension(function (d) {
                            return d['index'];
                        });

                        // We should be ready, display it
                        dc.renderAll();

                        // Finally, attach some data along with buttons and callbacks to the pie charts we've built
                        attachButtonsToThePieContainers('.pieChartContainer', handleExpandOrContractClick, buttondata, d3.selectAll('#suburst_container'));


                    };

            return {
                verifyLinkedData: verifyLinkedData,
                presentLinkedData: presentLinkedData
            }
        }())


        // **********************************************************
        // The highest level call.  Everything starts from here.
        // **********************************************************
        generateLinkedPies.verifyLinkedData();  //

    }());
</script>








<div id = "graphs">
    %{--<div id="histogram">--}%
    %{--<span id = "histTitle" class="graphTitle">Histogram</span>--}%
    %{--<a class="reset" href="javascript:histogramChart.filterAll();dc.redrawAll();" style="display: none;">reset</a>--}%
    %{--<span class="reset" style="display: none;"></span>--}%
    %{--<div class = "clearfix"></div>--}%
    %{--</div>--}%

    <div id = "pieCharts" class="pieCharts">

        <div id = "a0"  class = "pieChartContainer" style="left: 10px; top: 10px; width: 260px;  height: 300px;">
            <div id="a0-chart" class="pieChart">
                <span class="graphTitle">Biological process</span>
                <a class="reset" href="javascript:biologicalProcessPieChart.filterAll();dc.redrawAll();" style="display: none;">reset</a>
                <span class="reset" style="display: none;"></span>
                <div class = "clearfix"></div>
            </div>

        </div>

        <div id = "a1"  class = "pieChartContainer" style="left: 280px; top: 10px; width: 260px; height: 300px;">
            <div id="a1-chart" class="pieChart">
                <span class="graphTitle">Assay format</span>
                <a class="reset" href="javascript:assayFormatPieChart.filterAll();dc.redrawAll();" style="display: none;">reset</a>
                <span class="reset" style="display: none;"></span>
                <div class = "clearfix"></div>
            </div>

        </div>

        <div id = "a2"  class = "pieChartContainer" style="left: 550px; top: 10px;  width: 260px; height: 300px;">
            <div id="a2-chart" class="pieChart">
                <span class="graphTitle">Protein target</span>
                <a class="reset" href="javascript:assayIdDimensionPieChart.filterAll();dc.redrawAll();" style="display: none;">reset</a>
                <span class="reset" style="display: none;"></span>
                <div class = "clearfix"></div>
            </div>

        </div>

        <div id = "a3"  class = "pieChartContainer" style="left: 820px; top: 10px; width: 260px; height: 300px;">
            <div id="a3-chart" class="pieChart">
                <span class="graphTitle">Assay type</span>
                <a class="reset" href="javascript:assayTypePieChart.filterAll();dc.redrawAll();" style="display: none;">reset</a>
                <span class="reset" style="display: none;"></span>
                <div class = "clearfix"></div>
            </div>

        </div>
        <div id = "a4"  class = "resetDrillButtons" style="left: 1090px; top: 10px; width: 260px; height: 300px;">
        </div>

</div>


     %{--I will leave off the following section until I can figure out how to make data count work--}%
    %{--<div id="data-count">--}%
        %{--<span class="filter-count"></span> selected out of <span class="total-count"></span> records--}%
    %{--</div>--}%



</div>




%{--</script>--}%

<table id="data-table" class="table table-hover dc-data-table"  style="position:absolute; left: 0px; top: 300px;">
    <thead>
    <tr class="header">
        <th style='width: 23%' class="data-table-th">Biological process</th>
        <th style='width: 23%' class="data-table-th">Assay format</th>
        <th style='width: 23%' class="data-table-th">Proteins target</th>
        <th style='width: 23%' class="data-table-th">Assay type</th>
        <th style='width: 8%' class="data-table-th">ID</th>
    </tr>
    </thead>
</table>

<div id="widthTest" class="legendLine"></div>


<script>
    //        var $data = [{"name":"/", "ac":"0", "inac":"0", "children": [
    //                     ...
    var minimumValue=0;
    var maximumValue=1;

    var continuousColorScale = d3.scale.linear()
            .domain([0, 1])
            .interpolate(d3.interpolateRgb)
            .range(["#deffd9", "#74c476"]);

</script>


<div id="suburst_container" class="container-fluid" style="position:absolute; left: 10px; top: 1000px;">
    <div class="row-fluid">
        <div class="span6">

            %{--<a href="${createLink(controller: 'BardWebInterface', action: 'index')}">--}%
                %{--<img src="${resource(dir: 'images', file: 'bard_logo_med.png')}" alt="BioAssay Research Database"/>--}%
            %{--</a>--}%

        </div>

        <div class="span6" style="text-align: center; vertical-align: bottom;">
            %{--<br/>--}%
            %{--<h2>Panther class hierarchy</h2>--}%
        </div>

    </div>

    %{--<g:bigSunburstData compoundSummaryPlusId="${compoundSummaryPlusId}" cid="${cid}"/>--}%

    <div class="row-fluid">
        <div class="span9 pull-left">

            <div id="sunburstdiv">
                %{--// Sunburst goes here!--}%
            </div>

        </div>

        <div class="span3" style="padding-top: 50px;  height: 600px;">
            <div style="float:right;">
                <div id="legendGoesHere"></div>
               %{--<g:sunburstLegend/>--}%
            </div>

            <div style="text-align: center; vertical-align: bottom;">

                <select id="coloringOptions" style="visibility: hidden">
                    <option value="1"
                            <g:if test="${dropDown2Choice == 1}">selected</g:if>>Color by activity</option>
                    <option value="2"
                            <g:if test="${dropDown2Choice == 2}">selected</g:if>>Split classes by activity</option>
                    <option value="3" <g:if test="${dropDown2Choice == 3}">selected</g:if>>Color by class</option>
                </select>
                <div  style="padding-top: 320px;"></div>
                <select id="activity" style="visibility: hidden">
                        <option value="1" <g:if test="${dropDown1Choice == 1}">selected</g:if>>Active only</option>
                        <option value="2" <g:if test="${dropDown1Choice == 2}">selected</g:if>>Inactive only</option>
                        <option value="3"
                                <g:if test="${dropDown1Choice == 3}">selected</g:if>>Active and Inactive</option>
                </select>

            </div>

        </div>
    </div>
</div>

</body>
</html>