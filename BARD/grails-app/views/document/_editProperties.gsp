<%@ page import="bard.db.model.IDocumentType" %>

  <div class="control-group">
    <label class="control-label" for="inputType">Type</label>
    <div class="controls">
        <g:select name="type" from="${IDocumentType.DOCUMENT_TYPE_DISPLAY_ORDER}" />

    </div>
  </div>

Name: <input type="text">
Content: <textarea></textarea>
