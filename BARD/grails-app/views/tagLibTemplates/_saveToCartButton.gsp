<g:if test="${!hideLabel}">
    <label class="checkbox">
</g:if>
    <g:checkBox name="saveToCart" class="addToCartCheckbox" checked="${isInCart}"
                data-cart-name="${name}" data-cart-id="${id}" data-cart-type="${type}"
                data-cart-smiles="${smiles}"/>
<g:if test="${!hideLabel}">
    Save to Cart for analysis
</label>
</g:if>