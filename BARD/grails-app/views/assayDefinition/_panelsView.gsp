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
                               <th>PLID</th><th>Panel Name</th><th></th></thead>
                           </tr>
                           </thead>
                        <tbody>
                        <g:each in="${panelInstances.panel}" var="panel">
                            <tr>
                                <td><g:link controller="panel" action="show"
                                            id="${panel.id}">${panel.id}</g:link></td><td>${panel.name}</td>
                                <td>
                                    <g:if test="${editable == 'canedit'}">
                                        <g:link controller="panel" action="removeAssay"
                                                params="${[id: panel.id, assayIds: assay.id]}"
                                                class="btn btn-mini" title="Remove From Assay"
                                                onclick="return confirm('Are you sure you wish to remove this Panel from this Assay?');"><i
                                                class="icon-trash"></i>Remove From Panel</g:link>
                                    </g:if>
                            </tr>
                        </g:each>
                        </tbody>
                    </table>
                    </g:if>
                    <g:if test="${editable == 'canedit'}">
                        <g:link controller="panel" action="addAssayToPanel" params="${[assayIds: assay.id]}"
                                class="btn"><i class="icon-plus"></i>Add To Existing Panel</g:link>
                    </g:if>
                    <g:link controller="panel" action="create"
                            class="btn"><i class="icon-plus"></i>Create a New Panel</g:link>
                </div>

            </div>
        </div>
    </div>
</div>
