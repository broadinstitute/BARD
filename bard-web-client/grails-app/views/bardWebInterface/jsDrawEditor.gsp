<%@ page import="bard.core.rest.spring.util.StructureSearchParams" contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>BioAssay Research Database</title>
    <script type="text/javascript" src="${request.contextPath}/js/dojo-min/dojo/dojo.js"></script>
    <script type="text/javascript" src="${request.contextPath}/js/jsDraw/Scilligence.JSDraw2.js"></script>
    <script type="text/javascript" src="${request.contextPath}/js/jsDraw/license.js"></script>
    <r:require modules="jquery, jquery-ui, jquery-theme, core, bootstrap"/>
    <r:layoutResources/>
    <r:script>
        var jsDrawEditor = null;

        dojo.addOnLoad(function () {
            jsDrawEditor = new JSDraw2.Editor("jsDrawEditorDiv", {popup:false});
            adjustJSDrawEditorWindow()
            //Read cached structure
            jsDrawEditor.readCookie();
        });

        //On clicking the Submit button, update the hidden field with search-type + smiles and cache the structure in local storage.
        $(document).on('click', '#searchButton', function () {
            var smiles = jsDrawEditor.getSmiles();
            var structureSearchTypeSelected = $('input:radio[name=structureSearchType]:checked').attr('value');
            var constructedSearch = structureSearchTypeSelected + ":" + smiles;
            $('#searchString').attr('value', constructedSearch);
            jsDrawEditor.writeCookie();
        });

        $(document).ready(function () {
            $(window).resize(function () {
                adjustJSDrawEditorWindow();
            });
        });

        function adjustJSDrawEditorWindow() {
            var width = $(window).width() - 30;
            var height = $(window).height() - 150;
            if (width && height && jsDrawEditor) {
                jsDrawEditor.setSize(width, height)
            }
        }
    </r:script>
</head>

<body>
<div class="row-fluid">
    <div class="span12">
        <div id="jsDrawEditorDiv" skin='w8' style="border:1px solid gray; margin-left: auto; margin-right: auto;"></div>
        <g:form controller="bardWebInterface" action="searchResults">
        %{--Use this field to hold the smiles + search-type value--}%
            <g:hiddenField name="searchString" id="searchString" value=""/>

            <div style="text-align: left">
                <g:radioGroup name="structureSearchType"
                              values="${StructureSearchParams.Type.values()}"
                              value="${StructureSearchParams.Type.Substructure}"
                              labels="${StructureSearchParams.Type.values()}">
                    <label class="radio inline">
                        ${it.radio} ${it.label}
                    </label>
                </g:radioGroup>
            </div>
            <br/>

            <div style="text-align: left;">
                <g:submitButton name="search" value="Search" class="btn btn-primary span1" id="searchButton"/>
            </div>
        </g:form>
    </div>
</div>
<r:layoutResources/>
</body>
</html>