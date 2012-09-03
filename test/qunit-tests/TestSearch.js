var searchString;



module("Test findSearchType()", {
    setup:function () {
        searchString = ""
    },
    teardown:function () {
    }
});
test("Should return 'EMPTY', given an empty string", function () {
    equal(findSearchType(searchString), 'EMPTY', "Should return the string 'EMPTY'");
});
test("Should return 'ID', given a comma separated list of numbers", function () {
    searchString = "123,456,789";
    equal(findSearchType(searchString), 'ID', "Should return the string 'ID'");
});
test("Should NOT return 'ID', given a list of comma separated values containing alphanumeric characters", function () {
    searchString = "1,2A,A123,560";
    notEqual(findSearchType(searchString), 'ID', "Should return the string 'ID'");
});
test("Should return 'ID', given a single integer", function () {
    searchString = "123";
    equal(findSearchType(searchString), 'ID', "Should return the string 'ID'");
});

test("Should return 'STRUCTURE' when we pass in the string 'Exact:CC'", function () {
    searchString = "Exact:CC";
    equal(findSearchType(searchString), 'STRUCTURE', "Should return the string 'STRUCTURE'");
});
test("should return the string 'STRUCTURE' when we pass in SubStructure:CC", function () {
    searchString = "Substructure:CC";
    equal(findSearchType(searchString), "STRUCTURE");
});
test("should return the string 'STRUCTURE' when we pass in SuperStructure:CC", function () {

    searchString = "Superstructure:CC";
    equal(findSearchType(searchString), "STRUCTURE");
});
test("should return the string 'STRUCTURE' when we pass in Similarity:CC", function () {
    searchString = "Similarity:CC";
    equal(findSearchType(searchString), "STRUCTURE");
});

test("should return the string 'STRUCTURE' when we pass in one of 'eXact:CC',case insensitive", function () {
    searchString = "eXact:CC";
    equal(findSearchType(searchString), "STRUCTURE");
});
test("should return the string 'STRUCTURE' when we pass in 'subSTRUCTURE:CC', case insensitive", function () {

    searchString = "subSTRUCTURE:CC";
    equal(findSearchType(searchString), "STRUCTURE");
});
test("should return the string 'STRUCTURE' when we pass in 'superstructure:CC'case, insensitive", function () {
    searchString = "superstructure:CC";
    equal(findSearchType(searchString), "STRUCTURE");
});
test("should return the string 'STRUCTURE' when we pass in 'similarity:CC', case insensitive", function () {
    searchString = "similarity:CC";
    equal(findSearchType(searchString), "STRUCTURE");
});


test("should return the string 'FREE_TEXT' for everything else, 'Bogus:Me' ", function () {
    searchString = "Bogus:Me";
    equal(findSearchType(searchString), "FREE_TEXT");
});
test("should return the string 'FREE_TEXT' for everything else. '123,456:456'", function () {

    searchString = "123,456:456";
    equal(findSearchType(searchString), "FREE_TEXT");
});
test("should return the string 'FREE_TEXT' for everything else. 'GO:BIOLOGICAL_PROCESS'", function () {
    searchString = "GO:BIOLOGICAL_PROCESS";
    equal(findSearchType(searchString), "FREE_TEXT");
});
test("should return the string 'FREE_TEXT' for everything else. 'ADID:1,234,567'", function () {
    searchString = "ADID:1,234,567";
    equal(findSearchType(searchString), "FREE_TEXT");
});
test("should return the string 'FREE_TEXT' for everything else. 'PID:1,234,567'", function () {
    searchString = "PID:1,234,567";
    equal(findSearchType(searchString), "FREE_TEXT");
});
test("should return the string 'FREE_TEXT' for everything else. 'CID:1,234,567'", function () {
    searchString = "CID:1,234,567";
    equal(findSearchType(searchString), "FREE_TEXT");
});

//==Global variables===
var tabDisplayString;
var $fixture;
module("Test deActivateTabs()", {
    setup:function () {
        $fixture = $("#qunit-fixture");
        tabDisplayString = "";
    },
    teardown:function () {
    }
});

