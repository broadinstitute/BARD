var linkedVisualizationModule = (function () {
    //
    //  Variables to describe the layout of the whole page, with special attention
    //   to the unexpanded widgets
    //
    var grandWidth = 1052,// width of the entire display
        totalWidgetNumber = 4, // how many widgets are we dealing with
        widgetHeight = 270, // how tall is each individual widget
        widgetSpacing = 7, // how much vertical space between widgets
        margin = {top: 30, right: 20, bottom: 30, left: 10},  // boundaries of displayable area
        width = grandWidth - margin.left - margin.right, // displayable width
        height = widgetHeight - margin.top - margin.bottom, // displayable height
        widgetWidth = 25,//grandWidth / totalWidgetNumber,   // each individual widget width
        quarterWidgetWidth = widgetWidth / 4,   // useful spacer
        allowThisMuchExtraSpaceInWidgetForATitle = 30, // the title in your widget
        widgetWidthWithoutSpacing = 24,//widgetWidth - (widgetSpacing * 0.5),
        widgetHeightWithTitle = widgetHeight + allowThisMuchExtraSpaceInWidgetForATitle, // final widget width

    // We have to explicitly pass in the size of the pie charts, so describe those here
        pieChartWidth = 250,// widgetWidth - 13,  // how wide is the pie chart
        pieChartRadius = pieChartWidth / 2, // pie chart reuse
        innerRadius = 30, // open circle in pie
        innerRadiusWhenExpanded = 100, // open circle in pie

    // The expanded widgets are described below. These numbers can't be derived from anything else, because you could
    //  in principle put this display anywhere.
        displayWidgetX = 10,// expanded widget X location.
        displayWidgetY = 320, // expanded widget Y location.
        displayWidgetWidth = 1000, // expanded widget Y width.
        displayWidgetHeight = 1000, // expanded widget Y height.
        bigPie = (displayWidgetWidth / 2) - displayWidgetX, // size of pie in display mode

    //  I need to have a color for every possible pie slice. Since the color map is largely arbitrary, d3 does not
    //   seem to provide An easy way to say "please give me another color unlike when you've given me before".  Note
    //   that the colors I want are not _entirely_ arbitrary because really light colors don't seem to look good in
    //   a pie chart filled with brighter colors, so I can't simply use randomizing function with a seed
        colors = [ '#393b79', '#5254a3', '#6b6ecf', '#9c9ede', '#637939', '#8ca252', '#b5cf6b', '#cedb9c', '#8c6d31', '#bd9e39',
            '#e7ba52', '#e7cb94', '#843c39', '#ad494a', '#d6616b', '#e7969c', '#7b4173', '#a55194', '#ce6dbd', '#de9ed6',
            '#1f77b4', '#aec7e8', '#ff7f0e', '#ffbb78', '#2ca02c', '#98df8a', '#d62728', '#ff9896', '#9467bd', '#c5b0d5',
            '#8c564b', '#c49c94', '#e377c2', '#f7b6d2', '#7f7f7f', '#c7c7c7', '#bcbd22', '#dbdb8d', '#17becf', '#9edae5',
            '#1f77b4', '#ff7f0e', '#2ca02c', '#d62728', '#9467bd', '#8c564b', '#e377c2', '#7f7f7f', '#bcbd22', '#17becf',
            '#393b79', '#5254a3', '#6b6ecf', '#9c9ede', '#637939', '#8ca252', '#b5cf6b', '#cedb9c', '#8c6d31', '#bd9e39',
            '#e7ba52', '#e7cb94', '#843c39', '#ad494a', '#d6616b', '#e7969c', '#7b4173', '#a55194', '#ce6dbd', '#de9ed6',
            '#1f77b4', '#aec7e8', '#ff7f0e', '#ffbb78', '#2ca02c', '#98df8a', '#d62728', '#ff9896', '#9467bd', '#c5b0d5',
            '#8c564b', '#c49c94', '#e377c2', '#f7b6d2', '#7f7f7f', '#c7c7c7', '#bcbd22', '#dbdb8d', '#17becf', '#9edae5',
            '#1f77b4', '#ff7f0e', '#2ca02c', '#d62728', '#9467bd', '#8c564b', '#e377c2', '#7f7f7f', '#bcbd22', '#17becf',
            '#5254a3', '#6b6ecf', '#9c9ede', '#637939', '#8ca252', '#b5cf6b', '#cedb9c', '#8c6d31', '#bd9e39',
            '#e7ba52', '#e7cb94', '#843c39', '#ad494a', '#d6616b', '#e7969c', '#7b4173', '#a55194', '#ce6dbd', '#de9ed6',
            '#1f77b4', '#aec7e8', '#ff7f0e', '#ffbb78', '#2ca02c', '#98df8a', '#d62728', '#ff9896', '#9467bd', '#c5b0d5',
            '#8c564b', '#c49c94', '#e377c2', '#f7b6d2', '#7f7f7f', '#c7c7c7', '#bcbd22', '#dbdb8d', '#17becf', '#9edae5',
            '#1f77b4', '#ff7f0e', '#2ca02c', '#d62728', '#9467bd', '#8c564b', '#e377c2', '#7f7f7f', '#bcbd22', '#17becf',
            '#393b79', '#5254a3', '#6b6ecf', '#9c9ede', '#637939', '#8ca252', '#b5cf6b', '#cedb9c', '#8c6d31', '#bd9e39',
            '#e7ba52', '#e7cb94', '#843c39', '#ad494a', '#d6616b', '#e7969c', '#7b4173', '#a55194', '#ce6dbd', '#de9ed6',
            '#1f77b4', '#aec7e8', '#ff7f0e', '#ffbb78', '#2ca02c', '#98df8a', '#d62728', '#ff9896', '#9467bd', '#c5b0d5',
            '#8c564b', '#c49c94', '#e377c2', '#f7b6d2', '#7f7f7f', '#c7c7c7', '#bcbd22', '#dbdb8d', '#17becf', '#9edae5',
            '#1f77b4', '#ff7f0e', '#2ca02c', '#d62728', '#9467bd', '#8c564b', '#e377c2', '#7f7f7f', '#bcbd22', '#17becf'],
        blueColors = [
            '#3182bd', '#6baed6', '#9ecae1', '#c6dbef', '#393b79', '#5254a3', '#6b6ecf', '#9c9ede', '#17becf', '#9edae5', '#1f77b4', '#aec7e8', '#B4FCFF','#82B6FA', '#00FFFF',
            '#3182bd', '#6baed6', '#9ecae1', '#c6dbef', '#393b79', '#5254a3', '#6b6ecf', '#9c9ede', '#17becf', '#9edae5', '#1f77b4', '#aec7e8', '#B4FCFF','#82B6FA', '#00FFFF',
            '#3182bd', '#6baed6', '#9ecae1', '#c6dbef', '#393b79', '#5254a3', '#6b6ecf', '#9c9ede', '#17becf', '#9edae5', '#1f77b4', '#aec7e8', '#B4FCFF','#82B6FA', '#00FFFF',
            '#3182bd', '#6baed6', '#9ecae1', '#c6dbef', '#393b79', '#5254a3', '#6b6ecf', '#9c9ede', '#17becf', '#9edae5', '#1f77b4', '#aec7e8', '#B4FCFF','#82B6FA', '#00FFFF',
            '#3182bd', '#6baed6', '#9ecae1', '#c6dbef', '#393b79', '#5254a3', '#6b6ecf', '#9c9ede', '#17becf', '#9edae5', '#1f77b4', '#aec7e8', '#B4FCFF','#82B6FA', '#00FFFF',
            '#3182bd', '#6baed6', '#9ecae1', '#c6dbef', '#393b79', '#5254a3', '#6b6ecf', '#9c9ede', '#17becf', '#9edae5', '#1f77b4', '#aec7e8', '#B4FCFF','#82B6FA', '#00FFFF',
            '#3182bd', '#6baed6', '#9ecae1', '#c6dbef', '#393b79', '#5254a3', '#6b6ecf', '#9c9ede', '#17becf', '#9edae5', '#1f77b4', '#aec7e8', '#B4FCFF','#82B6FA', '#00FFFF',
            '#3182bd', '#6baed6', '#9ecae1', '#c6dbef', '#393b79', '#5254a3', '#6b6ecf', '#9c9ede', '#17becf', '#9edae5', '#1f77b4', '#aec7e8', '#B4FCFF','#82B6FA', '#00FFFF',
            '#3182bd', '#6baed6', '#9ecae1', '#c6dbef', '#393b79', '#5254a3', '#6b6ecf', '#9c9ede', '#17becf', '#9edae5', '#1f77b4', '#aec7e8', '#B4FCFF','#82B6FA', '#00FFFF',
            '#3182bd', '#6baed6', '#9ecae1', '#c6dbef', '#393b79', '#5254a3', '#6b6ecf', '#9c9ede', '#17becf', '#9edae5', '#1f77b4', '#aec7e8', '#B4FCFF','#82B6FA', '#00FFFF',
            '#3182bd', '#6baed6', '#9ecae1', '#c6dbef', '#393b79', '#5254a3', '#6b6ecf', '#9c9ede', '#17becf', '#9edae5', '#1f77b4', '#aec7e8', '#B4FCFF','#82B6FA', '#00FFFF',
            '#3182bd', '#6baed6', '#9ecae1', '#c6dbef', '#393b79', '#5254a3', '#6b6ecf', '#9c9ede', '#17becf', '#9edae5', '#1f77b4', '#aec7e8', '#B4FCFF','#82B6FA', '#00FFFF',
            '#3182bd', '#6baed6', '#9ecae1', '#c6dbef', '#393b79', '#5254a3', '#6b6ecf', '#9c9ede', '#17becf', '#9edae5', '#1f77b4', '#aec7e8', '#B4FCFF','#82B6FA', '#00FFFF',
            '#3182bd', '#6baed6', '#9ecae1', '#c6dbef', '#393b79', '#5254a3', '#6b6ecf', '#9c9ede', '#17becf', '#9edae5', '#1f77b4', '#aec7e8', '#B4FCFF','#82B6FA', '#00FFFF',
            '#3182bd', '#6baed6', '#9ecae1', '#c6dbef', '#393b79', '#5254a3', '#6b6ecf', '#9c9ede', '#17becf', '#9edae5', '#1f77b4', '#aec7e8', '#B4FCFF','#82B6FA', '#00FFFF',
            '#3182bd', '#6baed6', '#9ecae1', '#c6dbef', '#393b79', '#5254a3', '#6b6ecf', '#9c9ede', '#17becf', '#9edae5', '#1f77b4', '#aec7e8', '#B4FCFF','#82B6FA', '#00FFFF'],


        reddishColors = [
            '#e6550d', '#fd8d3c', '#fdae6b', '#fdd0a2', '#ad494a', '#d6616b', '#e7969c', '#ff7f0e', '#d62728', '#ff9896', '#EF6546','#E6933C', '#FFA500',
            '#e6550d', '#fd8d3c', '#fdae6b', '#fdd0a2', '#ad494a', '#d6616b', '#e7969c', '#ff7f0e', '#d62728', '#ff9896', '#EF6546','#E6933C', '#FFA500',
            '#e6550d', '#fd8d3c', '#fdae6b', '#fdd0a2', '#ad494a', '#d6616b', '#e7969c', '#ff7f0e', '#d62728', '#ff9896', '#EF6546','#E6933C', '#FFA500',
            '#e6550d', '#fd8d3c', '#fdae6b', '#fdd0a2', '#ad494a', '#d6616b', '#e7969c', '#ff7f0e', '#d62728', '#ff9896', '#EF6546','#E6933C', '#FFA500',
            '#e6550d', '#fd8d3c', '#fdae6b', '#fdd0a2', '#ad494a', '#d6616b', '#e7969c', '#ff7f0e', '#d62728', '#ff9896', '#EF6546','#E6933C', '#FFA500',
            '#e6550d', '#fd8d3c', '#fdae6b', '#fdd0a2', '#ad494a', '#d6616b', '#e7969c', '#ff7f0e', '#d62728', '#ff9896', '#EF6546','#E6933C', '#FFA500',
            '#e6550d', '#fd8d3c', '#fdae6b', '#fdd0a2', '#ad494a', '#d6616b', '#e7969c', '#ff7f0e', '#d62728', '#ff9896', '#EF6546','#E6933C', '#FFA500',
            '#e6550d', '#fd8d3c', '#fdae6b', '#fdd0a2', '#ad494a', '#d6616b', '#e7969c', '#ff7f0e', '#d62728', '#ff9896', '#EF6546','#E6933C', '#FFA500',
            '#e6550d', '#fd8d3c', '#fdae6b', '#fdd0a2', '#ad494a', '#d6616b', '#e7969c', '#ff7f0e', '#d62728', '#ff9896', '#EF6546','#E6933C', '#FFA500',
            '#e6550d', '#fd8d3c', '#fdae6b', '#fdd0a2', '#ad494a', '#d6616b', '#e7969c', '#ff7f0e', '#d62728', '#ff9896', '#EF6546','#E6933C', '#FFA500',
            '#e6550d', '#fd8d3c', '#fdae6b', '#fdd0a2', '#ad494a', '#d6616b', '#e7969c', '#ff7f0e', '#d62728', '#ff9896', '#EF6546','#E6933C', '#FFA500',
            '#e6550d', '#fd8d3c', '#fdae6b', '#fdd0a2', '#ad494a', '#d6616b', '#e7969c', '#ff7f0e', '#d62728', '#ff9896', '#EF6546','#E6933C', '#FFA500',
            '#e6550d', '#fd8d3c', '#fdae6b', '#fdd0a2', '#ad494a', '#d6616b', '#e7969c', '#ff7f0e', '#d62728', '#ff9896', '#EF6546','#E6933C', '#FFA500',
            '#e6550d', '#fd8d3c', '#fdae6b', '#fdd0a2', '#ad494a', '#d6616b', '#e7969c', '#ff7f0e', '#d62728', '#ff9896', '#EF6546','#E6933C', '#FFA500',
            '#e6550d', '#fd8d3c', '#fdae6b', '#fdd0a2', '#ad494a', '#d6616b', '#e7969c', '#ff7f0e', '#d62728', '#ff9896', '#EF6546','#E6933C', '#FFA500',
            '#e6550d', '#fd8d3c', '#fdae6b', '#fdd0a2', '#ad494a', '#d6616b', '#e7969c', '#ff7f0e', '#d62728', '#ff9896', '#EF6546','#E6933C', '#FFA500',
            '#e6550d', '#fd8d3c', '#fdae6b', '#fdd0a2', '#ad494a', '#d6616b', '#e7969c', '#ff7f0e', '#d62728', '#ff9896', '#EF6546','#E6933C', '#FFA500',
            '#e6550d', '#fd8d3c', '#fdae6b', '#fdd0a2', '#ad494a', '#d6616b', '#e7969c', '#ff7f0e', '#d62728', '#ff9896', '#EF6546','#E6933C', '#FFA500'],

        greenishColors = [
            '#31a354', '#74c476', '#a1d99b', '#c7e9c0', '#637939', '#8ca252', '#b5cf6b', '#cedb9c', '#2ca02c', '#98df8a', '#EDDA74','#00E100',
            '#31a354', '#74c476', '#a1d99b', '#c7e9c0', '#637939', '#8ca252', '#b5cf6b', '#cedb9c', '#2ca02c', '#98df8a', '#EDDA74','#00E100',
            '#31a354', '#74c476', '#a1d99b', '#c7e9c0', '#637939', '#8ca252', '#b5cf6b', '#cedb9c', '#2ca02c', '#98df8a', '#EDDA74','#00E100',
            '#31a354', '#74c476', '#a1d99b', '#c7e9c0', '#637939', '#8ca252', '#b5cf6b', '#cedb9c', '#2ca02c', '#98df8a', '#EDDA74','#00E100',
            '#31a354', '#74c476', '#a1d99b', '#c7e9c0', '#637939', '#8ca252', '#b5cf6b', '#cedb9c', '#2ca02c', '#98df8a', '#EDDA74','#00E100',
            '#31a354', '#74c476', '#a1d99b', '#c7e9c0', '#637939', '#8ca252', '#b5cf6b', '#cedb9c', '#2ca02c', '#98df8a', '#EDDA74','#00E100',
            '#31a354', '#74c476', '#a1d99b', '#c7e9c0', '#637939', '#8ca252', '#b5cf6b', '#cedb9c', '#2ca02c', '#98df8a', '#EDDA74','#00E100',
            '#31a354', '#74c476', '#a1d99b', '#c7e9c0', '#637939', '#8ca252', '#b5cf6b', '#cedb9c', '#2ca02c', '#98df8a', '#EDDA74','#00E100',
            '#31a354', '#74c476', '#a1d99b', '#c7e9c0', '#637939', '#8ca252', '#b5cf6b', '#cedb9c', '#2ca02c', '#98df8a', '#EDDA74','#00E100',
            '#31a354', '#74c476', '#a1d99b', '#c7e9c0', '#637939', '#8ca252', '#b5cf6b', '#cedb9c', '#2ca02c', '#98df8a', '#EDDA74','#00E100',
            '#31a354', '#74c476', '#a1d99b', '#c7e9c0', '#637939', '#8ca252', '#b5cf6b', '#cedb9c', '#2ca02c', '#98df8a', '#EDDA74','#00E100',
            '#31a354', '#74c476', '#a1d99b', '#c7e9c0', '#637939', '#8ca252', '#b5cf6b', '#cedb9c', '#2ca02c', '#98df8a', '#EDDA74','#00E100',
            '#31a354', '#74c476', '#a1d99b', '#c7e9c0', '#637939', '#8ca252', '#b5cf6b', '#cedb9c', '#2ca02c', '#98df8a', '#EDDA74','#00E100',
            '#31a354', '#74c476', '#a1d99b', '#c7e9c0', '#637939', '#8ca252', '#b5cf6b', '#cedb9c', '#2ca02c', '#98df8a', '#EDDA74','#00E100',
            '#31a354', '#74c476', '#a1d99b', '#c7e9c0', '#637939', '#8ca252', '#b5cf6b', '#cedb9c', '#2ca02c', '#98df8a', '#EDDA74','#00E100'],


        brownishColors = [
            '#8c6d31', '#bd9e39', '#e7ba52', '#e7cb94', '#bcbd22', '#dbdb8d', '#bcbd22', '#fdd0a2', '#F7E47E','#FFFFA6', '#CFBC56','#EDDA74',
            '#8c6d31', '#bd9e39', '#e7ba52', '#e7cb94', '#bcbd22', '#dbdb8d', '#bcbd22', '#fdd0a2', '#F7E47E','#FFFFA6', '#CFBC56','#EDDA74',
            '#8c6d31', '#bd9e39', '#e7ba52', '#e7cb94', '#bcbd22', '#dbdb8d', '#bcbd22', '#fdd0a2', '#F7E47E','#FFFFA6', '#CFBC56','#EDDA74',
            '#8c6d31', '#bd9e39', '#e7ba52', '#e7cb94', '#bcbd22', '#dbdb8d', '#bcbd22', '#fdd0a2', '#F7E47E','#FFFFA6', '#CFBC56','#EDDA74',
            '#8c6d31', '#bd9e39', '#e7ba52', '#e7cb94', '#bcbd22', '#dbdb8d', '#bcbd22', '#fdd0a2', '#F7E47E','#FFFFA6', '#CFBC56','#EDDA74',
            '#8c6d31', '#bd9e39', '#e7ba52', '#e7cb94', '#bcbd22', '#dbdb8d', '#bcbd22', '#fdd0a2', '#F7E47E','#FFFFA6', '#CFBC56','#EDDA74',
            '#8c6d31', '#bd9e39', '#e7ba52', '#e7cb94', '#bcbd22', '#dbdb8d', '#bcbd22', '#fdd0a2', '#F7E47E','#FFFFA6', '#CFBC56','#EDDA74',
            '#8c6d31', '#bd9e39', '#e7ba52', '#e7cb94', '#bcbd22', '#dbdb8d', '#bcbd22', '#fdd0a2', '#F7E47E','#FFFFA6', '#CFBC56','#EDDA74',
            '#8c6d31', '#bd9e39', '#e7ba52', '#e7cb94', '#bcbd22', '#dbdb8d', '#bcbd22', '#fdd0a2', '#F7E47E','#FFFFA6', '#CFBC56','#EDDA74',
            '#8c6d31', '#bd9e39', '#e7ba52', '#e7cb94', '#bcbd22', '#dbdb8d', '#bcbd22', '#fdd0a2', '#F7E47E','#FFFFA6', '#CFBC56','#EDDA74',
            '#8c6d31', '#bd9e39', '#e7ba52', '#e7cb94', '#bcbd22', '#dbdb8d', '#bcbd22', '#fdd0a2', '#F7E47E','#FFFFA6', '#CFBC56','#EDDA74',
            '#8c6d31', '#bd9e39', '#e7ba52', '#e7cb94', '#bcbd22', '#dbdb8d', '#bcbd22', '#fdd0a2', '#F7E47E','#FFFFA6', '#CFBC56','#EDDA74',
            '#8c6d31', '#bd9e39', '#e7ba52', '#e7cb94', '#bcbd22', '#dbdb8d', '#bcbd22', '#fdd0a2', '#F7E47E','#FFFFA6', '#CFBC56','#EDDA74',
            '#8c6d31', '#bd9e39', '#e7ba52', '#e7cb94', '#bcbd22', '#dbdb8d', '#bcbd22', '#fdd0a2', '#F7E47E','#FFFFA6', '#CFBC56','#EDDA74',
            '#8c6d31', '#bd9e39', '#e7ba52', '#e7cb94', '#bcbd22', '#dbdb8d', '#bcbd22', '#fdd0a2', '#F7E47E','#FFFFA6', '#CFBC56','#EDDA74'],



    // below are some names and text strings
        piename = ['a0', 'a1', 'a2', 'a3'], // internal names for the widgets
        textForExpandingButton = 'Drill down', // text on button to expand to full display
        textForContractingButton = 'click to contract', //text on button to contract unexpended widget

    //  This next set of variables are only for convenience.  They are derived strictly from those above,
    //   and they are consumed below in preference to those above.  The idea was to conceptually simplify
    //   some of the variables above and to those that describe either compressed or uncompressed widgets.
//               compressedPos = [
//                   {'x': margin.left + ((widgetWidth + widgetSpacing) * 0), 'y': 10},
//                   {'x': margin.left + ((widgetWidth + widgetSpacing) * 1), 'y': 10},
//                   {'x': margin.left + ((widgetWidth + widgetSpacing) * 2), 'y': 10},
//                   {'x': margin.left + ((widgetWidth + widgetSpacing) * 3), 'y': 10}
//               ],
//               expandedPos = [
//                   {'x': (widgetWidth * 0) + (quarterWidgetWidth * 1), 'y': 10},
//                   {'x': (widgetWidth * 1) + (quarterWidgetWidth * 2), 'y': 10},
//                   {'x': (widgetWidth * 2) + (quarterWidgetWidth * 3), 'y': 10}
//               ],
        compressedPos = [
            {'x': '0.5', 'y': 10},
            {'x': '25.5', 'y': 10},
            {'x': '50.5', 'y': 10},
            {'x': '75.5', 'y': 10}
        ],
        expandedPos = [
            {'x': '6', 'y': 10},
            {'x': '37', 'y': 10},
            {'x': '69', 'y': 10}
        ],

    //  dc vars
        maximumRowsInTable = 150,


    //-------widgetPosition------
    // JavaScript module. This portion of the code allows us to keep track of which widgets are expanded
    // and which remain in their original positions. There are a functions that allow you to ask the constructor
    // about its status ( examples:  isAnyWidgetExpanded() returns a Boolean to tell you if anything's expanded,
    // while expandedWidget () returns a number to tell you which widget has been expanded.
    //---------------------------
        widgetPosition = (function () {
            // private property
            var currentWidgetPosition = { 'up': [0, 1, 2, 3],
                    'down': [] },

                isAnyWidgetExpanded = function () {   // returns a Boolean
                    return currentWidgetPosition.down.length > 0;
                },

                expandedWidget = function () {   // returns a number
                    if (currentWidgetPosition.down.length == 1) {
                        return currentWidgetPosition.down[0];
                    } else {
                        return -1;
                    }
                },

                unexpandedWidgets = function () {   // returns an array
                    return currentWidgetPosition.up;
                },

            // the main action routine.
                expandThisWidget = function (widgetToBeExpanded) {  // number: 1 = success, 0 = failure
                    var indexOfDesiredWidget = 0;
                    // first make sure the incoming argument is inside the acceptable range
                    if ((widgetToBeExpanded < 0) || (widgetToBeExpanded > 3)) {
                        return -1;
                    }
                    // another way to go wrong is to try to expand a widget that isn't in the top row to begin with
                    indexOfDesiredWidget = currentWidgetPosition.up.indexOf(widgetToBeExpanded);
                    if (indexOfDesiredWidget == -1) {
                        return indexOfDesiredWidget;
                    }
                    // you can also go wrong if there is already a widget expanded
                    if (currentWidgetPosition.down.length != 0) {
                        indexOfDesiredWidget = -1;
                    }

                    if (indexOfDesiredWidget > -1) {
                        // everything looks good. Let's do what the caller has asked us to do.
                        //First copy the widget to the down position
                        currentWidgetPosition.down.push(currentWidgetPosition.up[indexOfDesiredWidget]);
                        // Now remove it from the top row and collapse those around it
                        currentWidgetPosition.up = currentWidgetPosition.up.slice(0, indexOfDesiredWidget).concat(
                            currentWidgetPosition.up.slice(indexOfDesiredWidget + 1, 4));
                    }
                    return indexOfDesiredWidget;
                },

            // the other action routine, though this one is much simpler since there's only one choice
                unexpandAllWidgets = function () {
                    currentWidgetPosition.up.push(currentWidgetPosition.down.pop());
                    currentWidgetPosition.up.sort(function (a, b) {
                        return a - b;
                    });
                    currentWidgetPosition.down = [];
                };
            // end var

            // now present the public API
            return {
                unexpandAllWidgets: unexpandAllWidgets,
                expandThisWidget: expandThisWidget,
                unexpandedWidgets: unexpandedWidgets,
                expandedWidget: expandedWidget,
                isAnyWidgetExpanded: isAnyWidgetExpanded
            };


        }()); // widgetPosition


    var displayManipulator = (function () {
        var rememberClickEffectors = [],
            disableAllPieClickEffectors = function () {
                for (var widgetCounter = 0; widgetCounter < totalWidgetNumber; widgetCounter++) {
                    rememberClickEffectors [widgetCounter] = d3.selectAll('#a' + widgetCounter + '-chart>svg>g>.pie-slice>path').on('click');
                    d3.selectAll('#a' + widgetCounter + '-chart>svg>g>.pie-slice>path').on('click', null);
                }
            },
            reenableAllPieClickEffectors = function () {
                if (rememberClickEffectors === undefined) {
                    alert(' JavaScript error--an attempt was made to reenableAllPieClickEffectors before originally disabling those ClickEffectors');
                } else {
                    for (var widgetCounter = 0; widgetCounter < totalWidgetNumber; widgetCounter++) {
                        d3.selectAll('#a' + widgetCounter + '-chart>svg>g>.pie-slice>path').on('click', rememberClickEffectors [widgetCounter]);
                    }
                }
            } ,
            eraseAnyOrphanedTooltips = function () {
                var orphanedTooltips = d3.selectAll('.toolTextAppearance');
                if (!(orphanedTooltips === undefined)) {
                    orphanedTooltips.style('opacity', 0);
                }
            },
            cleanUpAnyGraphicsWeAreDoneWith = function () {
                var potentialOrphanedGraphics = d3.select('div#sunburstdiv>svg');
                if ((!(potentialOrphanedGraphics === null)) &&
                    (!(potentialOrphanedGraphics[0] === null)) &&
                    (!(potentialOrphanedGraphics[0][0] === null))) {
                    potentialOrphanedGraphics.remove();
                }

                var potentialOrphanedLegend = d3.select('div#sunburstlegend');
                if ((!(potentialOrphanedLegend === null)) &&
                    (!(potentialOrphanedLegend[0] === null)) &&
                    (!(potentialOrphanedLegend[0][0] === null))) {
                    potentialOrphanedLegend.remove();
                }
            },
            createAResetButtonIfWeSwitchedARoot = function () {
                // Instead of looking to see which widget is closing let's just check them all, and create a reset button
                // whenever the root is not equal to the user-specified Global parent
                for (var i = 0; i < X; i++) {

                }
            },

        // convenience routine for adding a pie chart
            addPieChart = function (crossFilterVariable, id, key, colors, localPieChartWidth, localPieChartRadius, localInnerRadius) {
                var dimensionVariable = crossFilterVariable.dimension(function (d) {
                        return d[key];
                    }),
                    dimensionVariableGroup = dimensionVariable.group().reduceSum(function (d) {
                        return 1;
                    }),
                    displayDataGroup = function (d) {
                        var returnValue = d.data.key.toString() + " (" + d.data.value + ")";
                        returnValue = returnValue.replace("http://amigo.geneontology.org/cgi-bin/amigo/term_details?term=", "[GO]");
                        returnValue = returnValue.replace("http://www.ncbi.nlm.nih.gov/gquery/?term=", "[NCBI]");
                        return returnValue;
                    };

                var colorSet = {};
                switch(id){
                    case 'a0-chart':
                        colorSet=blueColors;
                        break;
                    case 'a1-chart':
                        colorSet=greenishColors ;
                        break;
                    case 'a2-chart':
                        colorSet=reddishColors ;
                        break;
                    case 'a3-chart':
                        colorSet=brownishColors ;
                        break;
                    default:;
                }

                return dc.pieChart("#" + id)
                    .width(localPieChartWidth)
                    .height(localPieChartWidth)
                    .transitionDuration(200)
                    .radius(localPieChartRadius)
                    .innerRadius(localInnerRadius)
                    .dimension(dimensionVariable)
                    .group(dimensionVariableGroup)
                    .colors(colorSet)
                    .label(displayDataGroup)
                    .renderTitle(true);
            },


            addDcTable = function (crossFilterVariable, id, key) {
                var dimensionVariable = crossFilterVariable.dimension(function (d) {
                        return d[key];
                    }),
                    dimensionVariableGroup = function (d) {
                        return "";
                    },
                    dimensionVariableGroupTotal = function (d) {
                        return dimensionVariable.groupAll();
                    };

                var theTable = dc.dataTable("#" + id)
                    .dimension(dimensionVariable)
                    .group(dimensionVariableGroup)
                    .size(maximumRowsInTable)
                    .columns([
                        function (d) {
                            return d.GO_biological_process_term;
                        },
                        function (d) {
                            return d.assay_format;
                        },
                        function (d) {
                            return d.protein_target;
                        },
                        function (d) {
                            return d.assay_type;
                        },
                        function (d) {
                            return "<a href='/bardwebclient/bardWebInterface/showAssay/" + d.assayBId + "'>" + d.assayId + "</a>";
                        }
                    ])
                    .order(d3.ascending)
                    .sortBy(function (d) {
                        return d.GO_biological_process_term;
                    });
                //                        This datecount  function below doesn't seem to work for me. What's wrong?
                //                        dc.dataCount("#data-count")
                //                                .dimension(dimensionVariable)
                //                                .group(dimensionVariableGroupTotal);

                return  theTable;
            },


            expandDataAreaForAllPieCharts = function (pieChartHolderElement) {
                pieChartHolderElement.attr('height', displayWidgetY);
            },


            moveDataTableOutOfTheWay = function (dataTable) {
                dataTable
                    .transition()
                    .duration(500)
                    .style("top", 50 + displayWidgetY + displayWidgetHeight + "px");  // Extra spaces for 'click to contract' button
            },

            moveDataTableBackToItsOriginalPosition = function (dataTable) {
                dataTable
                    .transition()
                    .delay(1000)
                    .duration(500)
                    .style("top", "300px");  // Extra spaces for 'click to contract' button
            },


            shiftBackgroundWidgets = function (domDescription, horizontalPosition) {
                domDescription
                    .transition()
                    .duration(1000)
                    .style("left", horizontalPosition + "%");
            },


        // buttons are supplied to allow the user to reset their hierarchy drill down, but the buttons only work
        // from the pie mode
            deactivateDrillDownResetButtons = function () {

                // turn off the reset buttons, and they are deactivated when the Sunburst is visible
                var drillDownButtons = d3.selectAll('.drill');
                if (!(drillDownButtons.empty())) {
                    drillDownButtons.style('pointer-events', 'none')
                        .style('opacity', 0.5)
                        .style('cursor', 'pointer');
                }

                // If there is a drill down explanation then fade it  out
                var drillLabelExplanation = d3.select('.drillLabel');
                if (!(drillLabelExplanation.empty())) {
                    drillLabelExplanation.style('opacity', 0.5);
                }

            },

        // buttons are supplied to allow the user to reset their hierarchy drill down. These were deactivated when a sunburst
        //   was displayed, but they need to be reactivated again when we get back to pie mode
            activateDrillDownResetButtons = function () {

                // do we have any drill down reset buttons to deal with
                var drillDownResetButtonsExist = false;
                var drillDownButtons = d3.selectAll('.drill');
                if (!(drillDownButtons.empty())) {

                    drillDownResetButtonsExist = true;
                    drillDownButtons.style('pointer-events', 'auto')
                        .style('opacity', 1)
                        .style('cursor', 'pointer');

                }

                var drillLabelExplanation = d3.select('.drillLabel');
                if (!(drillLabelExplanation.empty())) {
                    // we have an explanation line

                    if (drillDownResetButtonsExist) {   // if buttons exist
                        drillLabelExplanation.style('opacity', 1);
                        ;
                    } else {    // if no buttons exist
                        // this should never happen ( an explanation exists without any buttons ), but
                        // but if it should happen somehow then let's clean it up
                        drillLabelExplanation.remove();
                    }

                }


            },


            spotlightOneAndBackgroundThree = function (index, spotlight, backgroundIndex1, backgroundIndex2, backgroundIndex3, origButton, expandedPos) {

                deactivateDrillDownResetButtons();


                var widgetsGoHere = sharedStructures.widgetsGoHere();
                // first handle the spotlight element and then the three backup singers
                // movedown
                spotlight
                    .transition()
                    .duration(200)
                    .style("top", widgetsGoHere[index].display.coords.y + "px");

                // moveover
                spotlight
                    .transition()
                    .delay(200)
                    .duration(200)
                    //    .style("left", d.display.coords.x + "%");
                    .style("left", "10px");

                spotlight
                    .transition()
                    .delay(400)
                    .duration(200)
                    .style('height', widgetsGoHere[index].display.size.height + "px")
                    .style('max-width', widgetsGoHere[index].display.size.width + "px")
                    .style('width', '100%');
                shiftBackgroundWidgets(d3.select('#a' +backgroundIndex1), expandedPos[0].x);
                shiftBackgroundWidgets(d3.select('#a' +backgroundIndex2), expandedPos[1].x);
                shiftBackgroundWidgets(d3.select('#a' +backgroundIndex3), expandedPos[2].x);

                //   Turn off the text label based on click event for background widgets
                d3.select('#a' +backgroundIndex1).selectAll('.pieChart>svg>g>.pie-slice').style('pointer-events', 'none');
                d3.select('#a' +backgroundIndex2).selectAll('.pieChart>svg>g>.pie-slice').style('pointer-events', 'none');
                d3.select('#a' +backgroundIndex3).selectAll('.pieChart>svg>g>.pie-slice').style('pointer-events', 'none');

                //  Turn off the expander button, since the user needs to contract the expanded
                //  widget before they try to expand the new one. It would be nice to click that
                //  button for them, but D three does not support that sort of activation is you are
                //  using bound data. I should probably connect to those data dynamically to get around
                //  this problem.
                d3.select('#a' +backgroundIndex1).selectAll('.expandButton').style('pointer-events', 'none').style('opacity', 0.5);
                d3.select('#a' +backgroundIndex2).selectAll('.expandButton').style('pointer-events', 'none').style('opacity', 0.5);
                d3.select('#a' +backgroundIndex3).selectAll('.expandButton').style('pointer-events', 'none').style('opacity', 0.5);

                d3.select('#sunburstContractor')
                    .style('opacity', 1);
//                       if (!contractingButtonDiv.empty ()) {
//                           contractingButtonDiv.style('opacity', 1);
//                       } else {
//                           contractingButtonDiv.append('div')
//                                   .attr('class', 'contractButton')
//                                   .attr('id', 'hierarchyContractor')
//                                   .transition()
//                                   .delay(1000)
//                                   .duration(500)
//                                   .style('opacity', 1);
//                       }
//                       origButton
//                               .text(textForContractingButton)
//                               .attr('class', 'contractButton')
//                               .transition()
//                               .delay(1000)
//                               .duration(500)
//                               .style('opacity', 1);
            },

            resetOneAndResettleThree = function (index, spotlight, backgroundIndex1, backgroundIndex2, backgroundIndex3, origButton, expandedPos) {

                var widgetsGoHere = sharedStructures.widgetsGoHere();

                //shrink
                spotlight.transition()
                    .duration(400)
                    .style('height', widgetsGoHere[index].orig.size.height + "px")
                    .style('width', widgetsGoHere[index].orig.size.width + "%")
                    .style('padding-left', '5px');

                //move back to your original X position
                spotlight.transition()
                    .delay(400)
                    .duration(200)
                    .style("left", widgetsGoHere[index].orig.coords.x + "%");

                //move back to your original Y position
                spotlight.transition()
                    .delay(600)
                    .duration(400)
                    .style("top", widgetsGoHere[index].orig.coords.y + "px");


                shiftBackgroundWidgets(d3.select('#a'+backgroundIndex1), widgetsGoHere[backgroundIndex1].orig.coords.x);
                shiftBackgroundWidgets(d3.select('#a'+backgroundIndex2), widgetsGoHere[backgroundIndex2].orig.coords.x);
                shiftBackgroundWidgets(d3.select('#a'+backgroundIndex3), widgetsGoHere[backgroundIndex3].orig.coords.x);
                d3.select('#a'+backgroundIndex1).selectAll('.pieChart>svg>g>.pie-slice').style('pointer-events', 'auto');
                d3.select('#a'+backgroundIndex2).selectAll('.pieChart>svg>g>.pie-slice').style('pointer-events', 'auto');
                d3.select('#a'+backgroundIndex3).selectAll('.pieChart>svg>g>.pie-slice').style('pointer-events', 'auto');
                //  Turn back on the expander buttons
                //  widget before they try to expand the new one. It would be nice to click that
                //  button for them, but D three does not support that sort of activation is you are
                //  using bound data. I should probably connect to those data dynamically to get around
                //  this problem.
                if (!(linkedVizData.extendedHierarchyDataExists(backgroundIndex1))) {
                    d3.select('#expbutton' + backgroundIndex1).style('pointer-events', 'none').style('opacity', 0.5);
                } else {
                    d3.select('#expbutton' + backgroundIndex1).style('pointer-events', 'auto').style('opacity', 1);
                }
                if (!(linkedVizData.extendedHierarchyDataExists(backgroundIndex2))) {
                    d3.select('#expbutton' + backgroundIndex2).style('pointer-events', 'none').style('opacity', 0.5);
                } else {
                    d3.select('#expbutton' + backgroundIndex2).style('pointer-events', 'auto').style('opacity', 1);
                }
                if (!(linkedVizData.extendedHierarchyDataExists(backgroundIndex3))) {
                    d3.select('#expbutton' + backgroundIndex3).style('pointer-events', 'none').style('opacity', 0.5);
                } else {
                    d3.select('#expbutton' + backgroundIndex3).style('pointer-events', 'auto').style('opacity', 1);
                }


                var x = origButton
                    .text(textForExpandingButton)
                    .attr('class', 'expandButton')
                    .transition()
                    .delay(1000)
                    .duration(500)
                    .style('opacity', 1);

                // turn back on this reset buttons
                activateDrillDownResetButtons();

                // Set some style attributes that must be adjusted dynamically
                tidyUpDisplay();
            },

            expandGraphicsArea = function (graphicsTarget, graphicsTitle) {

                var bigarc = d3.svg.arc()
                    .innerRadius(innerRadiusWhenExpanded)
                    .outerRadius(bigPie);

                var nullarc = d3.svg.arc()
                    .innerRadius(innerRadiusWhenExpanded)
                    .outerRadius(innerRadiusWhenExpanded + 1);

                graphicsTarget
                    .attr('width', displayWidgetWidth)
                    .attr('height', displayWidgetHeight + 50); // Extra room for the 'click to contract' button

                graphicsTarget
                    .select('g')
                    .selectAll('text')
                    .remove();

                graphicsTarget
                    .selectAll('g')
                    .select('path')
                    .transition()
                    .duration(1500)
                    .attr("d", bigarc)  // pie size easily adjusted here
                    .attr("transform", "translate(368,375)");    // We need use explicit numbers here, not variables. This would be something to fix

                graphicsTitle.attr('class', 'expandedGraphTitle');
            },

            contractGraphicsArea = function (graphicsTarget, graphicsTitle) {

                var arc = d3.svg.arc()
                    .innerRadius(innerRadius)
                    .outerRadius(pieChartRadius);

                graphicsTarget
                    .transition()
                    .duration(1500)
                    .attr('width', pieChartWidth)
                    .attr('height', pieChartWidth);

                graphicsTarget
                    .select('g')
                    .selectAll('text')
                    .remove();

                graphicsTarget
                    .selectAll('g')
                    .select('path')
                    .transition()
                    .duration(500)
                    .attr("d", arc)
                    .attr("transform", "translate(0,0)");

                graphicsTitle.attr('class', 'graphTitle');

            },
            removeTheSun = function () {
                d3.selectAll('#suburst_container').style('pointer-events', 'none')
                    .transition()
                    .delay(0)
                    .duration(500)
                    .style('opacity', '0')
            },

        // We have to make a few changes to the display dynamically since we don't have complete control over the DC code
            tidyUpDisplay = function () {
                dc.renderAll();
            },


            swapAPieForTheSun = function (pieDiv, sunburstContainer, expandedButtonNum, callbackToExpandOrContractOnButtonClick) {
                pieDiv.style('pointer-events', 'none')
                    .transition()
                    .delay(1000)
                    .duration(500)
                    .style('opacity', '0');
                sunburstContainer.style('pointer-events', null)
                    .transition()
                    .delay(1000)
                    .duration(500)
                    .style('opacity', '1');
                if (linkedVizData.retrieveCurrentHierarchicalData(expandedButtonNum).children !== undefined) {
                    createASunburst(1000, 1000, 5, 1000, continuousColorScale, 'div#sunburstdiv', linkedVizData.cid(), expandedButtonNum);
                    var minMaxHolder = linkedVizData.findMinimumAndMaximumRatiosForColoring(linkedVizData.filteredHierarchyData(expandedButtonNum));
                    createALegend(120, 200, 100, continuousColorScale, 'div#legendGoesHere', minMaxHolder.minimum, minMaxHolder.maximum);
//                    createALegend(120, 200, 100, continuousColorScale, 'div#legendGoesHere', 0, 1);
                    d3.selectAll('#suburst_container').style('pointer-events', null);
                } else {
                    d3.select('div#sunburstdiv')
                        .append('div')
                        .attr("width", 1000)
                        .attr("height", 1000)
                        .style("padding-top", '200px')
                        .style("text-align", 'center')
                        .append("h1")
                        .html("No off-embargo assay data are  available for this compound." +
                            "Please either choose a different compound, or else come" +
                            " back later when more assay data may have accumulated.");
                }
                d3.select('#sunburstContractor')
                    .on('click', function (d) {
                        sunburstContainer.style('pointer-events', 'none')
                            .style('opacity', '0');
                        pieDiv.style('pointer-events', null)
                            .style('opacity', '1');
                        d3.selectAll('.molstruct')
                            .style('opacity', '0');
                        callbackToExpandOrContractOnButtonClick({    index: expandedButtonNum }, expandedButtonNum);
                    });
                var molecularStructure = d3.selectAll('.molstruct')
                    .transition()
                    .delay(1000)
                    .duration(500)
                    .style('opacity', '1');

            };

        // end var

        // Public API for this module
        return {
            disableAllPieClickEffectors: disableAllPieClickEffectors,
            reenableAllPieClickEffectors: reenableAllPieClickEffectors,
            eraseAnyOrphanedTooltips: eraseAnyOrphanedTooltips,
            cleanUpAnyGraphicsWeAreDoneWith: cleanUpAnyGraphicsWeAreDoneWith,
            contractGraphicsArea: contractGraphicsArea,
            expandGraphicsArea: expandGraphicsArea,
            resetOneAndResettleThree: resetOneAndResettleThree,
            spotlightOneAndBackgroundThree: spotlightOneAndBackgroundThree,
            expandDataAreaForAllPieCharts: expandDataAreaForAllPieCharts,
            moveDataTableOutOfTheWay: moveDataTableOutOfTheWay,
            moveDataTableBackToItsOriginalPosition: moveDataTableBackToItsOriginalPosition,
            addDcTable: addDcTable,
            addPieChart: addPieChart,
            swapAPieForTheSun: swapAPieForTheSun,
            tidyUpDisplay: tidyUpDisplay
        };
    }() );  //displayManipulator

    //
    //   Get the data and make the plots using dc.js.  Use this as an opportunity to encapsulate any methods that are
    //    used strictly locally
    //
    var generateLinkedPies = (function () {


        var buttondata = [
            { index: 0 },
            { index: 1 },
            { index: 2 },
            { index: 3 }]  ;


        // Private method used to pull the data in from the remote site
        var readInData = function () {

                return    linkedVizData.retrieveLinkedData();

            },

            /***
             *  This method has nothing to do with the Sunburst itself. Instead we are simply  removing
             *  the 'we are still loading' spinner so that users might see the display.
             *               *
             */
         removeTheLoadingSpinner = function (spinnerId) {
            var onScreenSpinnerElement = d3.select(spinnerId);
            if (!(onScreenSpinnerElement.empty ()))   {
                onScreenSpinnerElement.style ('opacity', 0) ;
            }
        },


        // Our main button handler callback
            handleExpandOrContractClick = function (d, x) {
                // we better decide whether where you want to expand or contract
                var origButton = d3.select('#expbutton' + d.index)
                        .style('opacity', 0),
                    expandedWidget,
                    unexpandedWidget,
                    expandContractButton;

                if (!widgetPosition.isAnyWidgetExpanded()) {
                    displayManipulator.disableAllPieClickEffectors();
                    displayManipulator.expandDataAreaForAllPieCharts(d3.select('.pieCharts'));
                    displayManipulator.moveDataTableOutOfTheWay(d3.select('#data-table'));
                    widgetPosition.expandThisWidget(d.index);
                    expandedWidget = widgetPosition.expandedWidget();
                    unexpandedWidget = widgetPosition.unexpandedWidgets();
                    displayManipulator.spotlightOneAndBackgroundThree(d.index, d3.select('#a' + expandedWidget),
                        unexpandedWidget[0],
                        unexpandedWidget[1],
                        unexpandedWidget[2],
                        origButton,
                        expandedPos);
                    expandContractButton = d3.select('#a' + expandedWidget + '-chart>.graphTitle')
                    displayManipulator.expandGraphicsArea(d3.select('#a' + expandedWidget).select('.pieChart>svg'),
                        expandContractButton);
                    if (linkedVizData.extendedHierarchyDataExists(expandedWidget)) {
                        displayManipulator.swapAPieForTheSun(d3.select('#a' + expandedWidget), d3.selectAll('#suburst_container'), expandedWidget, handleExpandOrContractClick);
                    }

                }

                else if (widgetPosition.expandedWidget() == d.index) {
                    expandedWidget = widgetPosition.expandedWidget();
                    unexpandedWidget = widgetPosition.unexpandedWidgets();
                    displayManipulator.contractGraphicsArea(d3.select('#a' + x).select('.pieChart>svg'),
                        d3.select('#a' + expandedWidget + '-chart>.expandedGraphTitle'));
                    displayManipulator.resetOneAndResettleThree(d.index, d3.select('#a' + expandedWidget),
                        unexpandedWidget[0],
                        unexpandedWidget[1],
                        unexpandedWidget[2],
                        origButton,
                        expandedPos);
                    widgetPosition.unexpandAllWidgets();
                    displayManipulator.moveDataTableBackToItsOriginalPosition(d3.select('#data-table'));
                    displayManipulator.reenableAllPieClickEffectors();
                    displayManipulator.eraseAnyOrphanedTooltips();
                    displayManipulator.cleanUpAnyGraphicsWeAreDoneWith();
                }

            },

            attachButtonsToThePieContainers = function (classOfPieContainers, callbackToExpandOrContractOnButtonClick, buttondata, sunburstContainer) {
                var placeButtonsHere = d3.selectAll(classOfPieContainers)
                    .data(buttondata);

                placeButtonsHere.append("div")
                    .text(textForExpandingButton)
                    .attr('class', 'expandButton')
                    .attr('id', function (d) {
                        return 'expbutton' + d.index;
                    })
                    .on('click', callbackToExpandOrContractOnButtonClick);

                // deactivate button if we have no data
                for (var i = 0; i < 4; i++) {
                    if (!(linkedVizData.extendedHierarchyDataExists(i))) {
                        var expandedButton = d3.select('#expbutton' + i);
                        if (!(expandedButton.empty())) {
                            expandedButton.style('pointer-events', 'none').style('opacity', 0.5);
                        }

                    } else {
                        var expandedButton = d3.select('#expbutton' + i);
                        if (!(expandedButton.empty())) {
                            expandedButton.style('pointer-events', 'auto').style('opacity', 1);
                        }

                    }
                }

                sunburstContainer.select('#sunburstContractor')
                    .text(textForContractingButton)
                    .style('opacity','0');

            },

            verifyLinkedData = function (cid) {
                console.log('beginning Linked Hierarchy with cid = '+cid+'.');
                d3.json("/bardwebclient/bardWebInterface/linkedData/"+cid, function (incomingData) {
                    // create an empty list, Just in case we get null data
                    linkedVizData.parseData(incomingData);
                    if (!linkedVizData.validateLinkedData()) {
                        console.log(' we have trouble with incoming linked data');
                        throw new Exception('bad data');
                    } else {
                        linkedVizData.cid(cid);
                        linkedVizData.appendConditionalStatusFields();
                    }
                    presentLinkedData();
                    linkedVizData.cleanupOriginalHierarchyData(2);
                    removeTheLoadingSpinner ( '#still_loading' );

                });// d3.json

            },


            presentLinkedData = function () {
                // create an empty list, Just in case we get null data
                var assays = [];

                // Clean up the data.  De-dup, and assign
                assays = readInData();

                // Create the crossfilter for the relevant dimensions and groups.
                sharedStructures.setAssay( crossfilter(assays) );

                // Build everything were going to display
                sharedStructures.setAllDataDcTable(displayManipulator.addDcTable(assay, 'data-table', 'assayId'));
                sharedStructures.setBiologicalProcessPieChart (displayManipulator.addPieChart(assay, 'a0-chart', 'GO_biological_process_term', colors, pieChartWidth, pieChartRadius, innerRadius));
                sharedStructures.setAssayFormatPieChart (displayManipulator.addPieChart(assay, 'a1-chart', 'assay_format', colors, pieChartWidth, pieChartRadius, innerRadius));
                sharedStructures.setAssayIdDimensionPieChart (displayManipulator.addPieChart(assay, 'a2-chart', 'protein_target', colors, pieChartWidth, pieChartRadius, innerRadius));
                sharedStructures.setAssayTypePieChart (displayManipulator.addPieChart(assay, 'a3-chart', 'assay_type', colors, pieChartWidth, pieChartRadius, innerRadius));

                sharedStructures.setAssayIndex( assay.dimension(function (d) {
                    return d['index'];
                }));

                // We should be ready, display it
                dc.renderAll();

                // Finally, attach some data along with buttons and callbacks to the pie charts we've built
                attachButtonsToThePieContainers('.columnsAssociatedWithPies', handleExpandOrContractClick, buttondata, d3.selectAll('#suburst_container'));


            };

        return {
            verifyLinkedData: verifyLinkedData,
            presentLinkedData: presentLinkedData
        }
    }()); // generateLinkedPies


    // **********************************************************
    // The highest level call.  Everything starts from here.
    // **********************************************************
    var buildLinkedHierarchiesVisualization = function (cid) {
        generateLinkedPies.verifyLinkedData(cid);  //
    }


    /***
     * We only want to return one thing from this top-level module to simplify the calling
     */
    return buildLinkedHierarchiesVisualization;

}());  // linkedVisualizationModule
