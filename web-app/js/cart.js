/*
 * Event handlers to interact with the QueryCart.
 */

function QueryCart() {
}

QueryCart.prototype.toggleDetailsHandler = function() {
    $(".panel").toggle("fast");
    $(this).toggleClass("active");
    return false;
}

QueryCart.prototype.refreshSummaryView = function() {
    var ajaxLocation='#summaryView';
    jQuery.ajax({  type:'GET',
        url:'/bardwebquery/queryCart/refreshSummaryView',
        success:function(data){
            jQuery(ajaxLocation).html(data);
        }
    });
}

QueryCart.prototype.refreshDetailsView = function() {
    var ajaxLocation='#detailView';
    jQuery.ajax({  type:'GET',
        url:'/bardwebquery/queryCart/refreshDetailsView',
        success:function(data){
            jQuery(ajaxLocation).html(data);
        }
    });
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
            'smiles': smiles},
        url:'/bardwebquery/queryCart/addItem',
        success:function (data) {
            $('.addToCartCheckbox,#summaryView,#detailView').trigger('cart.itemAdded', id);
        }
    });
    return true;
}

QueryCart.prototype.removeItemFromCartHandler = function() {
    var id = $(this).attr('data-cart-id');
    var type = $(this).attr('data-cart-type');
    jQuery.ajax({  type:'POST',
        data:{'id': id,
            'type': type},
        url:'/bardwebquery/queryCart/removeItem',
        success:function (data) {
            $('.addToCartCheckbox,#summaryView,#detailView').trigger('cart.itemRemoved', id);
        }
    });
    return true;
}

QueryCart.prototype.removeAll = function() {
    jQuery.ajax({  type:'POST',
        url:'/bardwebquery/queryCart/removeAll',
        success:function(data){
            $('.addToCartCheckbox,#summaryView,#detailView').trigger('cart.itemRemoved');
        },
        error:function(XMLHttpRequest,textStatus,errorThrown){
            alert('problem clearing cart');
            console.log(errorThrown);
        }
    });
    return false;
}

QueryCart.prototype.refreshInCartCheckboxes = function(event, idToTarget) {
    var id = $(this).attr('data-cart-id');
    if (idToTarget != null && idToTarget != id) {
        return false;
    }
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
    $(document).on('cart.itemAdded', '.addToCartCheckbox', this.refreshInCartCheckboxes);
    $(document).on('cart.itemAdded', '#summaryView', this.refreshSummaryView);
    $(document).on('cart.itemAdded', '#detailView', this.refreshDetailsView);
    $(document).on('cart.itemRemoved', '.addToCartCheckbox', this.refreshInCartCheckboxes);
    $(document).on('cart.itemRemoved', '#summaryView', this.refreshSummaryView);
    $(document).on('cart.itemRemoved', '#detailView', this.refreshDetailsView);
    $(document).on('cart.refreshCheckboxes', '.addToCartCheckbox', this.refreshInCartCheckboxes);

    // Refresh the screen periodically in case changes have been made in another tab/window
    var interval = 2000
    setInterval(this.refreshSummaryView, interval)
    setInterval(this.refreshDetailsView, interval)
    setInterval($('.addToCartCheckbox').trigger('cart.refreshCheckboxes'), interval)
}

var queryCart = new QueryCart();
queryCart.init();
