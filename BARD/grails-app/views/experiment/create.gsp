<%@ page import="bard.db.project.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require modules="core,bootstrap"/>
    <meta name="layout" content="basic"/>
    <r:external file="css/bootstrap-plus.css"/>
    <title>Create Experiment</title>
</head>

<body>
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


<g:form action="save">
    <input type="submit" class="btn btn-primary" value="Create"/>
    <input type="hidden" name="assayId" value="${assay.id}"/>
    <div class="row-fluid">
        <div id="accordion-foo" class="span12 accordion">

            <div class="accordion-group">
                <div class="accordion-heading">
                    <a href="#contexts-header" class="accordion-toggle" data-toggle="collapse"
                       data-target="#target-contexts-info">
                        <i class="icon-chevron-down"></i>
                        Summary
                    </a>
                </div>

                <div class="accordion-body in collapse">
                    <div class="accordion-inner">
                        <table>
                            <tbody>
                            <tr><td>Assay</td><td><g:link controller="assayDefinition" action="show" id="${assay.id}">${assay.name}</g:link></td></tr>
                            <tr><td>Description</td><td><input type="text" name="description"/></td></tr>
                            <tr><td>Experiment Name</td><td><input type="text" name="experimentName"/></td></tr>

                            </tbody>
                        </table>

                    </div>
                </div>
            </div>

        </div>    <!-- End accordion -->
    </div>
</g:form>

</body>
</html>