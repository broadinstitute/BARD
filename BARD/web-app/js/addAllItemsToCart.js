/* Copyright (c) 2014, The Broad Institute
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
 */

$(document).on('click', '#addAllItemsToCart', function () {
    var mainDivName = $(this).attr('mainDivName');
    var saveToCartElements = $('#' + mainDivName + ' input.addToCartCheckbox');
    var payloadArray = new Array();
    var spinnerImageLink = '<img src=' + bardAppContext + '"/images/loading_icon.gif" alt="loading" title="loading" height="16" width="16" style="background-color: transparent;"/>';
    var saveAllButtonElement = $('#' + mainDivName + ' a#addAllItemsToCart');
    var buttonHtml = $(saveAllButtonElement).html()

    saveToCartElements.each(function (index, element) {
        var id = $(element).attr('data-cart-id');
        var name = $(element).attr('data-cart-name');
        var type = $(element).attr('data-cart-type');
        var smiles = $(element).attr('data-cart-smiles');
        var payloadArrayItem = {
            'id':id,
            'type':type,
            'name':name,
            'smiles':smiles
        };
        payloadArray[index] = payloadArrayItem;
    });

    var payloadArrayJSON = JSON.stringify(payloadArray);

    $.ajax({
        url:bardAppContext + '/queryCart/addItems',
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
        error: handleAjaxError(function (XMLHttpRequest, textStatus, errorThrown) {
            $(saveAllButtonElement).find('i#addAllItemsToCartButtonIcon').addClass('icon-exclamation-sign') //add an exclamation-sign to the button
        }),
        complete:function () {
        }
    });
});
