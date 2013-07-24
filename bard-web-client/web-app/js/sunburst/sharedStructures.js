
var sharedStructures = (function () {
    var
        assayIndex = {},
        biologicalProcessPieChart,
        assayFormatPieChart,
        assayIdDimensionPieChart,
        assayTypePieChart,
        allDataDcTable,

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
        } ;


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

        setAssay:setAssay

    };
}());  // sharedStructures

