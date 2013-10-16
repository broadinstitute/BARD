<div class="span6 offset1">
    <g:form class="form-horizontal" action="saveTerm" id="saveTerm" name="saveTerm">
        <g:hiddenField name="currentElement.id" id="currentElement.id"
                       value="${termCommand?.parentElementId ?: ''}"/>
        <g:hiddenField name="parentElementId" id="parentElementId"
                       value="${termCommand?.parentElementId ?: ''}"/>
        <g:hiddenField name="parentLabel" id="parentLabel"
                       value="${termCommand?.parentLabel ?: ''}"/>
        <g:hiddenField name="parentDescription" id="parentDescription"
                       value="${termCommand?.parentDescription ?: ''}"/>
        <g:render template="addTermForm2nd"/>
        <div class="control-group">
            <label>
                <h4>5. Choose to save your proposed term.</h4>
            </label>
        </div>

        <div class="control-group">
            <div class="controls">
                <input type="submit" class="btn btn-primary" value="Save" id="saveBtn">
                <a href="javascript:closeWindow();">Close this window to cancel</a>
              </div>
        </div>
    </g:form>
</div>
