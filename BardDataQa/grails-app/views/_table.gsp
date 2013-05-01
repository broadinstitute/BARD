<table border="1" cellpadding="10" cellspacing="1">
    <tr>
        <g:each in="${headerList}" var="header">
            <th>${header}</th>
        </g:each>
    </tr>


    <g:each in="${rowList}" var="row">
        <tr>
            <g:each in="${row}" var="entry">
                <td>${entry}</td>
            </g:each>
        </tr>
    </g:each>
</table>