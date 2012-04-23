{
    "xdsVersion": "2.0.0",
    "frameworkVersion": "ext40",
    "internals": {
        "type": "treepanel",
        "reference": {
            "name": "items",
            "type": "array"
        },
        "codeClass": null,
        "userConfig": {
            "designer|initialView": true,
            "height": 616,
            "width": 543,
            "title": "My Tree Panel",
            "store": "elementTreeStore",
            "designer|userClassName": "elementTreePanel"
        },
        "customConfigs": [],
        "expanded": true,
        "cn": [
            {
                "type": "treeview",
                "reference": {
                    "name": "viewConfig",
                    "type": "object"
                },
                "codeClass": null,
                "userConfig": {
                    "designer|userClassName": "MyTreeView"
                },
                "customConfigs": [],
                "expanded": true
            }
        ]
    },
    "linkedNodes": {},
    "boundStores": {
        "elementTreeStore": {
            "type": "jsonptreestore",
            "reference": {
                "name": "items",
                "type": "array"
            },
            "codeClass": null,
            "userConfig": {
                "autoLoad": true,
                "storeId": "elementTreeStore",
                "model": "elementTreeModel",
                "root": "{text: 'RDM', id: '0', expanded: true}",
                "designer|userClassName": "elementTreeStore"
            },
            "customConfigs": [],
            "expanded": true,
            "cn": [
                {
                    "type": "ajaxproxy",
                    "reference": {
                        "name": "proxy",
                        "type": "object"
                    },
                    "codeClass": null,
                    "userConfig": {
                        "url": "element/list",
                        "designer|userClassName": "MyAjaxProxy"
                    },
                    "customConfigs": [],
                    "expanded": true,
                    "cn": [
                        {
                            "type": "jsonreader",
                            "reference": {
                                "name": "reader",
                                "type": "object"
                            },
                            "codeClass": null,
                            "userConfig": {
                                "designer|userClassName": "MyJsonReader"
                            },
                            "customConfigs": [],
                            "expanded": true
                        }
                    ]
                }
            ]
        }
    }
}