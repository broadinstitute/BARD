var showingCartDetails=0;
var ajaxLocation='#summaryView';

var QueryCart = (function() {

    function toggleDetailsHandler(event) {
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

    function addAssayToCartHandler() {
        var id = $(this).attr('data-cart-id');
        var name = $(this).attr('data-cart-name');
        jQuery.ajax({  type:'POST',
            data:{'id': id,
                'class': 'class bardqueryapi.CartAssay',
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

    function addCompoundToCartHandler() {
        var id = $(this).attr('data-cart-id');
        var name = $(this).attr('data-cart-name');
        var smiles = $(this).attr('data-cart-smiles');
        jQuery.ajax({  type:'POST',
            data:{'id': id,
                'class':'class bardqueryapi.CartCompound',
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

    function addProjectToCartHandler() {
        var id = $(this).attr('data-cart-id');
        var name = $(this).attr('data-cart-name');
        jQuery.ajax({  type:'POST',
            data:{'id': id,
                'class':'class bardqueryapi.CartProject',
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

    function removeItem() {
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

    function removeAll() {
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

    function init() {
        $(document).on('click', '.trigger', toggleDetailsHandler);
        $(document).on('click', '.addAssayToCart', addAssayToCartHandler);
        $(document).on('click', '.addCompoundToCart', addCompoundToCartHandler);
        $(document).on('click', '.addProjectToCart', addProjectToCartHandler);
        $(document).on('click', '.removeItemFromCart', removeItem);
        $(document).on('click', '.removeAllFromCart', removeAll);
    }


    init();

})();
