<%@ page import="bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require modules="core,bootstrap,bootstrapplus"/>
    <meta name="layout" content="basic"/>
    <title>Search for Panel by Name</title>
    <r:script>
        $(document).ready(function () {
            var autoOpts = {
                source: "/BARD/panelJSon/getNames",
                minLength: 3
            }
            $("#name").autocomplete(autoOpts);
            $("#results_accordion").accordion({ autoHeight: false });
        })
    </r:script>

</head>

<body>
<div class="row-fluid">
    <div class="span12">
        <div class="hero-unit-v1">
            <h4>Search for Panel by Name</h4>
        </div>
    </div>
</div>

<g:if test="${flash.message}">
    <div class="row-fluid">
        <div class="span12">
            <div class="ui-widget">
                <div class="ui-state-error ui-corner-all" style="margin-top: 20px; padding: 0 .7em;">
                    <p><span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>
                        <strong>${flash.message}</strong>
                </div>
            </div>
        </div>
    </div>
</g:if>

<div class="row-fluid">
    <div class="span12">
        <div class="bs-docs" style="padding: 20px 20px 20px;">
            <g:form action="findByName" class="form-inline">
                <input type="text" size="50" id="name" name='name' value="${params.name}" autofocus="true"
                       placeholder="Enter Panel Name" class="input-xxlarge search-query">
                <g:submitButton name="search" value="Search" class="btn btn-primary"/>
            </g:form>
        </div>
    </div>
</div>

<g:if test="${panels}">
    <div class="row-fluid">
        <div class="span12" id="results_accordion">
            <h3>Panels (${panels.size()})</h3>

            <div>
                <g:if test="${panels.size() > 0}">
                    <div>
                        <table class="gridtable">
                            <thead>
                            <tr>
                                <g:sortableColumn property="id"
                                                  title="${message(code: 'panel.id.label', default: 'ID')}"
                                                  params="${params}"/>
                                <g:sortableColumn property="name"
                                                  title="${message(code: 'panel.name.label', default: 'Panel Name')}"
                                                  params="${params}"/>
                            </tr>
                            </thead>
                            <tbody>
                            <g:each in="${panels}" status="i" var="panelInstance">
                                <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                                    <td><g:link action="show" id="${panelInstance.id}">${panelInstance.id}</g:link></td>
                                    <td>${fieldValue(bean: panelInstance, field: "name")}</td>
                                </tr>
                            </g:each>
                            </tbody>
                        </table>
                    </div>
                </g:if>
            </div>
        </div>
    </div>
</g:if>
</body>
</html>