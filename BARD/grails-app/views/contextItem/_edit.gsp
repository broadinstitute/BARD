<%--
  Created by IntelliJ IDEA.
  User: xiaorong
  Date: 12/5/12
  Time: 5:34 PM
  To change this template use File | Settings | File Templates.
--%>


<%-- A template for both project and assay def, edit card --%>
<div id="card-${context.id}" class="card roundedBorder card-table-container">
    <table class="table table-hover">
        <caption id="${context.id}" class="assay_context">
            <div class="cardTitle">${context.preferredName}</div>
            <div class="cardMenu">
                <div class="btn-group dropup">
                    %{-- <a class="btn dropdown-toggle" data-toggle="dropdown" href="#"><span class="caret"></span></a> --}%
                    <a class="btn btn-info dropdown-toggle" data-toggle="dropdown" href="#"><span class="icon-cog"></span></a>
                    <ul class="dropdown-menu" style="z-index:3999;">
                        <li><a href="#" onclick="editCardName(${context.id}, '${context.preferredName}');return false;"><i class="icon-pencil"></i> Edit card name</a></li>
                        <li><a href="#" onclick="launchAddItemWizard(${context.assay.id}, ${context.id}, '${cardSection.replace(' > ', '> ')}');return false;"><i class="icon-road"></i> Add item wizard</a></li>

                        <g:if test="${context.contextItems.size() == 0}">
                            <li><a href="#" onclick="deleteCard(${context.id});return false;"><i class="icon-pencil"></i> Delete card</a></li>
                        </g:if>
                    </ul>
                </div>
            </div>
        </caption>
        <tbody>
            <g:each in="${context.contextItems}" status="i" var="contextItem">
                <tr id="${contextItem.id}" class='context_item_row'>
                    <td class="attributeLabel">${contextItem.attributeElement?.label}</td>
                    <td class="valuedLabel">${contextItem.valueDisplay}</td>
                    <td class="deleteItemButton">
                        <div class="btn-group dropup" >
                            <a class="btn dropdown-toggle" data-toggle="dropdown" href="#"><span class="caret"></span></a>
                            <ul class="dropdown-menu" style="z-index: 10000;">
                                %{--<li><a href="#"><i class="icon-pencil"></i> Edit</a></li>--}%
                                <li><a href="#"  onclick="moveCardItem(${contextOwner.id}, ${contextItem.id});return false;"><i class="icon-move"></i> Move</a></li>
                                <li><a href="#" onclick="deleteCardItem(${contextItem.id}, ${context.id});return false;"><i class="icon-trash"></i> Delete</a></li>
                            </ul>
                        </div>
                    </td>
                </tr>
            </g:each>
        </tbody>
    </table>
</div>


