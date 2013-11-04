<%@ page import="bard.db.dictionary.OntologyItem" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="basic"/>
    <title>Glossary</title>
    <r:require  modules="dictionaryPage"/>
</head>

<body>

<div class="container-fluid">
    <div class="row-fluid"><div class="span12">
        <h3>BARD Glossary</h3>
        <p id="msg">&nbsp;</p>
        <table class="table table-striped table-bordered">
            <thead>
            <tr>
                <th data-sort="int">ID</th>
                <th data-sort="string-ins">Term</th>
                <th data-sort="string-ins">Description</th>
                <th data-sort="string-ins">Units</th>
                <th>External Ontology References</th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${capDictionary}" var="descriptor">
                <g:if test="${descriptor.label}">
                    <tr>
                        <td>
                            ${descriptor.id}
                        </td>
                        <td>
                            <a name="${descriptor.id}"></a>
                            ${descriptor.label}
                        </td>
                        <td>
                            <small class="text-info">${descriptor.fullPath}</small><br/>
                            ${descriptor.description}
                        </td>
                        <td>
                            ${descriptor?.unit?.abbreviation}
                        </td>
                        <td>
                            <%
                                List<OntologyItem> ontologyItems = descriptor.element.ontologyItems as List<OntologyItem>
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
</div>
</body>
</html>

