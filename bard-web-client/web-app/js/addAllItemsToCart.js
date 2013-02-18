$(document).on('click', '#addAllItemsToCart', function () {
    var mainDivName = $(this).attr('mainDivName');
    var saveToCartElements = $('#' + mainDivName + ' input.addToCartCheckbox');
    var payloadArray = new Array();

    saveToCartElements.each(function (index, element) {
        var id = $(element).attr('data-cart-id');
        var name = $(element).attr('data-cart-name');
        var type = $(element).attr('data-cart-type');
        var smiles = $(element).attr('data-cart-smiles');
        var numActive = $(element).attr('data-cart-numActive');
        var numAssays = $(element).attr('data-cart-numAssays');
        var payloadArrayItem = {
            'id':id,
            'type':type,
            'name':name,
            'smiles':smiles,
            'numActive':numActive,
            'numAssays':numAssays
        };
        payloadArray[index] = payloadArrayItem;
    });

    var payloadArrayJSON = JSON.stringify(payloadArray);

    $.ajax({
        url:'/bardwebclient/queryCart/addItems',
        type:'POST',
        cache:false,
//        dataType:'json',
        data:{'items':payloadArrayJSON},
        //timeout: 10000,
        beforeSend:function () {
        },
        success:function (data) {
            saveToCartElements.each(function (index, element) {
                $(element).attr('checked', 'checked');
                queryCart.publishCartChangeEvent('cart.itemAdded', element.id);
            });
        },
        error:function (XMLHttpRequest, textStatus, errorThrown) {},
        complete:function () {}
    });
});
