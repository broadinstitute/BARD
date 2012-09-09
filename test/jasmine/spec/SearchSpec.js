describe("Testing search.js", function () {

    beforeEach(function () {
    });

    describe("Test findSearchType", function () {
        var searchString;
        beforeEach(function () {
            searchString = "";
        });

        it("should return the string 'EMPTY', when we pass in an empty search string", function () {
            expect(findSearchType(searchString)).toEqual("EMPTY");
        });
        it("should return the string 'ID' when we pass in a list of comma separated integers", function () {
            searchString = "123,456,789";
            expect(findSearchType(searchString)).toEqual("ID");
        });
        it("should return the string 'ID' when we pass in a single integers", function () {
            searchString = "123";
            expect(findSearchType(searchString)).toEqual("ID");

            searchString = "AB,123,560";
            expect(findSearchType(searchString)).not.toEqual("ID");
        });
        it("should not return the string 'ID' when we pass in a list of comma separated values containing alphanumeric characters", function () {
            searchString = "1,2A,A123,560";
            expect(findSearchType(searchString)).not.toEqual("ID");
        });
        it("should return the string 'STRUCTURE' when we pass in Exact:CC", function () {
            searchString = "Exact:CC";
            expect(findSearchType(searchString)).toEqual("STRUCTURE");
        });
        it("should return the string 'STRUCTURE' when we pass in SubStructure:CC", function () {
            searchString = "Substructure:CC";
            expect(findSearchType(searchString)).toEqual("STRUCTURE");
        });
        it("should return the string 'STRUCTURE' when we pass in SuperStructure:CC", function () {

            searchString = "Superstructure:CC";
            expect(findSearchType(searchString)).toEqual("STRUCTURE");
        });
        it("should return the string 'STRUCTURE' when we pass in Similarity:CC", function () {
            searchString = "Similarity:CC";
            expect(findSearchType(searchString)).toEqual("STRUCTURE");
        });
        it("should return the string 'STRUCTURE' when we pass in one of 'eXact:CC','subSTRUCTURE:CC','superstructure:CC','similarity:CC', case insensitive", function () {
            searchString = "eXact:CC";
            expect(findSearchType(searchString)).toEqual("STRUCTURE");

            searchString = "subSTRUCTURE:CC";
            expect(findSearchType(searchString)).toEqual("STRUCTURE");

            searchString = "superstructure:CC";
            expect(findSearchType(searchString)).toEqual("STRUCTURE");

            searchString = "similarity:CC";
            expect(findSearchType(searchString)).toEqual("STRUCTURE");
        });
        it("should return the string 'STRUCTURE' when we pass in one of 'eXact:CC',case insensitive", function () {
            searchString = "eXact:CC";
            expect(findSearchType(searchString)).toEqual("STRUCTURE");
        });
        it("should return the string 'STRUCTURE' when we pass in 'subSTRUCTURE:CC', case insensitive", function () {

            searchString = "subSTRUCTURE:CC";
            expect(findSearchType(searchString)).toEqual("STRUCTURE");
        });
        it("should return the string 'STRUCTURE' when we pass in 'superstructure:CC'case, insensitive", function () {
            searchString = "superstructure:CC";
            expect(findSearchType(searchString)).toEqual("STRUCTURE");
        });
        it("should return the string 'STRUCTURE' when we pass in 'similarity:CC', case insensitive", function () {
            searchString = "similarity:CC";
            expect(findSearchType(searchString)).toEqual("STRUCTURE");
        });


        it("should return the string 'FREE_TEXT' for everything else, 'Bogus:Me' ", function () {
            searchString = "Bogus:Me";
            expect(findSearchType(searchString)).toEqual("FREE_TEXT");
        });
        it("should return the string 'FREE_TEXT' for everything else. '123,456:456'", function () {

            searchString = "123,456:456";
            expect(findSearchType(searchString)).toEqual("FREE_TEXT");
        });
        it("should return the string 'FREE_TEXT' for everything else. 'GO:BIOLOGICAL_PROCESS'", function () {
            searchString = "GO:BIOLOGICAL_PROCESS";
            expect(findSearchType(searchString)).toEqual("FREE_TEXT");
        });
        it("should return the string 'FREE_TEXT' for everything else. 'ADID:1,234,567'", function () {
            searchString = "ADID:1,234,567";
            expect(findSearchType(searchString)).toEqual("FREE_TEXT");
        });
        it("should return the string 'FREE_TEXT' for everything else. 'PID:1,234,567'", function () {
            searchString = "PID:1,234,567";
            expect(findSearchType(searchString)).toEqual("FREE_TEXT");
        });
        it("should return the string 'FREE_TEXT' for everything else. 'CID:1,234,567'", function () {
            searchString = "CID:1,234,567";
            expect(findSearchType(searchString)).toEqual("FREE_TEXT");
        });
    });

    describe("Test handleAllIdSearches()", function () {
        it("should call the handleSearch function 3 times", function () {
            spyOn(window, "handleSearch");
            handleAllIdSearches();
            expect(handleSearch).toHaveBeenCalled();
            expect(handleSearch.calls.length).toEqual(3);
            expect(handleSearch).toHaveBeenCalledWith('/bardwebquery/bardWebInterface/searchAssaysByIDs', 'searchForm', 'assaysTab', 'totalAssays', 'Assay Definitions ', 'assays');
            expect(handleSearch).toHaveBeenCalledWith('/bardwebquery/bardWebInterface/searchCompoundsByIDs', 'searchForm', 'compoundsTab', 'totalCompounds', 'Compounds ', 'compounds');
            expect(handleSearch).toHaveBeenCalledWith('/bardwebquery/bardWebInterface/searchProjectsByIDs', 'searchForm', 'projectsTab', 'totalProjects', 'Projects ', 'projects');

            //should not have been called with any of the calls for a free text search
            expect(handleSearch).not.toHaveBeenCalledWith('searchProjects', 'projectsTab', 'totalProjects', 'Projects ', 'projects');
        });
    });

    describe("Test handleAllFreeTextSearches()", function () {
        it("should call the handleSearch function 3 times", function () {
            spyOn(window, "handleSearch");
            handleAllFreeTextSearches();
            expect(handleSearch).toHaveBeenCalled();
            expect(handleSearch.calls.length).toEqual(3);

            expect(handleSearch).toHaveBeenCalledWith('/bardwebquery/bardWebInterface/searchAssays', 'searchForm', 'assaysTab', 'totalAssays', 'Assay Definitions ', 'assays');
            expect(handleSearch).toHaveBeenCalledWith('/bardwebquery/bardWebInterface/searchCompounds', 'searchForm', 'compoundsTab', 'totalCompounds', 'Compounds ', 'compounds');
            expect(handleSearch).toHaveBeenCalledWith('/bardwebquery/bardWebInterface/searchProjects', 'searchForm', 'projectsTab', 'totalProjects', 'Projects ', 'projects');

            //should not have been called with any of the calls for an id search
            expect(handleSearch).not.toHaveBeenCalledWith('searchProjectsByIDs', 'searchForm', 'projectsTab', 'totalProjects', 'Projects ', 'projects');
        });
    });
    describe("Test resetTabsForStructureSearches()", function () {
        it("should call the activateTabs() once and deActivateTabs() twice", function () {
            spyOn(window, "activateTabs");
            spyOn(window, "deActivateTabs");

            //call the method under test
            resetTabsForStructureSearches();

            expect(activateTabs).toHaveBeenCalled();
            expect(activateTabs.calls.length).toEqual(1);

            expect(deActivateTabs).toHaveBeenCalled();
            expect(deActivateTabs.calls.length).toEqual(2);

            expect(activateTabs).toHaveBeenCalledWith('compoundsTab', 'compoundsTabLi', 'compounds', "Compounds ");
            expect(activateTabs).not.toHaveBeenCalledWith('assaysTab', 'assaysTabLi', 'assays', 'Assay Definitions (0)');

            expect(deActivateTabs).toHaveBeenCalledWith('assaysTab', 'assaysTabLi', 'assays', 'Assay Definitions (0)');
            expect(deActivateTabs).toHaveBeenCalledWith('projectsTab', 'projectsTabLi', 'projects', 'Projects (0)');
            expect(deActivateTabs).not.toHaveBeenCalledWith('compoundsTab', 'compoundsTabLi', 'compounds', "Compounds ");

        });
    });
    describe("Test handleFormSubmit()", function () {
        var searchString;
        beforeEach(function () {
            searchString = "Some Text";
            spyOn(window, "handleAllFreeTextSearches");
            spyOn(window, "handleAllIdSearches");
            spyOn(window, "handleStructureSearch");

        });
        it("should verify that only handleAllFreeTextSearches() is called", function () {
            spyOn(window, "findSearchType").andReturn("FREE_TEXT");

            //call the method under test
            handleMainFormSubmit(searchString);

            expect(findSearchType).toHaveBeenCalled();
            expect(findSearchType.calls.length).toEqual(1);
            expect(findSearchType).toHaveBeenCalledWith(searchString);


            expect(handleAllFreeTextSearches).toHaveBeenCalled();
            expect(handleAllFreeTextSearches.calls.length).toEqual(1);
            expect(handleAllFreeTextSearches).toHaveBeenCalledWith();

            expect(handleAllIdSearches).not.toHaveBeenCalled();
            expect(handleAllIdSearches.calls.length).toEqual(0);

            expect(handleStructureSearch).not.toHaveBeenCalled();
            expect(handleStructureSearch.calls.length).toEqual(0);
        });
        it("should verify that only handleAllIdSearches() is called", function () {
            spyOn(window, "findSearchType").andReturn("ID");
            //call the method under test
            handleMainFormSubmit(searchString);

            expect(findSearchType).toHaveBeenCalled();
            expect(findSearchType.calls.length).toEqual(1);
            expect(findSearchType).toHaveBeenCalledWith(searchString);


            expect(handleAllFreeTextSearches).not.toHaveBeenCalled();
            expect(handleAllFreeTextSearches.calls.length).toEqual(0);

            expect(handleAllIdSearches).toHaveBeenCalled();
            expect(handleAllIdSearches.calls.length).toEqual(1);
            expect(handleAllIdSearches).toHaveBeenCalledWith();


            expect(handleStructureSearch).not.toHaveBeenCalled();
            expect(handleStructureSearch.calls.length).toEqual(0);
        });
        it("should verify that only handleAllIdSearches() is called", function () {
            spyOn(window, "findSearchType").andReturn("STRUCTURE");
            //call the method under test
            handleMainFormSubmit(searchString);

            expect(findSearchType).toHaveBeenCalled();
            expect(findSearchType.calls.length).toEqual(1);
            expect(findSearchType).toHaveBeenCalledWith(searchString);


            expect(handleAllFreeTextSearches).not.toHaveBeenCalled();
            expect(handleAllFreeTextSearches.calls.length).toEqual(0);

            expect(handleAllIdSearches).not.toHaveBeenCalled();
            expect(handleAllIdSearches.calls.length).toEqual(0);

            expect(handleStructureSearch).toHaveBeenCalled();
            expect(handleStructureSearch.calls.length).toEqual(1);
            expect(handleStructureSearch).toHaveBeenCalledWith('/bardwebquery/bardWebInterface/searchStructures', 'searchForm');

        });
    });

    describe("test activateTabs()", function () {
        it("Should activate assay definitions tab", function () {
            var tabDisplayString = 'Assay Definitions (10)';

            //verify that the string does no
            expect($('#assaysTab')).not.toHaveText(tabDisplayString);
            appendSetFixtures('<li id="assaysTab"></li>');
            appendSetFixtures('<li id="assays"></li>');
            appendSetFixtures('<li id="assaysTabLi"></li>');

            activateTabs('assaysTab', 'assaysTabLi', 'assays', tabDisplayString);
            expect($('#assaysTab')).toHaveText(tabDisplayString);
            expect($('#assays')).toBeEmpty();
            expect($('#assaysTabLi')).toHaveClass('active')

        });
        it("Should activate compounds tab", function () {
            var tabDisplayString = 'Compounds (10)';

            //verify that the string does no
            expect($('#assaysTab')).not.toHaveText(tabDisplayString);
            appendSetFixtures('<li id="compoundsTab"></li>');
            appendSetFixtures('<li id="compounds"></li>');
            appendSetFixtures('<li id="compoundsTabLi"></li>');

            activateTabs('compoundsTab', 'compoundsTabLi', 'compounds', tabDisplayString);
            expect($('#compoundsTab')).toHaveText(tabDisplayString);
            expect($('#compounds')).toBeEmpty();
            expect($('#compoundsTabLi')).toHaveClass('active')
        });

        it("Should activate projects tab", function () {
            var tabDisplayString = 'Projects (10)';

            //verify that the string does no
            expect($('#projectsTab')).not.toHaveText(tabDisplayString);
            appendSetFixtures('<li id="projectsTab"></li>');
            appendSetFixtures('<li id="projects"></li>');
            appendSetFixtures('<li id="projectsTabLi"></li>');

            activateTabs('projectsTab', 'projectsTabLi', 'projects', tabDisplayString);
            expect($('#projectsTab')).toHaveText(tabDisplayString);
            expect($('#projects')).toBeEmpty();
            expect($('#projectsTabLi')).toHaveClass('active')
        });
    });
    describe("test deActivateTabs()", function () {
        it("Should deactivate assay definitions tab", function () {
            var tabDisplayString = 'Assay Definitions (0)';

            //verify that the string does no
            expect($('#assaysTab')).not.toHaveText(tabDisplayString);
            appendSetFixtures('<li id="assaysTab"></li>');
            appendSetFixtures('<li id="assays"></li>');
            appendSetFixtures('<li id="assaysTabLi"></li>');

            deActivateTabs('assaysTab', 'assaysTabLi', 'assays', tabDisplayString);
            expect($('#assaysTab')).toHaveText(tabDisplayString);
            expect($('#assays')).toBeEmpty();
            expect($('#assaysTabLi')).not.toHaveClass('active')

        });
        it("Should deactivate compounds tab", function () {
            var tabDisplayString = 'Compounds (0)';

            //verify that the string does no
            expect($('#assaysTab')).not.toHaveText(tabDisplayString);
            appendSetFixtures('<li id="compoundsTab"></li>');
            appendSetFixtures('<li id="compounds"></li>');
            appendSetFixtures('<li id="compoundsTabLi"></li>');

            deActivateTabs('compoundsTab', 'compoundsTabLi', 'compounds', tabDisplayString);
            expect($('#compoundsTab')).toHaveText(tabDisplayString);
            expect($('#compounds')).toBeEmpty();
            expect($('#compoundsTabLi')).not.toHaveClass('active')
        });

        it("Should deactivate projects tab", function () {
            var tabDisplayString = 'Projects (0)';

            //verify that the string does no
            expect($('#projectsTab')).not.toHaveText(tabDisplayString);
            appendSetFixtures('<li id="projectsTab"></li>');
            appendSetFixtures('<li id="projects"></li>');
            appendSetFixtures('<li id="projectsTabLi"></li>');

            deActivateTabs('projectsTab', 'projectsTabLi', 'projects', tabDisplayString);
            expect($('#projectsTab')).toHaveText(tabDisplayString);
            expect($('#projects')).toBeEmpty();
            expect($('#projectsTabLi')).not.toHaveClass('active')
        });
    });
    describe("test handleFilteredQuery()", function () {
        var searchString;

        beforeEach(function () {
            spyOn(window, "handleSearch");
            spyOn(window, "handleStructureSearch");
            searchString = ""
        });


        it("Should handle empty String", function () {
            spyOn(window, "findTheAppropriateControllerActionForRequest").andReturn("EMPTY");
            handleFilteredQuery(searchString, 'facetFormType', 'currentFormId', 'currentTabId', 'numberOfHitsDivId', 'updateId', 'tabDisplayPrefix');
            expect(handleSearch).not.toHaveBeenCalled();
            expect(handleStructureSearch).not.toHaveBeenCalled();
        });

        it("Should handle Structure String", function () {
            searchString = "Stuff";
            spyOn(window, "findTheAppropriateControllerActionForRequest").andReturn("structureSearch");
            handleFilteredQuery(searchString, 'facetFormType', 'currentFormId', 'currentTabId', 'numberOfHitsDivId', 'updateId', 'tabDisplayPrefix');
            expect(handleSearch).not.toHaveBeenCalled();
            expect(handleStructureSearch).toHaveBeenCalledWith('/bardwebquery/bardWebInterface/searchStructures', 'currentFormId');
        });
        it("Should handle any other search", function () {
            searchString = "Stuff";
            spyOn(window, "findTheAppropriateControllerActionForRequest").andReturn("Default");
            handleFilteredQuery(searchString, 'facetFormType', 'currentFormId', 'currentTabId', 'numberOfHitsDivId', 'updateId', 'tabDisplayPrefix');
            expect(handleSearch).toHaveBeenCalledWith("/bardwebquery/bardWebInterface/Default", 'currentFormId', 'currentTabId', 'numberOfHitsDivId', 'tabDisplayPrefix', 'updateId');
            expect(handleStructureSearch).not.toHaveBeenCalled();
        });
    });

    describe("test handlePaging()", function () {
        var url;

        beforeEach(function () {
            spyOn(window, "handleSearch");
            spyOn(window, "handleStructureSearch");
            url = "";
        });
        it("Should call method with an empty url", function () {
            handlePaging(url);
            expect(handleSearch).not.toHaveBeenCalled();
            expect(handleStructureSearch).not.toHaveBeenCalled();
        });
        it("Should Call method with 'searchAssays'", function () {
            url = 'searchAssays';
            handlePaging(url);
            expect(handleSearch).toHaveBeenCalledWith(url, 'searchForm', 'assaysTab', 'totalAssays', 'Assay Definitions ', 'assays');
            expect(handleStructureSearch).not.toHaveBeenCalled();

        });
        it("Should Call method with 'searchProjects'", function () {
            url = 'searchProjects';
            handlePaging(url);
            expect(handleSearch).toHaveBeenCalledWith(url, 'searchForm', 'projectsTab', 'totalProjects', 'Projects ', 'projects');
            expect(handleStructureSearch).not.toHaveBeenCalled();

        });
        it("Should Call method with 'searchCompounds'", function () {
            url = 'searchCompounds';
            handlePaging(url);
            expect(handleSearch).toHaveBeenCalledWith(url, 'searchForm', 'compoundsTab', 'totalCompounds', 'Compounds ', 'compounds');
            expect(handleStructureSearch).not.toHaveBeenCalled();

        });
        it("Should Call method with 'searchStructures'", function () {
            url = 'searchStructures';
            handlePaging(url);
            expect(handleStructureSearch).toHaveBeenCalledWith(url, 'searchForm');
            expect(handleSearch).not.toHaveBeenCalledWith();
        });
        it("Should Call method with some unhandled string'", function () {
            url = 'bogus';
            handlePaging(url);
            expect(handleSearch).not.toHaveBeenCalled();
            expect(handleStructureSearch).not.toHaveBeenCalled();
        });
    });
    describe("test findTheAppropriateControllerActionForRequest()", function () {
        var searchString;
        var facetFormType;
        var searchType;
        beforeEach(function () {
            facetFormType = "";
            searchType = "";
            searchString = "";
            spyOn(window, "findTheAppropriateControllerActionFromFacetType")
        });
        it("Should handle Free Text Search", function () {
            searchString = "Free";
            facetFormType = "Text";
            searchType = "FREE_TEXT";
            spyOn(window, "findSearchType").andReturn(searchType);
            findTheAppropriateControllerActionForRequest(searchString, facetFormType);

            expect(findSearchType).toHaveBeenCalledWith(searchString);
            expect(findTheAppropriateControllerActionFromFacetType).toHaveBeenCalledWith(searchType, facetFormType);
        });
        it("Should handle ID Search", function () {
            searchString = "123";
            facetFormType = "Text";
            searchType = "ID";

            spyOn(window, "findSearchType").andReturn(searchType);
            findTheAppropriateControllerActionForRequest(searchString, facetFormType);

            expect(findSearchType).toHaveBeenCalledWith(searchString);
            expect(findTheAppropriateControllerActionFromFacetType).toHaveBeenCalledWith(searchType, facetFormType);
        });
        it("Should handle Structure Search", function () {
            searchString = "Exact:CC";
            facetFormType = "Text";
            searchType = "STRUCTURE";
            spyOn(window, "findSearchType").andReturn(searchType);
            findTheAppropriateControllerActionForRequest(searchString, facetFormType);

            expect(findSearchType).toHaveBeenCalledWith(searchString);
            expect(findTheAppropriateControllerActionFromFacetType).toHaveBeenCalledWith(searchType, facetFormType);
        });
        it("Should handle Empty", function () {
            searchString = "";
            facetFormType = "Text";
            searchType = "EMPTY";
            spyOn(window, "findSearchType").andReturn(searchType);
            findTheAppropriateControllerActionForRequest(searchString, facetFormType);

            expect(findSearchType).toHaveBeenCalledWith(searchString);
            expect(findTheAppropriateControllerActionFromFacetType).toHaveBeenCalledWith(searchType, facetFormType);

        });
    });


    describe("test findTheAppropriateControllerActionFromFacetType()", function () {
        var facetFormType;
        var searchType;
        beforeEach(function () {
            facetFormType = "";
            searchType = "";
        });

        it("Should handle Empty Search Type", function () {
            var controllerAction = findTheAppropriateControllerActionFromFacetType(searchType, facetFormType);
            expect(controllerAction).toEqual("EMPTY");
        });
        it("Should handle Empty Form Type", function () {
            searchType = "Some String";
            var controllerAction = findTheAppropriateControllerActionFromFacetType(searchType, facetFormType);
            expect(controllerAction).toEqual("EMPTY");
        });
        it("Should handle Free Text and AssayFacetForm type", function () {
            facetFormType = "AssayFacetForm";
            searchType = "FREE_TEXT";
            var controllerAction = findTheAppropriateControllerActionFromFacetType(searchType, facetFormType);
            expect(controllerAction).toEqual("searchAssays");
        });
        it("Should handle Free Text and ProjectFacetForm type", function () {
            facetFormType = "ProjectFacetForm";
            searchType = "FREE_TEXT";
            var controllerAction = findTheAppropriateControllerActionFromFacetType(searchType, facetFormType);
            expect(controllerAction).toEqual("searchProjects");
        });
        it("Should handle Free Text and CompoundFacetForm type", function () {
            facetFormType = "CompoundFacetForm";
            searchType = "FREE_TEXT";
            var controllerAction = findTheAppropriateControllerActionFromFacetType(searchType, facetFormType);
            expect(controllerAction).toEqual("searchCompounds");
        });
        it("Should handle ID and AssayFacetForm type", function () {
            facetFormType = "AssayFacetForm";
            searchType = "ID";
            var controllerAction = findTheAppropriateControllerActionFromFacetType(searchType, facetFormType);
            expect(controllerAction).toEqual("searchAssaysByIDs");
        });
        it("Should handle ID and ProjectFacetForm type", function () {
            facetFormType = "ProjectFacetForm";
            searchType = "ID";
            var controllerAction = findTheAppropriateControllerActionFromFacetType(searchType, facetFormType);
            expect(controllerAction).toEqual("searchProjectsByIDs");
        });
        it("Should handle ID and CompoundFacetForm type", function () {
            facetFormType = "CompoundFacetForm";
            searchType = "ID";
            var controllerAction = findTheAppropriateControllerActionFromFacetType(searchType, facetFormType);
            expect(controllerAction).toEqual("searchCompoundsByIDs");
        });
        it("Should handle STRUCTURE and AssayFacetForm type", function () {
            facetFormType = "AssayFacetForm";
            searchType = "STRUCTURE";
            var controllerAction = findTheAppropriateControllerActionFromFacetType(searchType, facetFormType);
            expect(controllerAction).toEqual("EMPTY");
        });
        it("Should handle STRUCTURE and ProjectFacetForm type", function () {
            facetFormType = "ProjectFacetForm";
            searchType = "STRUCTURE";
            var controllerAction = findTheAppropriateControllerActionFromFacetType(searchType, facetFormType);
            expect(controllerAction).toEqual("EMPTY");
        });
        it("Should handle STRUCTURE and CompoundFacetForm type", function () {
            facetFormType = "CompoundFacetForm";
            searchType = "STRUCTURE";
            var controllerAction = findTheAppropriateControllerActionFromFacetType(searchType, facetFormType);
            expect(controllerAction).toEqual("structureSearch");
        });
        it("Should handle Any Other String and facet type", function () {
            facetFormType = "CompoundFacetForm";
            searchType = "I_DO_NOT_EXIST";
            var controllerAction = findTheAppropriateControllerActionFromFacetType(searchType, facetFormType);
            expect(controllerAction).toEqual("EMPTY");
        });
    });
    describe("test handleSearch()", function () {
        var fakeData;
        var errorData;

        beforeEach(function () {
            fakeData = "Fake Data From Server";
            errorData = "You can put your errorData Here";

            appendSetFixtures('<li id="tabId"></li>');
            appendSetFixtures('<li id="currentFormId"></li>');
            appendSetFixtures('<li id="totalHitsForResourceId"></li>');
            appendSetFixtures('<li id="updateDiv"></li>');

        });

        it("Should be a successful call", function () {
            //mocking ajax call with Jasmine Spies
            spyOn($, "ajax").andCallFake(function (params) {
                params.success(fakeData);
            });

            //we expect these to be empty
            expect($('#tabId')).toBeEmpty();
            expect($('#updateDiv')).toBeEmpty();

            handleSearch('controllerAction', 'currentFormId', 'tabId', 'totalHitsForResourceId', 'displayStringPrefix', 'updateDiv');

            //do assertions
            expect($('#tabId')).toHaveText('displayStringPrefix(0)');
            expect($('#updateDiv')).toHaveText(fakeData);
        });

        it("Should be an Error condition", function () {
            //mocking ajax call with Jasmine Spies
            spyOn($, "ajax").andCallFake(function (params) {
                params.error(errorData)
            });

            //we expect these to be empty
            expect($('#tabId')).toBeEmpty();
            expect($('#updateDiv')).toBeEmpty();

            handleSearch('controllerAction', 'currentFormId', 'tabId', 'totalHitsForResourceId', 'displayStringPrefix', 'updateDiv');
            //do assertions
            expect($('#tabId')).toHaveText('displayStringPrefix');

            //Here is what we get back in the error. displayStringPrefix<img src="" class="icon-exclamation-sign" alt="error" height="16" width="16">
            expect($('#tabId')).toContain('img.icon-exclamation-sign');
            expect($('#updateDiv')).toBeEmpty();
        });
    });
    describe("test handleStructureSearch()", function () {
        var fakeData;
        var errorData;

        beforeEach(function () {
            fakeData = "Fake Data From Server";
            errorData = "You can put your return data here";
            appendSetFixtures('<li id="tabId"></li>');
            appendSetFixtures('<li id="currentFormId"></li>');
            appendSetFixtures('<li id="totalHitsForResourceId"></li>');
            appendSetFixtures('<li id="updateDiv"></li>');

        });

        it("Should be a successful Call", function () {
            //we expect these to be empty
            expect($('#tabId')).toBeEmpty();
            expect($('#updateDiv')).toBeEmpty();

            //mocking ajax call with Jasmine Spies
            spyOn($, "ajax").andCallFake(function (params) {
                params.success(fakeData);
            });

            handleSearch('controllerAction', 'currentFormId', 'tabId', 'totalHitsForResourceId', 'displayStringPrefix', 'updateDiv');
            expect($('#tabId')).toHaveText('displayStringPrefix(0)');
            expect($('#updateDiv')).toHaveText(fakeData);

        });

        it("Should be an Error", function () {
            //we expect these to be empty
            expect($('#tabId')).toBeEmpty();
            expect($('#updateDiv')).toBeEmpty();

            //mocking ajax call with Jasmine Spies
            spyOn($, "ajax").andCallFake(function (params) {
                params.error(errorData);
            });

            handleSearch('controllerAction', 'currentFormId', 'tabId', 'totalHitsForResourceId', 'displayStringPrefix', 'updateDiv');
            //do assertions
            expect($('#tabId')).toHaveText('displayStringPrefix');

            //Here is what we get back in the error. displayStringPrefix<img src="" class="icon-exclamation-sign" alt="error" height="16" width="16">
            expect($('#tabId')).toContain('img.icon-exclamation-sign');
            expect($('#updateDiv')).toBeEmpty();

        });
    });
});
