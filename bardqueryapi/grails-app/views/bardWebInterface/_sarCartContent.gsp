<%@ page import="com.metasieve.shoppingcart.Shoppable; com.metasieve.shoppingcart.ShoppingCart; bardqueryapi.CartAssay" %>
<%@ page import="com.metasieve.shoppingcart.ShoppingCartService" %>
<%
    ShoppingCartService  shoppingCartService = grailsApplication.classLoader.loadClass('com.metasieve.shoppingcart.ShoppingCartService').newInstance()
%>
    <g:each in="${shoppingCartService.getItems()}" var="elem" status="item">
    <%
        def shoppingItem = Shoppable.findByShoppingItem(elem)
        CartAssay cartAssay1 = shoppingItem as  CartAssay
        Shoppable shoppable = shoppingItem as  Shoppable
    %>
        <tr>
        <td>
            ${cartAssay1.toString()}
        </td>
        <td>
            ${shoppingCartService.getQuantity(elem)}
        </td>
        %{--<td>--}%
            %{--<a href="/bardqueryapi/sarCart/add/${shoppable.id}?class=class+bardqueryapi.CartAssay&version=0"--}%
               %{--onclick="jQuery.ajax({  type:'POST',--}%
                   %{--data:{'id': '2','class': 'class bardqueryapi.CartAssay','version': '0'},--}%
                   %{--url:'/bardqueryapi/sarCart/add',--}%
                   %{--success:function(data,textStatus){--}%
                       %{--jQuery('#sarCartContent').html(data);--}%
                       %{--alert('success');--}%
                   %{--},--}%
                   %{--error:function(XMLHttpRequest,textStatus,errorThrown){--}%
                       %{--alert('error')--}%
                   %{--}--}%
                %{--});--}%
               %{--return false;"--}%
               %{--action="add"--}%
               %{--controller="sarCart">add</a>--}%
        %{--</td>--}%
        <td>
            <a href="/bardqueryapi/sarCart/remove/${shoppable.id}"
               onclick="jQuery.ajax({  type:'POST',
                   data:{'id': '${shoppable.id}','class': 'class bardqueryapi.CartAssay','version': '0'},
                   url:'/bardqueryapi/sarCart/remove',
                   success:function(data,textStatus){
                       jQuery('#sarCartContent').html(data);
                   },
                   error:function(XMLHttpRequest,textStatus,errorThrown){
                       alert('problem removing assay')
                   }
               });
               return false;"
               action="remove"
               class="removeXMark"
               controller="sarCart">X</a>
        </td>
        <td>
            %{--<g:remoteLink action="removeAll"--}%
                          %{--controller="sarCart"--}%
                          %{--params="${[id:CartAssay.findByShoppingItem(it['item']).id, class:(CartAssay.findByShoppingItem(it['item'])).class, version:(CartAssay.findByShoppingItem(it['item'])).version]}"--}%
                          %{--update="sarCartContent"--}%
                          %{--onComplete="Effect.Pulsate('shoppingCartContent', {pulses: 1, duration: 1.0});">--}%
                %{--Remove All--}%
            %{--</g:remoteLink>--}%
        </td>
    </tr>
</g:each>
