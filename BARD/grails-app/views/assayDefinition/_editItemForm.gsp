<div class="well well-small">
	Add new numeric value and unit:
</div>
<form id="edit_numeric_item_form" class="form-horizontal" >
	<input type="hidden" id="attributeElementId" name="attributeElementId" value="${assayContextItem.attributeElement?.id}"/>
	<input type="hidden" id="attributeElementUnitId" name="attributeElementUnitId" value="${assayContextItem.attributeElement?.unit?.id}"/>
	<input type="hidden" id="valueUnitLabel" name="valueUnitLabel" value="${assayContextId}"/>
	<div class="control-group">
		<label class="control-label">Numeric Value:</label>
		<div class="controls">
			<input type="text" id="numericValue" name='numericValue' value="${assayContextItem?.valueNum}">
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">Unit:</label>
		<div class="controls">
			<%-- This hidden control becomes the units selection box --%>
            <input type="hidden" id="valueUnitId" name="valueUnitId">
		</div>
	</div>
	<input type="hidden" id="assayContextItemId" name="assayContextItemId" value="${assayContextItem.id}"/>
   	<input type="hidden" id="assayContextId" name="assayContextId" value="${assayContextId}"/>
</form>