test("deActivate assay definitions tab", function () {
    tabDisplayString = 'Assay Definitions (0)';
    $fixture.append('<li id="assaysTab"></li>');
    $fixture.append('<li id="assays"></li>');
    $fixture.append('<li id="assaysTabLi" class="active someclass"></li>');

    deActivateTabs('assaysTab', 'assaysTabLi', 'assays', tabDisplayString);
    var className = $('#assaysTabLi').attr('class');

    equal($('#assaysTab').html(), tabDisplayString, "Should be " + tabDisplayString);
    equal($('#assays').html(), '', "Should be Empty");
    equal(className, 'someclass', "Class name should be 'someclass' after we remove the 'active' class");
});

module("Test activateTabs()", {
    setup:function () {
        $fixture = $("#qunit-fixture");
        tabDisplayString = "";
    },
    teardown:function () {
    }
});

test("activate assay definitions tab", function () {
    var expectedTabDisplayString = 'Assay Definitions (10)<img src="/bardwebquery/static/images/loading_icon.gif" alt="loading" title="loading" height="16" width="16">';

    tabDisplayString = 'Assay Definitions (10)';
    $fixture.append('<li id="assaysTab"></li>');
    $fixture.append('<li id="assays"></li>');
    $fixture.append('<li id="assaysTabLi"></li>');

    activateTabs('assaysTab', 'assaysTabLi', 'assays', tabDisplayString);

    var className = $('#assaysTabLi').attr('class');
    equal($('#assaysTab').html(), expectedTabDisplayString, "Should be " + expectedTabDisplayString);
    equal($('#assays').html(), '', "Should be Empty");
    equal(className, 'active', "Class name should be 'active'");
});

module("Test resetTabsForStructureSearches()", {
    setup:function () {
        $fixture = $("#qunit-fixture");
    },
    teardown:function () {
        $fixture = $("#qunit-fixture");
    }
});
test("reset tabs", function () {

    var assaysDisplay = 'Assay Definitions (0)';
    var projectsDisplay = 'Projects (0)';

    //assays
    $fixture.append('<li id="assaysTab"></li>');
    $fixture.append('<li id="assays"></li>');
    $fixture.append('<li id="assaysTabLi" class="active assayclass"></li>');

    //projects
    $fixture.append('<li id="projectsTab"></li>');
    $fixture.append('<li id="projects"></li>');
    $fixture.append('<li id="projectsTabLi" class="active projectclass"></li>');

    //compounds
    var expectedCompoundsTabDisplay = 'Compounds <img src="/bardwebquery/static/images/loading_icon.gif" alt="loading" title="loading" height="16" width="16">';
    $fixture.append('<li id="compoundsTab"></li>');
    $fixture.append('<li id="compounds"></li>');
    $fixture.append('<li id="compoundsTabLi"></li>');

    resetTabsForStructureSearches();

    //assert compounds
    var compoundsClassName = $('#compoundsTabLi').attr('class');
    equal($('#compoundsTab').html(), expectedCompoundsTabDisplay, "Should be " + expectedCompoundsTabDisplay);
    equal($('#compounds').html(), '', "Should be Empty");
    equal(compoundsClassName, 'active', "Compounds Class name should be 'active'");

    //assert assays
    var assaysClassName = $('#assaysTabLi').attr('class');
    equal($('#assaysTab').html(), assaysDisplay, "Should be " + assaysDisplay);
    equal($('#assays').html(), '', "Should be Empty");
    equal(assaysClassName, 'assayclass', "Class name should be 'assayclass' after we remove the 'active' class");

    //assert projects
    var projectsClassName = $('#projectsTabLi').attr('class');
    equal($('#projectsTab').html(), projectsDisplay, "Should be " + projectsDisplay);
    equal($('#projects').html(), '', "Should be Empty");
    equal(projectsClassName, 'projectclass', "Class name should be 'projectclass' after we remove the 'active' class");

});
module("Test handleAllIdSearches()", {
    setup:function () {
    },
    teardown:function () {
    }
});
test("Search by ID", function () {
    expectCall(this, "handleSearch", 3); // expect the method call
    handleAllIdSearches();
});

module("Test handleAllFreeTextSearches()", {
    setup:function () {

    },
    teardown:function () {
    }
});
test("Search by Free Text Search", function () {
    expectCall(this, "handleSearch", 3); // expect the method call
    handleAllFreeTextSearches();
});