%{-- Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 --}%



<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
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
