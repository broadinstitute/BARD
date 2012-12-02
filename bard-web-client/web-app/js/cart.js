/*
 * Event handlers to interact with the QueryCart.
 */

function QueryCart() {
}

QueryCart.prototype.toggleDetailsHandler = function() {
    $(".panel").toggle("fast");
    $(".trigger").toggleClass("active");
    return false;
};

QueryCart.prototype.refreshSummaryView = function() {
    var ajaxLocation='#summaryView';
    jQuery.ajax({  type:'GET',
        url:'/bardwebclient/queryCart/refreshSummaryView',
        success:function(data){
            jQuery(ajaxLocation).html(data);
        }
    });
};

QueryCart.prototype.refreshDetailsView = function() {
    var ajaxLocation='#detailView';
    jQuery.ajax({  type:'GET',
        url:'/bardwebclient/queryCart/refreshDetailsView',
        success:function(data){
            jQuery(ajaxLocation).html(data);
        }
    });
};

QueryCart.prototype.addItemToCartHandler = function() {
    var id = $(this).attr('data-cart-id');
    var name = $(this).attr('data-cart-name');
    var type = $(this).attr('data-cart-type');
    var smiles = $(this).attr('data-cart-smiles');
    var numActive = $(this).attr('data-cart-numActive');
    var numAssays=$(this).attr('data-cart-numAssays');
    jQuery.ajax({  type:'POST',
        data:{
            'id': id,
            'type': type,
            'name': name,
            'smiles': smiles,
            'numActive':numActive,
            'numAssays':numAssays
        },
        url:'/bardwebclient/queryCart/addItem',
        success:function (data) {
            queryCart.publishCartChangeEvent('cart.itemAdded', id);
        }
    });
    return true;
};

QueryCart.prototype.removeItemFromCartHandler = function() {
    var id = $(this).attr('data-cart-id');
    var type = $(this).attr('data-cart-type');
    jQuery.ajax({  type:'POST',
        data:{'id': id,
            'type': type},
        url:'/bardwebclient/queryCart/removeItem',
        success:function (data) {
            queryCart.publishCartChangeEvent('cart.itemRemoved', id);
        }
    });
    return true;
};

QueryCart.prototype.removeAll = function() {
    jQuery.ajax({  type:'POST',
        url:'/bardwebclient/queryCart/removeAll',
        success:function(data){
            queryCart.publishCartChangeEvent('cart.itemRemoved');
        },
        error:function(XMLHttpRequest,textStatus,errorThrown){
            alert('problem clearing cart');
            console.log(errorThrown);
        }
    });
    return false;
};

QueryCart.prototype.publishCartChangeEvent = function(eventName, itemChangedId) {
    $('.addToCartCheckbox,#summaryView,#detailView').trigger(eventName, itemChangedId);
};

QueryCart.prototype.refreshInCartCheckboxes = function(event, idToTarget) {
    var id = $(this).attr('data-cart-id');
    if (idToTarget != null && idToTarget != id) {
        return false;
    }
    var type = $(this).attr('data-cart-type');
    var elementToUpdate = $(this);
    jQuery.ajax({  type:'POST',
        url:'/bardwebclient/queryCart/isInCart',
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
};

QueryCart.prototype.init = function() {
    $(document).on('click', '.trigger', this.toggleDetailsHandler);
    $(document).on('click', '.removeItemFromCart', this.removeItemFromCartHandler);
    $(document).on('click', '.removeAllFromCart', this.removeAll);
    $(document).on('click', '.addToCartCheckbox:checked', this.addItemToCartHandler);
    $(document).on('click', '.addToCartCheckbox:not(:checked)', this.removeItemFromCartHandler);
    $(document).on('cart.itemAdded', '.addToCartCheckbox', this.refreshInCartCheckboxes);
    $(document).on('cart.itemAdded', '#summaryView', this.refreshSummaryView);
    $(document).on('cart.itemAdded', '#detailView', this.refreshDetailsView);
    $(document).on('cart.itemRemoved', '.addToCartCheckbox', this.refreshInCartCheckboxes);
    $(document).on('cart.itemRemoved', '#summaryView', this.refreshSummaryView);
    $(document).on('cart.itemRemoved', '#detailView', this.refreshDetailsView);
    $(document).on('cart.refreshCheckboxes', '.addToCartCheckbox', this.refreshInCartCheckboxes);
};

var queryCart = new QueryCart();
queryCart.init();
