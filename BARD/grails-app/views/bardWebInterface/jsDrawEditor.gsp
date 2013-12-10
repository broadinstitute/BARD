<%@ page defaultCodec="none" %>
<%@ page import="bard.core.rest.spring.util.StructureSearchParams" contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>BARD: Structure Search</title>
    <script type="text/javascript" src="${request.contextPath}/js/dojo-min/dojoConfig.js"></script>
    <script type="text/javascript" src="${request.contextPath}/js/dojo-min/dojo/dojo.js"></script>
    <script type="text/javascript" src="${request.contextPath}/js/jsDraw/JSDraw3.1.2/Scilligence.JSDraw2.Pro.js"></script>
    <script type="text/javascript" src="${request.contextPath}/js/jsDraw/license.js"></script>
    <r:require modules="jquery, jquery-ui, jquery-theme, core, bootstrap, jsDrawEditor"/>
    <r:layoutResources/>
</head>

<body>
<div class="row-fluid">
    %{--AJAX is handling this--}%
    <form class="form-inline" style="margin-top: 3px; margin-left: 3px">
        <input type="text" id="convertSmilesTextfield" name="smiles" class="span8" rows="1"
               placeholder="${message(code: 'jsDraw.convertSmiles')}"/>

        <button type="submit" id="smilesToMolBtn" class="btn">Import</button>

        <label id="smilesImportErrorLabel" style="margin-left: 5px;"/>
    </form>
</div>

<div class="row-fluid">
    <div class="span12">
        <div id="jsDrawEditorDiv" skin='w8' style="border:1px solid gray; margin-left: auto; margin-right: auto;"></div>
        <g:form controller="bardWebInterface" action="searchResults">
        %{--Use this field to hold the smiles + search-type value--}%
            <g:hiddenField name="searchString" id="searchString" value=""/>
            <g:hiddenField name="similaritySearchTypeValue" id="similaritySearchTypeValue"
                           value="${StructureSearchParams.Type.Similarity}"/>

            <div style="text-align: left;">
                <g:radioGroup name="structureSearchType"
                              values="${StructureSearchParams.Type.values()}"
                              value="${StructureSearchParams.Type.Substructure}"
                              labels="${StructureSearchParams.Type.values()}">
                    <label class="radio inline">
                        ${it.radio} ${it.label}
                    </label>
                </g:radioGroup>

                <span><g:textField name="cutoff" value="90" id="cutoff" disabled="disabled"/>[%]</span>
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