<g:if test="${status == 'Draft'}">
    <img src="${resource(dir: 'images', file: 'draft_retired.png')}"
         alt="Draft" title="Warning this ${entity} has not yet been reviewed for accuracy"/>
</g:if>
<g:elseif
        test="${status == 'Provisional'}">
    <img src="${resource(dir: 'images', file: 'provisional_16.png')}"
         alt="Provisional" title="This ${entity} has been reviewed for accuracy by curators"/>
</g:elseif>
<g:elseif
        test="${status == 'Approved'}">
    <img src="${resource(dir: 'images', file: 'witnessed.png')}"
         alt="Approved" title="This ${entity} has been reviewed for accuracy"/>
</g:elseif>