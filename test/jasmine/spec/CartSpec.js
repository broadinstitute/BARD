describe("Testing cart.js", function () {

    beforeEach(function () {
        jQuery.fx.off = true;
        appendSetFixtures('<a class="trigger" href="#">Cart Trigger</a>');
        appendSetFixtures('<div class="panel"><div id="detailView">Test</div></div>');
        appendSetFixtures('<div id="summaryView"></div>');
    });

    afterEach(function() {
        jQuery.fx.off = false;
    });

    describe("Test wiring of event handlers", function(){
        it("should wire up the show details trigger", function(){
            var spyEvent = spyOnEvent('.trigger', 'click');

            $('.trigger').click();

            expect('click').toHaveBeenTriggeredOn('.trigger');
            expect(spyEvent).toHaveBeenTriggered();
        });
        it("should have all add buttons wired up", function(){
            appendSetFixtures('<a class="addAssayToCart" href="#">Add Assay</a>');
            spyOn(QueryCart, 'addAssayToCartHandler');
            $('.addAssayToCart').click();
            expect(QueryCart.addAssayToCartHandler).toHaveBeenCalled();
        });
    });

    describe("Test QueryCart.toggleDetailsHandler", function(){
        var mockEvent;
        var fakeData;
        beforeEach(function() {
            mockEvent = {
                target : $(".trigger").first()
            };
            fakeData = "Fake Data From Server";
        });

        it("should switch to the expanded view after toggle if summary was being displayed", function(){
            showingCartDetails = false;
            ajaxLocation = '#summaryView';
            $(".panel").hide();
            //mocking ajax call with Jasmine Spies
            spyOn($, "ajax").andCallFake(function (params) {
                params.success(fakeData);
            });
            QueryCart.toggleDetailsHandler(mockEvent);
            expect($(".panel")).toBeVisible();
            expect($(".trigger")).toHaveClass("active");
            expect(ajaxLocation).toEqual('#detailView');
            expect(showingCartDetails).toEqual(true);
            expect($('#detailView')).toHaveText(fakeData);
        });

        it("should switch to the summary view after toggle if details view was being displayed", function(){
            showingCartDetails = true;
            ajaxLocation = '#detailView';
            $(".trigger").addClass("active");
            //mocking ajax call with Jasmine Spies
            spyOn($, "ajax").andCallFake(function (params) {
                params.success(fakeData);
            });
            QueryCart.toggleDetailsHandler(mockEvent);
            expect($(".panel")).toBeHidden();
            expect($(".trigger")).not.toHaveClass("active");
            expect(ajaxLocation).toEqual('#summaryView');
            expect(showingCartDetails).toEqual(false);
            expect($('#summaryView')).toHaveText(fakeData);
        });
    });

    describe("Test QueryCart.addAssayToCartHandler", function(){
        var fakeData;
        beforeEach(function() {
            fakeData = "Fake Data From Server";
        });

        it("should update the summary view if it is showing", function(){
            spyOn($, "ajax").andCallFake(function (params) {
                params.success(fakeData);
            });
            showingCartDetails = false;
            ajaxLocation = '#summaryView';

            QueryCart.addAssayToCartHandler();

            expect($('#summaryView')).toHaveText(fakeData);
        });

        it("should update the detail view if it is showing", function(){
            spyOn($, "ajax").andCallFake(function (params) {
                params.success(fakeData);
            });
            showingCartDetails = true;
            ajaxLocation = '#detailView';

            QueryCart.addAssayToCartHandler();

            expect($('#detailView')).toHaveText(fakeData);
        });
    });

    describe("Test QueryCart.addCompoundToCartHandler", function(){
        var fakeData;
        beforeEach(function() {
            fakeData = "Fake Data From Server";
        });

        it("should update the summary view if it is showing", function(){
            spyOn($, "ajax").andCallFake(function (params) {
                params.success(fakeData);
            });
            showingCartDetails = false;
            ajaxLocation = '#summaryView';

            QueryCart.addCompoundToCartHandler();

            expect($('#summaryView')).toHaveText(fakeData);
        });

        it("should update the detail view if it is showing", function(){
            spyOn($, "ajax").andCallFake(function (params) {
                params.success(fakeData);
            });
            showingCartDetails = true;
            ajaxLocation = '#detailView';

            QueryCart.addCompoundToCartHandler();

            expect($('#detailView')).toHaveText(fakeData);
        });
    });

    describe("Test QueryCart.addProjectToCartHandler", function(){
        var fakeData;
        beforeEach(function() {
            fakeData = "Fake Data From Server";
        });

        it("should update the summary view if it is showing", function(){
            spyOn($, "ajax").andCallFake(function (params) {
                params.success(fakeData);
            });
            showingCartDetails = false;
            ajaxLocation = '#summaryView';

            QueryCart.addProjectToCartHandler();

            expect($('#summaryView')).toHaveText(fakeData);
        });

        it("should update the detail view if it is showing", function(){
            spyOn($, "ajax").andCallFake(function (params) {
                params.success(fakeData);
            });
            showingCartDetails = true;
            ajaxLocation = '#detailView';

            QueryCart.addProjectToCartHandler();

            expect($('#detailView')).toHaveText(fakeData);
        });
    });

});
