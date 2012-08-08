// **This example illustrates the binding of DOM events to View methods.**
//
// _Working example: [2.html](../2.html)._  
// _[Go to Example 3](3.html)_

//
(function($){
  var ListView = Backbone.View.extend({
    el: $('body'), // el attaches to existing element
    // `events`: Where DOM events are bound to View methods. Backbone doesn't have a separate controller to handle such bindings; it all happens in a View.
    events: {
      'click button#add': 'addItem'
    },
    initialize: function(){
      _.bindAll(this, 'render', 'addItem'); // every function that uses 'this' as the current object should be in here
      
      this.counter = 0; // total number of items added thus far
      this.render();
    },
    // `render()` now introduces a button to add a new list item.
    render: function(){
      $(this.el).append("<button id='add'>Add list item</button>");
      $(this.el).append("<ul></ul>");
    },
    // `addItem()`: Custom function called via `click` event above.
    addItem: function(){
      this.counter++;
      $('ul', this.el).append("<li>hello world"+this.counter+"</li>");
    }
  });

  var listView = new ListView();      
})(jQuery);

// <div style="float:left; margin-bottom:40px;"><img style="margin:8px; margin-right:10px;" src="https://twimg0-a.akamaihd.net/profile_images/1389031556/Artur_Adib-164x164_normal.png"/></div> <div style="background:rgb(245,245,255); padding:10px;">For more on coding and startups, follow me at:<br/> <a target="_blank" href="http://twitter.com/arturadib">@arturadib</a> </div>
// <script>
//   if (window.location.href.search(/\?x/) < 0) {
//     var _gaq = _gaq || [];
//     _gaq.push(['_setAccount', 'UA-924459-7']);
//     _gaq.push(['_trackPageview']);
//     (function() {
//       var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
//       ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
//       var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
//     })();
//   }
// </script>
