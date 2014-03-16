<%--
  Created by IntelliJ IDEA.
  User: dlahr
  Date: 4/22/13
  Time: 11:01 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <r:require modules="core,bootstrap,elementEditHierarchy,elementSelect"/>
    <meta name="layout" content="basic"/>
    <title>Edit Element Hierarchy</title>
</head>

<body>
<sec:ifAnyGranted roles="ROLE_CURATOR">
    <h1>Edit Element Hierarchy</h1>

    <g:if test="${flash.message}">
        <div class="row-fluid">
            <div class="span12">
                <div class="ui-widget">
                    <div class="ui-state-error ui-corner-all" style="margin-top: 20px; padding: 0 .7em;">
                        <p><span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>
                            <strong>${flash.message}</strong>
                        </p>
                    </div>
                </div>
            </div>
        </div>
    </g:if>

    <div class="row-fluid">
        <div class="span10 offset1">
            <h3>Hierarchy Path(s)</h3>
            <g:form controller="element" action="deleteElementPath" class="form-inline" role="form">
                <g:each in="${list}" var="elementAndPath">
                    <div class="form-group">
                        <label for="${elementAndPath} " style="cursor: auto;">${elementAndPath}</label>
                        <g:if test="${elementAndPath.path}">
                            <button id="${elementAndPath}" type="submit" class="btn-small" value="0"
                                    button-role="deleteElementPath" style="margin-left: 10px;">
                                <i class="icon-remove"></i>
                            </button>
                        </g:if>
                        <g:else>
                            <label class="bg-primary"
                                   style="cursor: auto; color: #0000ff">(element is a root)</label>
                        </g:else>
                    </div>
                </g:each>

                <g:hiddenField name="elementId" value="${element.id}"/>
            %{--Updated via JS--}%
                <g:hiddenField name="elementPathToDelete" id="elementPathToDelete" value=""/>
            </g:form>
        </div>
    </div>

    <div class="row-fluid">
        <div class="span11 offset1">
            <h3>Add a New Hierarchy Path</h3>

            <p style="color: #0000ff">Please select a new parent-element for '${element.label}' to be added to the element hierarchy:</p>
        </div>
    </div>

    <div class="row-fluid" style="margin: 5px;">
        <g:form controller="element" action="addElementPath" name="addElementPathForm">

            %{--Select2 widget--}%
            <div id="elementList" name="elementList" class="span10 offset1"></div>

            <g:hiddenField name="elementId" value="${element.id}"/>
            <g:hiddenField name="select2FullPath" id="select2FullPath" value=""/>
            <g:hiddenField name="select2ElementId" id="select2ElementId" value=""/>
        </g:form>
    </div>

</sec:ifAnyGranted>
</body>
</html>