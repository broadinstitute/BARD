<!DOCTYPE html>
<html>
<head>
    <r:require modules="core,bootstrap,select2,card,bootstrapplus"/>
    <meta name="layout" content="basic"/>
    <title>Edit ${assayInstance?.getDomainClass()?.getNaturalName()} Context</title>
</head>

<body>
<div class="row-fluid">
    <div class="span12">
        <div class="well well-small">
            <div class="pull-left">
                <h4>Edit ${assayInstance?.getDomainClass()?.getNaturalName()} (${assayInstance?.id}) Contexts </h4>
            </div>
            <g:if test="${assayInstance?.id}">
                <div class="pull-right">
                    <g:link action="show" id="${assayInstance?.id}"
                            class="btn btn-small btn-primary">Finish Editing</g:link>
                </div>
            </g:if>
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

<g:if test="${assayInstance?.id}">
    <div class="row-fluid">
        <div class="row-fluid">
            <div id="accordion-foo" class="span12 accordion">

                <div class="accordion-group">
                    <div class="accordion-heading">
                        <a href="#contexts-header" id="contexts-header" class="accordion-toggle" data-toggle="collapse"
                           data-target="#target-contexts-info">
                            <i class="icon-chevron-down"></i>
                            Contexts
                        </a>
                    </div>

                    <div id="target-contexts-info" class="accordion-body in collapse">
                        <div class="accordion-inner">
                            <g:render template="../context/edit"
                                      model="[contextOwner: assayInstance, contexts: assayInstance?.groupContexts()]"/>
                        </div>
                    </div>
                </div>

            </div>    <!-- End accordion -->
        </div>

    </div>
</g:if>

</body>
</html>
