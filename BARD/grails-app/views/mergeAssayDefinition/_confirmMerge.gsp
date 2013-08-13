<%@ page import="bard.db.enums.AssayStatus" %>
<div class="container-fluid">
    <div class="row-fluid">
        <div class="span2">

        </div>

        <div class="span8">

            <g:formRemote url="[controller: 'mergeAssayDefinition', action: 'mergeAssays']"
                          name="mergeAssays"
                          update="[success: 'confirmResponse', failure: 'confirmResponse']">

                <div class="control-group">
                    <label><h3>Confirmation: You want to merge the following ${mergeAssayCommand.sourceAssays.size()} assay(s)</h3>
                    </label>
                </div>

                <div class="control-group">
                    <ol>

                        <g:each var="sourceAssay" in="${mergeAssayCommand.sourceAssays}" status="counter">
                            <g:hiddenField name="sourceAssayIds[${counter}]" value="${sourceAssay.id}"/>
                            <li>
                                <g:link controller="assayDefinition" action="show" id="${sourceAssay.id}"
                                        target="_blank">
                                    ${sourceAssay.id} - ${sourceAssay.assayName}
                                    <g:if test="${sourceAssay.assayStatus == AssayStatus.RETIRED}">
                                        <span class="alert-error">
                                            - ${sourceAssay.assayStatus}
                                        </span>
                                    </g:if>
                                </g:link>
                            </li>
                        </g:each>
                    </ol>
                </div>

                <div class="control-group">
                    <label><h3>Into</h3></label>
                </div>

                <div class="control-group">
                    <label>
                        <g:hiddenField name="targetAssayId" value="${mergeAssayCommand.targetAssay?.id}"/>
                        <g:link controller="assayDefinition" action="show" id="${mergeAssayCommand.targetAssay.id}"
                                target="_blank">
                            ${mergeAssayCommand.targetAssay.id} - ${mergeAssayCommand.targetAssay.assayName}
                            <g:if test="${mergeAssayCommand.targetAssay.assayStatus == AssayStatus.RETIRED}">
                                <span class="alert-error">
                                    - ${mergeAssayCommand.targetAssay.assayStatus}
                                </span>
                            </g:if>
                        </g:link>
                    </label>
                </div>
                <g:if test="${mergeAssayCommand.errorMessages}">
                    <div class="control-group">
                        <div class="alert-error">
                            <ul>
                                <g:each in="${mergeAssayCommand.errorMessages}" var="errorMessage">
                                    <li>${errorMessage}</li>
                                </g:each>
                            </ul>
                        </div>
                    </div>
                </g:if>
                <div class="control-group">
                    <div class="controls">
                        <input type="submit" class="btn btn-primary" name="Merge" value="Merge">
                    </div>
                </div>

                <div id="confirmResponse"></div>
                <br/>

            </g:formRemote>

        </div>

        <div class="span2">

        </div>
    </div>
</div>
