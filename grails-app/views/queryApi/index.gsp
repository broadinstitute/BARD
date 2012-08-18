<!doctype html>
<html>
<head>

    <title>REST Query API</title>
</head>
<body>
<div id="page-body" role="main">
    <h1>Test REST Query API</h1>

    <div>
        <table>
            <tr>
                <td>
                    <g:form action="searchProject" >
                        <fieldset class="form">
                            <label for="project">
                                <g:message code="project.id.label" default="Project"/>
                            </label>
                            <g:textField name="project"  value="${project}"/>
                        </fieldset>
                        <fieldset class="form">
                            <label for="projectType">
                                <g:message code="project.displayStatus.label" default="Type"/>
                            </label>
                            <g:select name="projectType" from="${bardqueryapi.ProjectDisplayType?.values()}"
                                      keys="${bardqueryapi.ProjectDisplayType.values()*.name()}" required=""
                                      value="${projectInstance?.displayStatus?.name()}"/>
                        </fieldset>
                        <fieldset class="form">
                            <g:submitButton name="searchProject"
                                            value="${message(code: 'default.button.search.label', default: 'Search')}"/>
                        </fieldset>
                    </g:form>
                </td>
                <td>
                    <g:form action="searchAssay">
                        <fieldset class="form">
                            <label for="assay">
                                <g:message code="assay.id.label" default="AID"/>
                            </label>
                            <g:textField name="assay" required="" value="${assay}"/>
                        </fieldset>
                        <fieldset class="form">
                            <label for="assayType">
                                <g:message code="assay.assayType.label" default="Type"/>
                            </label>
                            <g:select name="assayType" from="${bardqueryapi.AssayDisplayType?.values()}"
                                      keys="${bardqueryapi.AssayDisplayType.values()*.name()}"
                                      value="${params?.assayType?.name()}"/>
                        </fieldset>
                        <fieldset>
                            <g:submitButton name="searchAssay"
                                            value="${message(code: 'default.button.search.label', default: 'Search')}"/>
                        </fieldset>
                    </g:form></td>
                <td>
                    <g:form action="searchCompound" >
                        <fieldset class="form">
                            <label for="compound">
                                <g:message code="compound.id.label" default="Compound"/>
                            </label>
                            <g:textField name="compound" required="" value="${compound}"/>
                        </fieldset>
                        <fieldset class="form">
                            <label for="compoundType">
                                <g:message code="compound.compoundType.label" default="Type"/>
                            </label>
                            <g:select name="compoundType" from="${bardqueryapi.CompoundDisplayType?.values()}"
                                      keys="${bardqueryapi.CompoundDisplayType.values()*.name()}"
                                      value="${params?.compoundType?.name()}"/>
                            <label for="compoundFormat">
                                <g:message code="compound.compoundType.label" default="Format"/>
                            </label>
                            <g:select name="compoundFormat" from="${bardqueryapi.CompoundFormat?.values()}"
                                      keys="${bardqueryapi.CompoundFormat.values()*.name()}"
                                      value="${params?.compoundFormat?.name()}"/>
                        </fieldset>
                        <fieldset>
                            <g:submitButton name="searchCompound"
                                            value="${message(code: 'default.button.search.label', default: 'Search')}"/>
                        </fieldset>
                    </g:form>
                </td>
                <td>
                    <g:form action="searchTarget" >
                        <fieldset class="form">
                            <label for="target">
                                <g:message code="target.id.label" default="GeneID/AccessionID"/>
                            </label>
                            <g:textField name="target" required="" value="${target}"/>
                        </fieldset>
                        <fieldset class="form">
                            <label for="targetType">
                                <g:message code="target.targetType.label" default="Type"/>
                            </label>
                            <g:select name="targetType" from="${bardqueryapi.TargetDisplayType?.values()}"
                                      keys="${bardqueryapi.TargetDisplayType.values()*.name()}"
                                      value="${params?.targetType?.name()}"/>
                        </fieldset>
                        <fieldset>
                            <g:submitButton name="searchTarget"
                                            value="${message(code: 'default.button.search.label', default: 'Search')}"/>
                        </fieldset>
                    </g:form>
                </td>
            </tr>

        </table>
    </div>
</div>
</body>
</html>
