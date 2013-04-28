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
