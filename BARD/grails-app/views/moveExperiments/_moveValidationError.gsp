<br/>
<br/>
<hr/>

<div class="alert alert-error">
    <h3>Found the following Errors:</h3>
    <g:if test="${validationError}">
        <ol>
            <g:each in="${validationError?.errors?.allErrors}" var="currentError">
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
</div>