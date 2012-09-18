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

    describe("Test QueryCart.toggleDetailsHandler", function(){
        var fakeData;
        beforeEach(function() {
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
            queryCart.toggleDetailsHandler();
            expect($(".panel")).toBeVisible();
            expect(ajaxLocation).toEqual('#detailView');
            expect(showingCartDetails).toEqual(1);
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
            queryCart.toggleDetailsHandler();
            expect($(".panel")).toBeHidden();
            expect(ajaxLocation).toEqual('#summaryView');
            expect(showingCartDetails).toEqual(0);
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

            queryCart.addAssayToCartHandler();

            expect($('#summaryView')).toHaveText(fakeData);
        });

        it("should update the detail view if it is showing", function(){
            spyOn($, "ajax").andCallFake(function (params) {
                params.success(fakeData);
            });
            showingCartDetails = true;
            ajaxLocation = '#detailView';

            queryCart.addAssayToCartHandler();

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

            queryCart.addCompoundToCartHandler();

            expect($('#summaryView')).toHaveText(fakeData);
        });

        it("should update the detail view if it is showing", function(){
            spyOn($, "ajax").andCallFake(function (params) {
                params.success(fakeData);
            });
            showingCartDetails = true;
            ajaxLocation = '#detailView';

            queryCart.addCompoundToCartHandler();

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

            queryCart.addProjectToCartHandler();

            expect($('#summaryView')).toHaveText(fakeData);
        });

        it("should update the detail view if it is showing", function(){
            spyOn($, "ajax").andCallFake(function (params) {
                params.success(fakeData);
            });
            showingCartDetails = true;
            ajaxLocation = '#detailView';

            queryCart.addProjectToCartHandler();

            expect($('#detailView')).toHaveText(fakeData);
        });
    });

    describe("Test QueryCart.removeItem", function(){
        var fakeData;
        beforeEach(function() {
            fakeData = "Fake Data From Server";
        });

        it("should update the detail view if it is showing", function(){
            spyOn($, "ajax").andCallFake(function (params) {
                params.success(fakeData);
            });

            queryCart.removeItem();

            expect($('#detailView')).toHaveText(fakeData);
        });

    });

    describe("Test QueryCart.removeAll", function(){
        var fakeData;
        beforeEach(function() {
            fakeData = "Fake Data From Server";
        });

        it("should update the detail view if it is showing", function(){
            spyOn($, "ajax").andCallFake(function (params) {
                params.success(fakeData);
            });

            queryCart.removeAll();

            expect($('#detailView')).toHaveText(fakeData);
        });

    });

});
