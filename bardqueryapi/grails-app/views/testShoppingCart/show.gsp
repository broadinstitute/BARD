

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Shopping Cart</title>    
		<g:javascript library="application" />     
        <g:javascript library="prototype" />
        <g:javascript library="scriptaculous" />
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
        </div>
        <div class="body">
            <h1>Shopping Cart</h1>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                   	        <th>Product</th>
                        
                   	        <th>Qty</th>

                   	        <th>&nbsp;</th>

                   	        <th>&nbsp;</th>

                   	        <th>&nbsp;</th>
                   	    
                        </tr>
                    </thead>
                    <tbody id="shoppingCartContent">
						<g:render plugin="shoppingCart" template="shoppingCartContent"/>
					</tbody>
				</table>
				<br />
				<g:remoteLink action="checkOut"
					update="shoppingCartContent"
					onComplete="Effect.Pulsate('shoppingCartContent', {pulses: 1, duration: 1.0});">
					Check out
				</g:remoteLink>
			</div>
			<h1>Products</h1>
			<div class="list">
                <table>
                    <thead>
                        <tr>
                        
                   	        <th>Product</th>

                   	        <th>&nbsp;</th>
                   	    
                        </tr>
                    </thead>
                    <tbody id="shoppingCartContent">
						<g:each in="${com.metasieve.shoppingcart.Shoppable.list()}" var="product">
							<tr>
								<td>
									${product}
								</td>
								<td>
									<g:remoteLink action="add"
										params="${[id:product.id, class:product.class, version:product.version]}"
										update="shoppingCartContent"
										onComplete="Effect.Pulsate('shoppingCartContent', {pulses: 1, duration: 1.0});">
										Add
									</g:remoteLink>
								</td>
							</tr>
						</g:each>
						<g:each in="${com.metasieve.shoppingcart.ShoppingCartInterfaceTestProduct.list()}" var="product">
							<tr>
								<td>
									${product}
								</td>
								<td>
									<g:remoteLink action="add"
										params="${[id:product.id, class:product.class, version:product.version]}"
										update="shoppingCartContent"
										onComplete="Effect.Pulsate('shoppingCartContent', {pulses: 1, duration: 1.0});">
										Add
									</g:remoteLink>
								</td>
							</tr>
						</g:each>
					</tbody>
				</table>
			</div>
        </div>
    </body>
</html>
