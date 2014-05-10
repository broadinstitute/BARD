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

function style(element,name,value){                           //#1
    name = name.replace(/-([a-z])/ig,                           //#2
        function(all,letter){
            return letter.toUpperCase();
        });

    if (typeof value !== 'undefined') {                        //#3
        element.style[name] = value;
    }

    return element.style[name];                                //#4
}


(function(){                                               //#1

    var translations = {                                     //#2
        "for": "htmlFor",
        "class": "className",
        readonly: "readOnly",
        maxlength: "maxLength",
        cellspacing: "cellSpacing",
        rowspan: "rowSpan",
        colspan: "colSpan",
        tabindex: "tabIndex",
        cellpadding: "cellPadding",
        usemap: "useMap",
        frameborder: "frameBorder",
        contenteditable: "contentEditable"
    };



    window.attr = function(element,name,value) {              //#3
        var property = translations[name] || name,
            propertyExists = typeof element[ property ] !== "undefined";

        if (typeof value !== "undefined") {
            if (propertyExists) {
                element[property] = value;
            }
            else {
                element.setAttribute(name,value);
            }
        }

        return propertyExists ?
            element[property] :
            element.getAttribute(name);
    };

})();

var subject = document.getElementById('testSubject');      //#4
assert(attr(subject,'id') === 'testSubject',
    "id value fetched");

assert(attr(subject,'id','other') === 'other',
    "new id value set");

(function(){                                      //#1

    var PROPERTIES = {                              //#2
        position: "absolute",
        visibility: "hidden",
        display: "block"
    };

    window.getDimensions = function(element) {      //#3

        var previous = {};                            //#4
        for (var key in PROPERTIES) {
            previous[key] = element.style[key];
            element.style[key] = PROPERTIES[key];       //#5
        }

        var result = {                                //#6
            width: element.offsetWidth,
            height: element.offsetHeight
        };

        for (key in PROPERTIES) {                     //#7
            element.style[key] = previous[key];
        }
        return result;
    };

})();


var dimensions = getDimensions(withShuriken);        //#10

assert(dimensions.width == 36,                       //#11
    "Shuriken image width fetched; actual: " +
        dimensions.width + ", expected: 36");


function assert(value, desc) {
    var resultsList = document.getElementById("results");
    if (!resultsList) {
        resultsList = document.createElement('ul');
        document.getElementsByTagName('body')[0].appendChild(resultsList);
        resultsList.setAttribute('id','results');
    }
    var li = document.createElement("li");
    li.className = value ? "pass" : "fail";
    li.appendChild(document.createTextNode(desc));
    resultsList.appendChild(li);
}
