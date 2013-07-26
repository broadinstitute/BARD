<ul>
    <g:each in="${contexts}" var="context">
        <g:each in="${context.contextItems}" var="contextItem">
            <g:if test="${contextItem.key.equals('GO biological process term')}">
                <li>
                    <a href="https://neuinfo.org/mynif/search.php?q=GO_${contextItem.url.substring(contextItem.url.indexOf('GO:')+3)}&t=data&qs=1" target="_blank">NIF search for GO term "${contextItem.display}"</a>
                </li>
            </g:if>
        </g:each>
    </g:each>
</ul>