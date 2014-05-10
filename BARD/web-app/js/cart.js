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

/*
 * Event handlers to interact with the QueryCart.
 */

function QueryCart() {
}

QueryCart.prototype.toggleDetailsHandler = function () {
    $(".panel").toggle("fast");
    $(".trigger").toggleClass("active");
    return false;
};

QueryCart.prototype.refreshSummaryView = function () {
    var ajaxLocation = '#summaryView';
    jQuery.ajax({  type: 'GET',
        url: bardAppContext + '/queryCart/refreshSummaryView',
        success: function (data) {
            jQuery(ajaxLocation).html(data);
        }
    });
};

QueryCart.prototype.refreshDetailsView = function () {
    var ajaxLocation = '#detailView';
    jQuery.ajax({  type: 'GET',
        url: bardAppContext + '/queryCart/refreshDetailsView',
        success: function (data) {
            jQuery(ajaxLocation).html(data);
        }
    });
};

QueryCart.prototype.addItemToCartHandler = function () {
    var id = $(this).attr('data-cart-id');
    var name = $(this).attr('data-cart-name');
    var type = $(this).attr('data-cart-type');
    var smiles = $(this).attr('data-cart-smiles');
    jQuery.ajax({  type: 'POST',
        data: {
            'id': id,
            'type': type,
            'name': name,
            'smiles': smiles
        },
        url: bardAppContext + '/queryCart/addItem',
        success: function (data) {
            queryCart.publishCartChangeEvent('cart.itemAdded', id);
        }
    });
    return true;
};

QueryCart.prototype.removeItemFromCartHandler = function () {
    var id = $(this).attr('data-cart-id');
    var type = $(this).attr('data-cart-type');
    jQuery.ajax({  type: 'POST',
        data: {'id': id,
            'type': type},
        url: bardAppContext + '/queryCart/removeItem',
        success: function (data) {
            queryCart.publishCartChangeEvent('cart.itemRemoved', id);
        }
    });
    return true;
};

QueryCart.prototype.removeAll = function () {
    jQuery.ajax({  type: 'POST',
        url: bardAppContext + '/queryCart/removeAll',
        success: function (data) {
            queryCart.publishCartChangeEvent('cart.itemRemoved');
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            alert('problem clearing cart');
            console.log(errorThrown);
        }
    });
    return false;
};

QueryCart.prototype.publishCartChangeEvent = function (eventName, itemChangedId) {
    this.refreshSummaryView();
    this.refreshDetailsView();
    $('.addToCartCheckbox').trigger(eventName, itemChangedId);
};

QueryCart.prototype.refreshInCartCheckboxes = function (event, idToTarget) {
    var id = $(this).attr('data-cart-id');
    if (idToTarget != null && idToTarget != id) {
        return false;
    }
    var type = $(this).attr('data-cart-type');
    var elementToUpdate = $(this);
    jQuery.ajax({  type: 'POST',
        url: bardAppContext + '/queryCart/isInCart',
        data: {'id': id,
            'type': type},
        success: function (data) {
            if (data == 'false') {
                elementToUpdate.prop('checked', false);
            }
            else {
                elementToUpdate.prop('checked', true);
            }
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            alert('problem refreshing');
            console.log(errorThrown);
        }
    });
};

QueryCart.prototype.init = function () {
    $(document).on('click', '.trigger', this.toggleDetailsHandler);
    $(document).on('keypress', '.trigger', function (event) {
        if (event.keyCode == 13) {
            $(this).click();
        }
    })
    $(document).on('click', '.removeItemFromCart', this.removeItemFromCartHandler);
    $(document).on('click', '.removeAllFromCart', this.removeAll);
    $(document).on('click', '.addToCartCheckbox:checked', this.addItemToCartHandler);
    $(document).on('click', '.addToCartCheckbox:not(:checked)', this.removeItemFromCartHandler);
    $(document).on('cart.itemAdded', '.addToCartCheckbox', this.refreshInCartCheckboxes);
    $(document).on('cart.itemRemoved', '.addToCartCheckbox', this.refreshInCartCheckboxes);
    $(document).on('cart.refreshCheckboxes', '.addToCartCheckbox', this.refreshInCartCheckboxes);
//    This is for the addToCart link we give in each compound's option pull-down menu
    $(document).on('click', '.addToCartLink', this.addItemToCartHandler);
};

var queryCart = new QueryCart();
queryCart.init();
