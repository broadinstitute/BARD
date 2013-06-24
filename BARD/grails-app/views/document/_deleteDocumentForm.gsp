<br/>
<br/>
<g:form action="delete" controller="document"
        params="${[type: document.owner.class.simpleName]}" id="${document.id}"
        onsubmit="return confirm('Are you sure you wish to delete the document?');">
    %{--<button type="button" class="documentPencil btn" data-id="${document.id}">--}%
        %{--<i class="icon-pencil"></i> Edit ${document.documentType.id}--}%
    %{--</button>--}%
    <button type="submit" class="btn">
        <i class="icon-trash"></i> Delete ${document.documentType.id}
    </button>
</g:form>
