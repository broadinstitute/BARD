<%
/**
 * javascript to close current add item wizard window and open another wizard window
 *
 * @author	Yasel Cruz
 */
%>
<script type="text/javascript">

	var assayId = $("#cardAssayId").val();
	var assayContextId = $("#cardAssayContextId").val();
	var cardSection = $("#sectionPath").val();

	$("#dialog_add_item_wizard").dialog("close");
	launchAddItemWizard(assayId, assayContextId, cardSection);
		
</script>