<div class="container-fluid">
    <div class="row-fluid">
        <div class="span9">
            <div class="row-fluid">
                <div>
                    <g:if test="${panelInstances}">
                        <h3>Associated Panels</h3>
                        <table class="table table-striped table-hover table-bordered">
                           <thead>
                           <tr>
                               <th>Panel ID</th><th>Panel Name</th></thead>
                           </tr>
                           </thead>
                        <tbody>
                        <g:each in="${panelInstances.panel}" var="panel">
                            <tr>
                                <td><g:link controller="panel" action="show"
                                            id="${panel.id}">${panel.id}</g:link></td><td>${panel.name}</td>
                            </tr>
                        </g:each>
                        </tbody>
                    </table>
                    </g:if>
                </div>

            </div>
        </div>
    </div>
</div>
