<br/>
<br/>
<hr/>

<div class="alert alert-error">
    <h3>Found the following Errors:</h3>
    <g:if test="${validationError}">
        <ol>
            <g:each in="${validationError}" var="currentError">
                <li>
                    <ul>
                        <li>${currentError}<br/>
                        <g:message error="${currentError}"/>
                        </li>
                    </ul>

                </li>
            </g:each>
        </ol>
    </g:if>
    <g:if test="${errorMessage}">
        <ul>
            <li>${errorMessage}</li>
        </ul>
    </g:if>
</div>