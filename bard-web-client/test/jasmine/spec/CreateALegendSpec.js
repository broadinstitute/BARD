describe("Testing createALegend.js", function () {

    describe('legend smoke test -- range non-zero', function () {
        it('start by building one', function () {
            var  colorMock  = function () {return 47;}
            var  domSelectorMock = {};
            var d3SubstituteMock = {} ;
            d3SubstituteMock.attr =  (function () {return d3SubstituteMock;});
            d3SubstituteMock.html =  (function () {return d3SubstituteMock;});
            d3SubstituteMock.append =  (function () {return d3SubstituteMock;});
            d3SubstituteMock.selectAll =  (function () {return d3SubstituteMock;});
            d3SubstituteMock.data =  (function () {return d3SubstituteMock;});
            d3SubstituteMock.enter =  (function () {return d3SubstituteMock;});
            d3SubstituteMock.style =  (function () {return d3SubstituteMock;});
            d3SubstituteMock.text =  (function () {return d3SubstituteMock;});
            spyOn(d3,'select').andCallFake(function (){return d3SubstituteMock;});
            var myLegend =  createALegend (100, 100, 4,colorMock,domSelectorMock, 1, 2);
            expect (!(myLegend === undefined)).toBeTruthy()  ;
       });
    });


    describe('legend smoke test -- zero range', function () {
        it('start by building one', function () {
            var  colorMock  = function () {return 47;}
            var  domSelectorMock = {};
            var d3SubstituteMock = {} ;
            d3SubstituteMock.attr =  (function () {return d3SubstituteMock;});
            d3SubstituteMock.html =  (function () {return d3SubstituteMock;});
            d3SubstituteMock.append =  (function () {return d3SubstituteMock;});
            spyOn(d3,'select').andCallFake(function (){return d3SubstituteMock;});
            var myLegend =  createALegend (100, 100, 4,colorMock,domSelectorMock, 1, 1);
            expect (true)  ;
        });
    });



    describe('test the attributes of a non-zero range legend', function () {
        var legend, fixture, colorMock;

        beforeEach(function (){
            colorMock  = function () {return 47;}
            legend =  createALegend (0, 0, 4, colorMock, '#legendGoesHere', 1, 2);
            fixture  = d3.select('#legendGoesHere');
        });
        afterEach(function (){
            fixture.remove();
        });

        it('test qualities', function () {
            // do we have any sort of legend
            expect (fixture.select('#legendHolder')).toBeDefined(1);

            // do we have a legend with values
            expect (fixture.select('.legend')).toBeDefined(1);

            // do we have a legend describing a zero dynamic range
            expect (fixture.select('.legendExplanation').empty()).toBeTruthy();
        });
    });




    describe('test the attributes of a zero range legend', function () {
        var legend, fixture, colorMock;

        beforeEach(function (){
            colorMock  = function () {return 47;}
            legend =  createALegend (0, 0, 4, colorMock, '#legendGoesHere', 1, 1);
            fixture  = d3.select('#legendGoesHere');
        });
        afterEach(function (){
            fixture.remove();
        });

        it('test qualities', function () {
            // do we have any sort of legend
            expect (fixture.select('#legendHolder')).toBeDefined(1);

            // do we have a legend with values
            expect (fixture.select('.legend').empty()).toBeTruthy();

            // do we have a legend describing a zero dynamic range
            expect (fixture.select('.legendExplanation')).toBeDefined(1);
        });
    });


});
