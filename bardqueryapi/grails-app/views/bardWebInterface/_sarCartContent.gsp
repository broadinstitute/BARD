<%@ page import="bardqueryapi.CartAssay" %>
<%@ page import="com.metasieve.shoppingcart.ShoppingCartService" %>
%{--<g:each in="${com.metasieve.shoppingcart.ShoppingCartService.findAll {it}}" var="elem" status="i">--}%
<%
    ShoppingCartService  shoppingCartService = grailsApplication.classLoader.loadClass('com.metasieve.shoppingcart.ShoppingCartService').newInstance()
%>
<r:script>
    alert ('obj=${shoppingCartService.findAll().size()}') ;
</r:script>
<g:each in="${shoppingCartService.findAll()}" var="elem" status="i">
        <tr>
        <td>

            %{--${CartAssay.findByShoppingItem(elem['item'])}--}%
        </td>
        <td>
            %{--${elem['qty']}--}%
        </td>
        <td>
            <a href="/bardqueryapi/sarCart/add/2?class=class+bardqueryapi.CartAssay&version=0"
               onclick="jQuery.ajax({  type:'POST',
                   data:{'id': '2','class': 'class bardqueryapi.CartAssay','version': '0'},
                   url:'/bardqueryapi/sarCart/add',
                   success:function(data,textStatus){
                       jQuery('#sarCartContent').html(data);
                       alert('hi');
                   },
                   error:function(XMLHttpRequest,textStatus,errorThrown){
                       alert('ho')
                   },
                   complete:function(XMLHttpRequest,textStatus){
                       Effect.Pulsate('shoppingCartContent', {pulses: 1, duration: 1.0});
                       alert('hey');
                   }
               });
               return false;"
               action="add"
               controller="sarCart">add</a>
            %{--<g:remoteLink action="add"--}%
                          %{--controller="sarCart"--}%
                          %{--params="${[id:CartAssay.findByShoppingItem(it['item']).id, class:(CartAssay.findByShoppingItem(it['item'])).class, version:(CartAssay.findByShoppingItem(it['item'])).version]}"--}%
                          %{--update="sarCartContent"--}%
                          %{--onComplete="Effect.Pulsate('shoppingCartContent', {pulses: 1, duration: 1.0});">--}%
                %{--Add--}%
            %{--</g:remoteLink>--}%
        </td>
        <td>
            %{--<g:remoteLink action="remove"--}%
                          %{--controller="sarCart"--}%
                          %{--params="${[id:CartAssay.findByShoppingItem(it['item']).id, class:(CartAssay.findByShoppingItem(it['item'])).class, version:(CartAssay.findByShoppingItem(it['item'])).version]}"--}%
                          %{--update="sarCartContent"--}%
                          %{--onComplete="Effect.Pulsate('shoppingCartContent', {pulses: 1, duration: 1.0});">--}%
                %{--Remove--}%
            %{--</g:remoteLink>--}%
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
