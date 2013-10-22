<%@ page import="bard.db.dictionary.OntologyItem" %>
<html>
<head>
    <title>BARD: Dictionary Terms and Description</title>
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
                <th>Units</th>
                <th class="sorter-false">Reference</th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${capDictionary}" var="dictionaryElement">
                <g:if test="${dictionaryElement.label}">
                    <tr>
                        <td>
                            ${dictionaryElement.id}
                        </td>
                        <td>
                            <a name="${dictionaryElement.id}"></a>
                            ${dictionaryElement.label}
                        </td>
                        <td>
                            ${dictionaryElement.description}
                        </td>
                        <td>
                            ${dictionaryElement?.unit?.abbreviation}
                        </td>
                        <td>
                            <%
                                List<OntologyItem> ontologyItems = dictionaryElement.ontologyItems as List<OntologyItem>
                            %>
                            <g:each in="${ontologyItems}" var="ontologyItem">
                                ${ontologyItem.displayValue()}
                            </g:each>
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

