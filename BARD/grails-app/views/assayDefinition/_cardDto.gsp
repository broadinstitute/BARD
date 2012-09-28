<div id="card-${card.id}" class="span4">
<div class="bs-docs">
<table class="table table-hover">
    <caption id="${card.id}" class="assay_context">
        <div>
            <p>${card.label}</p>
        </div>
        %{-- <div><button id="b-${card.id}" class="btn btn-mini">Edit</button></div> --}%
        <g:if test="${card.lines.size() == 0}">
        	<div class="deleteCardButton"><button id="${card.id}" class="btn btn-mini"><span style="color: red; font-weight:bold;">x</span></button></div>
        </g:if>
    </caption>
    <tbody>
    <g:each in="${card.lines}" status="i" var="line">
        <tr id="${line.id}" class='context_item_row'>
            <td class="attributeLabel">${line.attributeLabel}</td>
            <td class="valuedLabel">${line.valueLabel}</td>
            <td><div class="deleteItemButton"><button id="${line.id}" class="btn btn-mini"><span style="color: red; font-weight:bold;">x</span></button></div></td>
        </tr>
    </g:each>
    </tbody>
</table>
</div>
</div>
