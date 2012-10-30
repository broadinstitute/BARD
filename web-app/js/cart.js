var showingCartDetails=false;
var ajaxLocation='#summaryView';

function QueryCart() {
}

QueryCart.prototype.toggleDetailsHandler = function() {
    $(".panel").toggle("fast");
    $(this).toggleClass("active");
    if (showingCartDetails){
        showingCartDetails = false;
        ajaxLocation='#summaryView';
        jQuery.ajax({  type:'POST',
            data:{'showCartDetails':showingCartDetails},
            url:'/bardwebquery/queryCart/updateOnscreenCart',
            success:function(data,textStatus){
                jQuery(ajaxLocation).html(data);
            }
        });

    } else   {
        showingCartDetails = true;
        ajaxLocation='#detailView';
        jQuery.ajax({  type:'POST',
            data:{'showCartDetails':showingCartDetails},
            url:'/bardwebquery/queryCart/updateOnscreenCart',
            success:function(data,textStatus){
                jQuery(ajaxLocation).html(data);
            }
        });
    }
    return false;
}

QueryCart.prototype.addItemToCartHandler = function() {
    var id = $(this).attr('data-cart-id');
    var name = $(this).attr('data-cart-name');
    var type = $(this).attr('data-cart-type');
    var smiles = $(this).attr('data-cart-smiles');
    jQuery.ajax({  type:'POST',
        data:{'id': id,
            'type': type,
            'name': name,
            'smiles': smiles,
            'showCartDetails':showingCartDetails},
        url:'/bardwebquery/queryCart/addItem',
        success:function (data, textStatus) {
            jQuery(ajaxLocation).html(data);
        }
    });
    return true;
}

QueryCart.prototype.removeItemFromCartHandler = function() {
    var id = $(this).attr('data-cart-id');
    var type = $(this).attr('data-cart-type');
    jQuery.ajax({  type:'POST',
        data:{'id': id,
            'type': type,
            'showCartDetails':showingCartDetails},
        url:'/bardwebquery/queryCart/removeItem',
        success:function (data, textStatus) {
            jQuery(ajaxLocation).html(data);
            $('.addToCartCheckbox').trigger('cart.itemRemoved');
        }
    });
    return true;
}

QueryCart.prototype.removeAll = function() {
    jQuery.ajax({  type:'POST',
        url:'/bardwebquery/queryCart/removeAll',
        success:function(data,textStatus){
            jQuery('#detailView').html(data);
            $('.addToCartCheckbox').trigger('cart.itemRemoved');
        },
        error:function(XMLHttpRequest,textStatus,errorThrown){
            alert('problem clearing cart');
            console.log(errorThrown);
        }
    });
    return false;
}

QueryCart.prototype.refreshCartStatus = function() {
    var id = $(this).attr('data-cart-id');
    var type = $(this).attr('data-cart-type');
    var elementToUpdate = $(this);
    jQuery.ajax({  type:'POST',
        url:'/bardwebquery/queryCart/isInCart',
        data:{'id': id,
            'type': type},
        success:function(data){
            if (data == 'false') {
                elementToUpdate.prop('checked', false);
            }
            else {
                elementToUpdate.prop('checked', true);
            }
        },
        error:function(XMLHttpRequest,textStatus,errorThrown){
            alert('problem refreshing');
            console.log(errorThrown);
        }
    });
}

QueryCart.prototype.requestDoseResponseImageHandler = function() {
    var id = $(this).attr('concs');
    var name = $(this).attr('resps');
    jQuery.ajax({  type:'POST',
        data:{'id': id,
            'class': 'class DrcCurveCommand'},
        contentType: "image/png",
        url:'/bardwebquery/doseResponseCurve/doseResponseCurve',
        success:function(data,textStatus){
            jQuery('#plot').html('<img src="' + data + '" />');
        }
    });
    return false;
}


QueryCart.prototype.init = function() {
    $(document).on('click', '.trigger', this.toggleDetailsHandler);
    $(document).on('click', '.removeItemFromCart', this.removeItemFromCartHandler);
    $(document).on('click', '.removeAllFromCart', this.removeAll);
    $(document).on('click', '.addToCartCheckbox:checked', this.addItemToCartHandler);
    $(document).on('click', '.addToCartCheckbox:not(:checked)', this.removeItemFromCartHandler);
    $(document).on('click', '.requestDoseResponseImage', this.requestDoseResponseImageHandler);
    $(document).on('cart.itemRemoved', '.addToCartCheckbox', this.refreshCartStatus);
}

var queryCart = new QueryCart();
queryCart.init();
