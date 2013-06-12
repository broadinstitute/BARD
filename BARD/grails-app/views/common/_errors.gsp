<g:if test="${errors}">
    <div class="alert alert-error">
        <g:each var="error" in="${errors}">
            <p><g:message error="${error}"/></p>
        </g:each>
    </div>
</g:if>

