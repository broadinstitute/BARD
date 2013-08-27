
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
