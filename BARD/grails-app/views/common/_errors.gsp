<g:if test="${errors}">
    <div class="row-fluid">
        <div class="span12">
            <div class="ui-widget">
                <div class="ui-state-error ui-corner-all" style="margin-top: 20px; padding: 0 .7em;">
                <p>
                    <span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"/>
                    <g:each var="error" in="${errors}">
                        <p><g:message error="${error}"/></p>

                    </g:each>
                </p>
                </div>
            </div>
        </div>
    </div>
</g:if>

