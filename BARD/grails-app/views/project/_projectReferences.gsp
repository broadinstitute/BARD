%{-- Extract this to a taglib to simplify the logic --}%
<dl>
    <dt>External references</dt>
    <dd>
        <ul>
            <g:each in="${project?.externalReferences}" var="xRef">
                <li>
                    <a href="${xRef.externalSystem.systemUrl}${xRef.extAssayRef}"
                       target="_blank">${xRef.externalSystem.systemName} ${xRef.extAssayRef}</a>
                    <g:if test="${editable == 'canedit'}">
                        <g:set var="externalReferenceParams"
                               value="${[ownerClass: project.class.simpleName, ownerId: project.id, xRefId: xRef.id]}"></g:set>
                        <g:form class="no-padding" controller="externalReference" action="delete"
                                params="${externalReferenceParams}" method="POST">
                            <button type="submit" title="Delete" class="btn btn-mini"
                                    onclick="return confirm('Are you sure you wish to delete this external reference?');"><i
                                    class="icon-trash"></i></button>
                        </g:form>
                        <g:link controller="externalReference" action="create" params="${externalReferenceParams}"
                                class="btn btn-mini"><i class="icon-pencil" title="Edit"></i></g:link>
                    </g:if>
                </li>
            </g:each>
        </ul>
    </dd>
</dl>