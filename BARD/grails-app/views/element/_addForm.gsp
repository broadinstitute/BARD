<div class="span6 offset1">
    <g:form class="form-horizontal" action="saveTerm" id="saveTerm" name="saveTerm">
        <g:hiddenField name="currentElementId" id="currentElementId"
                       value="${currentElement ? currentElement.id : ''}"/>
        <g:render template="addTermForm1st"/>
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
