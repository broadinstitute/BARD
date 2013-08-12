<g:if test="${guidanceList}">
    <div class="alert alert-error">
        <ul>
            <g:each in="${guidanceList}" var="guidance">
                <li>${guidance.message}</li>
            </g:each>
        </ul>
    </div>
</g:if>