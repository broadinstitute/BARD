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


var sharedStructures = (function () {
    var
        assayIndex = {},
        biologicalProcessPieChart,
        assayFormatPieChart,
        assayIdDimensionPieChart,
        assayTypePieChart,
        allDataDcTable,
        widgetsGoHere,
        widgetWidthWithoutSpacing=21,
        widgetHeightWithTitle=240,
        displayWidgetX=0,
        displayWidgetY=300,
        displayWidgetWidth=1000,
        displayWidgetHeight=1000,
        compressedPos = [
            {'x': '0.5', 'y': 10},
            {'x': '22.5', 'y': 10},
            {'x': '44.5', 'y': 10},
            {'x': '66.5', 'y': 10}
        ],
        expandedPos = [
            {'x': '6', 'y': 10},
            {'x': '37', 'y': 10},
            {'x': '69', 'y': 10}
        ],



    // standard setters.   We need to save these variables
        setBiologicalProcessPieChart = function (localBiologicalProcessPieChart) {
            biologicalProcessPieChart = localBiologicalProcessPieChart;
        },
        setAssayFormatPieChart = function (localAssayFormatPieChart) {
            assayFormatPieChart = localAssayFormatPieChart;
        },
        setAssayIdDimensionPieChart = function (localAssayIdDimensionPieChart) {
            assayIdDimensionPieChart = localAssayIdDimensionPieChart;
        },
        setAssayTypePieChart = function (localAssayTypePieChart) {
            assayTypePieChart = localAssayTypePieChart;
        },
        setAllDataDcTable = function (localAllDataDcTable) {
            allDataDcTable = localAllDataDcTable;
        },


    // reset these variables. These methods are called from the reset button above the pie chart managed by DC.js
        resetBiologicalProcessPieChart = function () {
            biologicalProcessPieChart.filterAll();
            dc.redrawAll();
        },
        resetAssayFormatPieChart = function () {
            assayFormatPieChart.filterAll();
            dc.redrawAll();
        },
        resetAssayIdDimensionPieChart = function () {
            assayIdDimensionPieChart.filterAll();
            dc.redrawAll();
        },
        resetAssayTypePieChart = function () {
            assayTypePieChart.filterAll();
            dc.redrawAll();
        },

    // these are our cross filter variables.
        setAssay = function (localAssay) {
            assay = localAssay;
        },

    // these are our cross filter variables.
        setAssayIndex = function (localAssayIndex) {
            assayIndex = localAssayIndex;
        },
        getAssayIndex = function () {
            return assayIndex;
        },
        widgetsGoHere = function (){
            var placementInfo = [
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
            return placementInfo;
        };


    return {

        setBiologicalProcessPieChart: setBiologicalProcessPieChart,
        resetBiologicalProcessPieChart:resetBiologicalProcessPieChart,

        setAssayFormatPieChart: setAssayFormatPieChart,
        resetAssayFormatPieChart:resetAssayFormatPieChart,

        setAssayIdDimensionPieChart: setAssayIdDimensionPieChart,
        resetAssayIdDimensionPieChart:resetAssayIdDimensionPieChart,

        setAssayTypePieChart: setAssayTypePieChart,
        resetAssayTypePieChart:resetAssayTypePieChart,

        setAllDataDcTable:setAllDataDcTable,  //no reset needed – table is read only

        getAssayIndex: getAssayIndex,
        setAssayIndex: setAssayIndex,

        setAssay:setAssay,

        widgetsGoHere:widgetsGoHere
    };
}());  // sharedStructures
