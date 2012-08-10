// An example Backbone application contributed by
// [Jérôme Gravel-Niquet](http://jgn.me/). This demo uses a simple
// [LocalStorage adapter](backbone-localstorage.html)
// to persist Backbone models within your browser.


var compoundGrid_columns = ['img', 'cid', 'paddedColumn1'];


// Load the application once the DOM is ready, using `jQuery.ready`:
//$(document).ready(function () {
$(function () {

        _.templateSettings = {
        interpolate:/\{\{(.+?)\}\}/g,
        evaluate:/\{!(.+?)!\}/g
    };

    // BBGrid Model
    // ----------

    // Our basic **BBGrid** model has `text`, `order`, and `done` attributes.
    window.CompoundGridRow = Backbone.Model.extend({

        // Default attributes for a CompoundGridRow item.
        defaults:{
            cid:0,
            img:"undefined"
        },
        initialize:function () {

        }

    });

    //  CompoundGridRow Collection
    // ---------------

    // The collection of CompoundGridRows is backed by *localStorage* instead of a remote
    // server.
    window.CompoundGridRowList = Backbone.Collection.extend({

        // Reference to this collection's model.
        model:window.CompoundGridRow,

        url:'/bardqueryapi/bbgrid'

    });

    // Create our global collection of **CompoundGridRows**.
//    window.CompoundGridRows = new CompoundGridRowList;
    window.CompoundGridRows = new CompoundGridRowList();


    // The DOM element for a CompoundGridRow item...
    window.CompoundGridRowView = Backbone.View.extend({

        initialize:function () {
            alert("initializing the view");
        }

    });

    // The Application
    // ---------------

    // Our overall **AppView** is the top-level piece of UI.
    window.AppView = Backbone.View.extend({

        // Instead of generating a new element, bind to the existing skeleton of
        // the App already present in the HTML.
        el:$("#todoapp"),


//
//        // Lazily show the tooltip that tells you to press `enter` to save
//        // a new todo item, after one second.
//        showTooltip:function (e) {
//            var tooltip = this.$(".ui-tooltip-top");
//            var val = this.input.val();
//            tooltip.fadeOut();
//            if (this.tooltipTimeout) clearTimeout(this.tooltipTimeout);
//            if (val == '' || val == this.input.attr('placeholder')) return;
//            var show = function () {
//                tooltip.show().fadeIn();
//            };
//            this.tooltipTimeout = _.delay(show, 1000);
//        }
        // At initialization we bind to the relevant events on the `Todos`
        // collection, when items are added or changed. Kick things off by
        // loading any preexisting todos that might be saved in *localStorage*.
        initialize:function () {
            var tempCompoundGridRowList = [];
            _.each(compounds, function (compound) {
                var compoundGridRow = new window.CompoundGridRow();
                compoundGridRow.cid = compound.cid;
                compoundGridRow.img = compound.img;
                tempCompoundGridRowList.push(compoundGridRow);
            });
            window.CompoundGridRowList = new window.CompoundGridRowList(tempCompoundGridRowList);
            alert("initialized the app");
        }
    });

// Finally, we kick things off by creating the **App**.
    window.App = new AppView;


    // this sequence works
//    var Vertebrate = {};
//    Vertebrate.Model = Backbone.Model.extend();
//    Vertebrate.Collection = Backbone.Collection.extend({
//        model: Vertebrate.Model
//    });
//    var vertebrates = new Vertebrate.Collection([
//        {
//            img: "Angatha",
//            cid: "jawless fishes",
//            paddedColumn1 : "5"
//        },{
//            img: "Chondrichthyes",
//            cid: "cartilaginous fishes",
//            paddedColumn1 : "6"
//        }]);
//    var bbcompounds_table = new Backbone.Table({
//        collection: vertebrates,
//        columns: ["img", "cid", "paddedColumn1"]
//    });


      var Gridbb = {};
      Gridbb.Model = Backbone.Model.extend();
      Gridbb.Collection = Backbone.Collection.extend({
        model: Gridbb.Model
      });
    var tempCompoundGridRowList = [];
//    _.each(compounds, function (compound) {
//        var compoundGridRow = new window.CompoundGridRow();
//        compoundGridRow.cid = compound.cid;
//        compoundGridRow.img = compound.img;
//        tempCompoundGridRowList.push(compoundGridRow);
//    });
    var griddata = new Gridbb.Collection(compounds);
    var bbcompounds_table = new Backbone.Table({
        collection: griddata,
        columns: ["img", "cid", "paddedColumn1"]
    });








//    var bbcompounds_table = new Backbone.Table({
//        collection: window.CompoundGridRowList,
//        columns: ["img", "cid", "paddedColumn1"]
//    });

    // Render and append the table.
    $("#compoundDiv").append(bbcompounds_table.render().el);


});