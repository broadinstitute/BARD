<html>
<head>
    <r:require modules="jqueryWizard,bootstrap,select2,handlebars"/>
    <meta name="layout" content="basic"/>
</head>

<body>

<div id="wizard-steps">
    <h2>Add item wizard</h2>

    <input type="hidden" value="assay protocol> assay component>" id="sectionPath">
    <input type="hidden" id="cardAssayContextId" value="209">
    <input type="hidden" id="cardAssayId" value="4250">

    <input type="hidden" id="attributeName" value="undefined">
    <input type="hidden" id="valueType" value="undefined">
    <input type="hidden" id="valueName" value="undefined">

    <script id="current-item-template" type="text/x-handlebars-template">
        <table class="gridtable">
            <thead>
            <tr>
                <th>Attribute</th>
                <th>Value Type</th>
                <th>Value</th>
            </tr>
            </thead>
            <tbody>
            <tr>
                <td>{{attribute}}</td>
                <td>{{valueType}}</td>
                <td>{{value}}</td>
            </tr>
            </tbody>
        </table>
    </script>

    <form name="wizard-steps" id="wrapped">

        <div id="search-attribute" class="step stop">
            <div class="itemWizardSelectionsTable"></div>

            <h1>Search for Attribute</h1>

            <input type="hidden" id="elementId" name="elementId" value=""/>
            <input type="hidden" id="path" name="path" value=""/>
            <input type="hidden" id="assayContextIdValue" name="assayContextIdValue"
                   value=""/>

            <span class="help-inline">Search for an item and then choose from auto-suggest to make it your current choice</span>

            <input type="hidden" id="attributeTextField" name="attributeId"/>
        </div>

        <div id="attribute-type" class="step">

            <div class="itemWizardSelectionsTable"></div>

            <h1>Value type defines the restriction that is placed on the values associated with the chosen attribute:</h1>

            <label class="radio">
                <input type="radio" name="valueTypeOption" value="Fixed">
                <strong>Fixed</strong> - Every experiment always has the same value for the attribute "cell line equals HeLa"
            </label>

            <label class="radio">
                <input type="radio" name="valueTypeOption" value="list">
                <strong>List</strong> - Every experiment has one of the entries in the list for the attribute "cell line one of
            HeLa, CHO, MM"
            </label>

            <label class="radio">
                <input type="radio" name="valueTypeOption" value="Range">
                <strong>Range</strong> - Every experiment has a value within the provided range for the attribute "cell density
            between 10 and 100 cells / well"
            </label>

            <label class="radio">
                <input type="radio" name="valueTypeOption" value="Free">
                <strong>Free</strong> - Every experiment must provide a value for the attribute, but there is no restriction on
            that value "cell density specified by experiment"
            </label>
        </div>

        <div id="select-fixed-value" class="step">
            <div class="itemWizardSelectionsTable"></div>

            <p>Search or Browse for a defined term to use as the value. Or, enter a number directly into the current choice. If
            relevant, choose the relevant units that describe the number entered.</p>

            <!-- This hidden control becomes the value selection box -->
            <input type="hidden" id="valueId" name="valueId">

            <!-- This hidden field is needed for passing state needed for the ontology query -->
            <input type="hidden" id="attributeElementId" value="">

            <label class="control-label" for="valueQualifier">
                Qualifier
            </label>

            <div class="controls">
                <select name="valueQualifier" id="valueQualifier">
                    <option></option>
                    <option>=</option>
                    <option>&lt;</option>
                    <option>&lt;=</option>
                    <option>&gt;</option>
                    <option>&gt;=</option>
                    <option>&lt;&lt;</option>
                    <option>&gt;&gt;</option>
                    <option>~</option>
                </select>
            </div>

            <label class="control-label">Units:</label>

            <input class="input-xlarge" type="text" id="valueUnits" name='valueUnits' value="">
        </div>

        <div id="review" class="step">
            <div class="itemWizardSelectionsTable"></div>

            <h1>Please review the information for this item above.</h1>

            <p>
                Click <strong>"Save"</strong> below to create the new item (cannot use "Previous" after this)
            </p>

            <p>
                Click <strong>"Cancel"</strong> to return to editing the assay definition
            </p>

            <p>
                Click <strong>"Previous"</strong> to return to the previous page in the wizard
            </p>
        </div>

        <div class="navigation">
            <div class="btn-group pull-right">
                <button type="button" class="btn backward">Back</button>
                <button type="button" class="btn forward">Next</button>
                <button type="submit" class="btn submit">Finish</button>
            </div>
        </div>
    </form>
</div>

<r:script>
    var source = $("#current-item-template").html();
    var currentItemTemplate = Handlebars.compile(source);

    var updateCurrentSelection = function () {
        $(".itemWizardSelectionsTable").html(currentItemTemplate({attribute: $("#attributeName").val(),
            valueType: $("[name=valueTypeOption]:checked").val(), // $("#valueType").val(),
            value: $("#valueName").val()
        }));
    }
    updateCurrentSelection()

    $("#wizard-steps").wizard({
        afterForward: function (event, state) {
            var stepId = state.step[0].id

            updateCurrentSelection()

            if (stepId == "attribute-type") {
//                $("#valueType").val($("[name=valueTypeOption]:checked").val())
            }
        },
        beforeForward: function (event, state) {
            var stepId = state.step[0].id

            if (stepId == "search-attribute") {
                // $("#attributeElementId").val()
            }
        }
    });

    var attribIdCache = {}
    $("#attributeTextField").select2({
        minimumInputLength: 2,
        width: "70%",
        placeholder: "Search for attribute name",
        query: function (query) {
            var sectionPath = $("#sectionPath").val();
            $.getJSON(
                    "/BARD/ontologyJSon/getDescriptors",
                    {
                        term: query.term,
                        section: sectionPath
                    },
                    function (data, textStatus, jqXHR) {
                        var selectData = {results: []}
                        $.each(data, function (index, val) {
                            selectData.results.push({id: val.elementId, text: val.label});
                            attribIdCache[val.elementId] = val.label;
                        })
                        query.callback(selectData)
                    }
            );
        }
    }).on("change", function (e) {
                $("#attributeElementId").val(e.val)
                $("#attributeName").val(attribIdCache[e.val])

                $(".forward").removeAttr("disabled")
            });

    $("#valueId").select2({
        minimumInputLength: 2,
        width: "70%",
        placeholder: "Search for attribute name",
        query: function (query) {
            var cardAssaySection = $("#sectionPath").val();
            var elementId = $("#attributeElementId").val();
            $.getJSON(
                    "/BARD/ontologyJSon/getValueDescriptors",
                    {
                        term: query.term,
                        section: cardAssaySection,
                        attributeId: elementId
                    },
                    function (data, textStatus, jqXHR) {
                        var selectData = {results: []}
                        $.each(data, function (index, val) {
                            selectData.results.push({id: val.elementId, text: val.label})
                        })
                        query.callback(selectData)
                    }
            );
        }
    })

</r:script>

</body>

</html>
