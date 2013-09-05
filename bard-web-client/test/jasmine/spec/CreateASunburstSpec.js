describe("Testing createASunburst.js", function () {

    // can we build a Mocked up sunburst?
    describe('Sunburst smoke test', function () {
        it('start by building one', function () {
            var  colorMock  = function () {return 47;}
            var  domSelectorMock = {};
            var d3SubstituteMock = {} ;
            var d3ArcMock = {};
            d3ArcMock.startAngle  =  (function () {return d3ArcMock;});
            d3ArcMock.endAngle  =  (function () {return d3ArcMock;});
            d3ArcMock.innerRadius  =  (function () {return d3ArcMock;});
            d3ArcMock.outerRadius  =  (function () {return d3ArcMock;});
            d3SubstituteMock.attr =  (function () {return d3SubstituteMock;});
            d3SubstituteMock.html =  (function () {return d3SubstituteMock;});
            d3SubstituteMock.append =  (function () {return d3SubstituteMock;});
            d3SubstituteMock.selectAll =  (function () {return d3SubstituteMock;});
            d3SubstituteMock.data =  (function () {return d3SubstituteMock;});
            d3SubstituteMock.enter =  (function () {return d3SubstituteMock;});
            d3SubstituteMock.style =  (function () {return d3SubstituteMock;});
            d3SubstituteMock.text =  (function () {return d3SubstituteMock;});
            d3SubstituteMock.datum =  (function () {return d3SubstituteMock;});
            d3SubstituteMock.classed =  (function () {return d3SubstituteMock;});
            d3SubstituteMock.on =  (function () {return d3SubstituteMock;});
            spyOn(d3,'select').andCallFake(function (){return d3SubstituteMock;});
            spyOn(d3.svg,'arc').andCallFake(function (){return d3ArcMock;});
            spyOn(linkedVizData,'filteredHierarchyData').andCallFake(function (){return d3SubstituteMock;});
            var mySunburst =  createASunburst (100, 100, 4,1000,colorMock,domSelectorMock, 1, 2);
            expect (mySunburst === undefined).toBeTruthy()  ;
        });
    });

    // can we build a Mocked up Color manager?
    describe('Color management smoke test', function () {
        it('start by building one', function () {
            var  colorManagementRoutines  = new ColorManagementRoutines ();
            expect (!(colorManagementRoutines === undefined)).toBeTruthy()  ;
        });
    });


    // can we build a Mocked up Tooltip handler?
    describe('Tooltip handler smoke test', function () {
        it('start by building one', function () {
            var  tooltipHandler  = new TooltipHandler ();
            expect (!(tooltipHandler === undefined)).toBeTruthy()  ;
        });
    });





});
