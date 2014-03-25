%{-- Extract this to a taglib to simplify the logic --}%
<dl>
    <dt>External references</dt>
    <dd>
        <ul>
            <g:each in="${project?.externalReferences}" var="xRef">
                <li>
                    <a href="${xRef.externalSystem.systemUrl}${xRef.extAssayRef}"
                       target="_blank">${xRef.externalSystem.systemName} ${xRef.extAssayRef}</a>
                </li>
            </g:each>
        </ul>
    </dd>
</dl>