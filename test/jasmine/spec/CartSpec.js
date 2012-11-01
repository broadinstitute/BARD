describe("Testing cart.js", function () {

    describe("Test QueryCart.addItemToCartHandler", function() {
        var fakeData;
        beforeEach(function() {
            fakeData = "Fake Data From Server";
        });

        it("should call ajax and publish a change event upon success", function() {
            spyOn($, "ajax").andCallFake(function (params) {
                params.success(fakeData);
            });
            spyOn(queryCart, "publishCartChangeEvent");

            queryCart.addItemToCartHandler();

            expect($.ajax).toHaveBeenCalled();
            expect(queryCart.publishCartChangeEvent).toHaveBeenCalled();
        });
    });

    describe("Test QueryCart.removeItemFromCartHandler", function() {
        var fakeData;
        beforeEach(function() {
            fakeData = "Fake Data From Server";
        });

        it("should call ajax and publish a change event upon success", function() {
            spyOn($, "ajax").andCallFake(function (params) {
                params.success(fakeData);
            });
            spyOn(queryCart, "publishCartChangeEvent");

            queryCart.removeItemFromCartHandler();

            expect($.ajax).toHaveBeenCalled();
            expect(queryCart.publishCartChangeEvent).toHaveBeenCalled();
        });
    });

    describe("Test QueryCart.removeAll", function() {
        var fakeData;
        beforeEach(function() {
            fakeData = "Fake Data From Server";
        });

        it("should call ajax and publish a change event upon success", function() {
            spyOn($, "ajax").andCallFake(function (params) {
                params.success(fakeData);
            });
            spyOn(queryCart, "publishCartChangeEvent");

            queryCart.removeAll();

            expect($.ajax).toHaveBeenCalled();
            expect(queryCart.publishCartChangeEvent).toHaveBeenCalled();
        });
    });

    describe("Test QueryCart.toggleDetailsHandler", function(){

        beforeEach(function () {
            jQuery.fx.off = true;
            appendSetFixtures('<a class="trigger" href="#">Cart Trigger</a>');
            appendSetFixtures('<div class="panel"><div id="detailView">Test</div></div>');
            appendSetFixtures('<div id="summaryView"></div>');
        });

        afterEach(function() {
            jQuery.fx.off = false;
        });

        it("should switch from summary to details", function(){

            $(".panel").hide();

            queryCart.toggleDetailsHandler();

            expect($(".panel")).toBeVisible();
            expect($('.trigger')).toHaveClass('active');
        });

        it("should switch from details to summary", function(){

            $(".trigger").addClass("active");

            queryCart.toggleDetailsHandler();

            expect($(".panel")).toBeHidden();
            expect($('.trigger')).not.toHaveClass('active');
        });
    });

    describe("Test QueryCart.refreshSummaryView", function(){
        var fakeData;
        beforeEach(function() {
            fakeData = "Fake Data From Server";
        });

        it("should update the summary view", function(){

            appendSetFixtures('<div id="summaryView"></div>');

            spyOn($, "ajax").andCallFake(function (params) {
                params.success(fakeData);
            });

            queryCart.refreshSummaryView();

            expect($('#summaryView')).toHaveText(fakeData);
        });
    });

    describe("Test QueryCart.refreshDetailsView", function(){
        var fakeData;
        beforeEach(function() {
            fakeData = "Fake Data From Server";
        });

        it("should update the detail view", function(){

            appendSetFixtures('<div id="detailView"></div>');

            spyOn($, "ajax").andCallFake(function (params) {
                params.success(fakeData);
            });

            queryCart.refreshDetailsView();

            expect($('#detailView')).toHaveText(fakeData);
        });
    });

});
