<div id="dialog_edit_summary" title="Edit Summary">
    <r:require modules="summary"/>
    <form id="editSummaryForm">
    <dl class="dl-horizontal">
        <dt><g:message code="default.id.label" default="Fix i18n"/>:</dt>
        <dd id="assayId">${assay?.id}</dd>

        <dt><g:message code="assay.assayName.label" default="Fix i18n"/>:</dt>
        <dd><g:textField id="assayName" name="assayName" class="assayName" required="required" value="${assay?.assayName}"/><span id="assayNameValidation" style="color:red" ></span></dd>

        <dt><g:message code="assay.assayVersion.label" default="Fix i18n"/>:</dt>
        <dd><g:fieldValue bean="${assay}" field="assayVersion"/></dd>


        <dt><g:message code="assay.assayStatus.label" default="Fix i18n"/>:</dt>
        <dd><g:select id="assayStatus" name="assayStatus"
                      from="${bard.db.registration.AssayStatus.values()}"
                      value="${assay?.assayStatus}" /></dd>

        <dt><g:message code="assay.designedBy.label" default="Fix i18n"/>:</dt>
        <dd><g:textField id="designedBy" name="designedBy" class="designedBy" required="required" value="${assay?.designedBy}"/></dd>

        <dt><g:message code="default.dateCreated.label"/>:</dt>
        <dd><g:formatDate date="${assay.dateCreated}" format="yyyy-mm-dd"/></dd>
    </dl>
        </form>
</div>