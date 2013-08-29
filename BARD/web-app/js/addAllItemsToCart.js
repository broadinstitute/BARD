$(document).on('click', '#addAllItemsToCart', function () {
    var mainDivName = $(this).attr('mainDivName');
    var saveToCartElements = $('#' + mainDivName + ' input.addToCartCheckbox');
    var payloadArray = new Array();
    var spinnerImageLink = '<img src="/bardwebclient/static/images/loading_icon.gif" alt="loading" title="loading" height="16" width="16" style="background-color: transparent;"/>';
    var saveAllButtonElement = $('#' + mainDivName + ' a#addAllItemsToCart');
    var buttonHtml = $(saveAllButtonElement).html()

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
            $(saveAllButtonElement).find('i#addAllItemsToCartButtonIcon').removeClass(); //remove any existing icons
            $(saveAllButtonElement).html(spinnerImageLink + buttonHtml);
        },
        success:function (data) {
            saveToCartElements.each(function (index, element) {
                $(element).attr('checked', 'checked');
                queryCart.publishCartChangeEvent('cart.itemAdded', element.id);
                $(saveAllButtonElement).html(buttonHtml); //restore back the button html content
                $(saveAllButtonElement).find('i#addAllItemsToCartButtonIcon').removeClass(); //remove any existing icons
            });
        },
        error:function (XMLHttpRequest, textStatus, errorThrown) {
            $(saveAllButtonElement).find('i#addAllItemsToCartButtonIcon').addClass('icon-exclamation-sign') //add an exclamation-sign to the button
        },
        complete:function () {
        }
    });
});
