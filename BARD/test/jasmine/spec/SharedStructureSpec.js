describe("Testing SharedStructure.js", function () {

    /***
     * The SharedStructure module is a very simple one. It's holding variables
     * that need to be shared across other modules so that I can avoid globals,
     * and also providing a wrapper around a couple of library calls.
     */
    describe('AssayIndex getter and setter', function () {
        it('sets assay index and then retrieves the value', function () {
            expect (sharedStructures.setAssayIndex(47)==undefined);
            expect(sharedStructures.getAssayIndex()).toEqual(47);
        });
    });

    describe('AssayIdDimensionPieChart setter and accessor', function () {
        it('sets AssayIdDimensionPieChart', function () {
            var  myAssayIdDimensionPieChart  = {};
            myAssayIdDimensionPieChart.filterAll = jasmine.createSpy('filter all spy');
            spyOn(dc,'redrawAll');
            expect(sharedStructures.setAssayIdDimensionPieChart(myAssayIdDimensionPieChart)==undefined);
            sharedStructures.resetAssayIdDimensionPieChart();
            expect(myAssayIdDimensionPieChart.filterAll).toHaveBeenCalled();
            expect(dc.redrawAll).toHaveBeenCalled();

        });

        it('sets AssayFormatPieChart', function () {
            var  myAssayFormatPieChart  = {};
            myAssayFormatPieChart.filterAll = jasmine.createSpy('filter all spy');
            spyOn(dc,'redrawAll');
            expect(sharedStructures.setAssayFormatPieChart(myAssayFormatPieChart)==undefined);
            sharedStructures.resetAssayFormatPieChart();
            expect(myAssayFormatPieChart.filterAll).toHaveBeenCalled();
            expect(dc.redrawAll).toHaveBeenCalled();
        });

        it('sets BiologicalProcessPieChart', function () {
            var  myBiologicalProcessPieChart  = {};
            myBiologicalProcessPieChart.filterAll = jasmine.createSpy('filter all spy');
            spyOn(dc,'redrawAll');
            expect(sharedStructures.setBiologicalProcessPieChart(myBiologicalProcessPieChart)==undefined);
            sharedStructures.resetBiologicalProcessPieChart();
            expect(myBiologicalProcessPieChart.filterAll).toHaveBeenCalled();
            expect(dc.redrawAll).toHaveBeenCalled();
        });

        it('sets AssayTypePieChart', function () {
            var  myAssayTypePieChart  = {};
            myAssayTypePieChart.filterAll = jasmine.createSpy('filter all spy');
            spyOn(dc,'redrawAll');
            expect(sharedStructures.setAssayTypePieChart(myAssayTypePieChart)==undefined);
            sharedStructures.resetAssayTypePieChart();
            expect(myAssayTypePieChart.filterAll).toHaveBeenCalled();
            expect(dc.redrawAll).toHaveBeenCalled();
        });

    });


});
