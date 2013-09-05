<html>
<head>
    <title>Title: Dictionary Terms and Description</title>
    <r:require modules="bootstrap,dictionaryPage"/>
    <r:layoutResources/>
    <script type="text/javascript">
        $(document).ready(function () {
            $("#dictionary").tablesorter();
        });
    </script>
</head>

<body>


<div class="container-fluid">
    <div class="row-fluid">

        <table id="dictionary" class="tablesorter table table-condensed table-striped table-bordered">
            <caption>Dictionary Terms and Description</caption>
            <thead>
            <tr>
                <th>ID</th>
                <th>Term</th>
                <th class="sorter-false">Description</th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${capDictionary?.nodes}" var="dictionaryElement">
                <g:if test="${dictionaryElement.description}">
                    <tr>
                        <td>
                            ${dictionaryElement.elementId}
                        </td>
                        <td>
                            <a name="${dictionaryElement.elementId}"></a>
                            ${dictionaryElement.label}
                        </td>
                        <td>
                            ${dictionaryElement.description}
                        </td>

                    </tr>
                </g:if>
            </g:each>
            </tbody>
        </table>

    </div>
</div>
<r:layoutResources/>
</body>
</html>

