var showingCartDetails=0;
var ajaxLocation='#summaryView';

function QueryCart() {
}

QueryCart.prototype.toggleDetailsHandler = function() {
    $(".panel").toggle("fast");
    $(this).toggleClass("active");
    if (showingCartDetails){
        showingCartDetails = 0;
        ajaxLocation='#summaryView';
        jQuery.ajax({  type:'POST',
            data:{'stt':showingCartDetails},
            url:'/bardwebquery/queryCart/updateOnscreenCart',
            success:function(data,textStatus){
                jQuery(ajaxLocation).html(data);
            }
        });

    } else   {
        showingCartDetails = 1;
        ajaxLocation='#detailView';
        jQuery.ajax({  type:'POST',
            data:{'stt':showingCartDetails},
            url:'/bardwebquery/queryCart/updateOnscreenCart',
            success:function(data,textStatus){
                jQuery(ajaxLocation).html(data);
            }
        });
    }
    return false;
}

QueryCart.prototype.addAssayToCartHandler = function() {
    var id = $(this).attr('data-cart-id');
    var name = $(this).attr('data-cart-name');
    jQuery.ajax({  type:'POST',
        data:{'id': id,
            'class': 'class querycart.CartAssay',
            'assayTitle':name,
            'version': '0',
            'stt':showingCartDetails},
        url:'/bardwebquery/queryCart/add',
        success:function(data,textStatus){
            jQuery(ajaxLocation).html(data);
        }
    });
    return false;
}

QueryCart.prototype.addCompoundToCartHandler = function() {
    var id = $(this).attr('data-cart-id');
    var name = $(this).attr('data-cart-name');
    var smiles = $(this).attr('data-cart-smiles');
    jQuery.ajax({  type:'POST',
        data:{'id': id,
            'class':'class querycart.CartCompound',
            'name': name,
            'smiles': smiles,
            'version':'0',
            'stt':showingCartDetails},
        url:'/bardwebquery/queryCart/add',
        success:function (data, textStatus) {
            jQuery(ajaxLocation).html(data);
        }
    });
    return false;
}

QueryCart.prototype.addProjectToCartHandler = function() {
    var id = $(this).attr('data-cart-id');
    var name = $(this).attr('data-cart-name');
    jQuery.ajax({  type:'POST',
        data:{'id': id,
            'class':'class querycart.CartProject',
            'projectName': name,
            'version':'0',
            'stt':showingCartDetails},
        url:'/bardwebquery/queryCart/add',
        success:function (data, textStatus) {
            jQuery(ajaxLocation).html(data);
        }
    });
    return false;
}

QueryCart.prototype.removeItem = function() {
    var url = $(this).attr('href');
    jQuery.ajax({  type:'POST',
        url:url,
        success:function (data, textStatus) {
            jQuery('#detailView').html(data);
        },
        error:function (XMLHttpRequest, textStatus, errorThrown) {
            alert('problem removing ' + url);
            console.log(errorThrown);
        }
    });
    return false;
}

QueryCart.prototype.removeAll = function() {
    jQuery.ajax({  type:'POST',
        url:'/bardwebquery/queryCart/removeAll',
        success:function(data,textStatus){
            jQuery('#detailView').html(data);
        },
        error:function(XMLHttpRequest,textStatus,errorThrown){
            alert('problem clearing cart');
            console.log(errorThrown);
        }
    });
    return false;
}

QueryCart.prototype.updateItemSavedIndicator = function() {

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
    $(document).on('click', '.addAssayToCart', this.addAssayToCartHandler);
    $(document).on('click', '.addCompoundToCart', this.addCompoundToCartHandler);
    $(document).on('click', '.addProjectToCart', this.addProjectToCartHandler);
    $(document).on('click', '.removeItemFromCart', this.removeItem);
    $(document).on('click', '.removeAllFromCart', this.removeAll);
    $(document).on('click', '.requestDoseResponseImage', this.requestDoseResponseImageHandler);
}

var queryCart = new QueryCart();
queryCart.init();
