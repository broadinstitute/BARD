var linkedVizData = (function (){
    var validator = {

        // all available checks
        types: {},

        // error messages in the current
        // validation session
        messages: [],

        // current validation config
        // name: validation type
        config: {},

        // the interface method
        // `data` is key => value pairs
        validate: function (data) {

            var i, msg, type, checker, result_ok;

            // reset all messages
            this.messages = [];

            for (i in data) {

                if (data.hasOwnProperty(i)) {

                    type = this.config[i];
                    checker = this.types[type];

                    if (!type) {
                        continue; // no need to validate
                    }
                    if (!checker) { // uh-oh
                        throw {
                            name: "ValidationError",
                            message: "No handler to validate type " + type
                        };
                    }

                    result_ok = checker.validate(data[i]);
                    if (!result_ok) {
                        msg = "Invalid value for *" + i + "*, " + checker.instructions;
                        this.messages.push(msg);
                    }
                }
            }
            return this.hasErrors();
        },

        // helper
        hasErrors: function () {
            return this.messages.length !== 0;
        }
    };



    validator.types.isNonEmpty = {
        validate: function (value) {
            return !!value;
        },
        instructions: "the value cannot be empty"
    };

    validator.types.isNumber = {
        validate: function (value) {
            return !isNaN(Number(value));
        },
        instructions: "the value can only be a valid number, e.g. 1, 3.14 or 2010"
    };

    validator.types.isAlphaNum = {
        validate: function (value) {
            return !String(value).replace(/[a-z0-9]/ig, "").length;
        },
        instructions: "the value can only contain characters and numbers, no special symbols"
    };
    // make sure that every element inside the category group passes some basic tests
    validator.types.categoryCheck = {
        validate: function (value) {
            var returnVal = true;
            if (value.length!=4) {
                returnVal = false;
            }
            if (returnVal){
                for (var loopCount = 0;loopCount < value.length ; loopCount++  ) {
                    if (returnVal) { returnVal =  !isNaN(Number(value[loopCount].CatIdx));  }
                    if (returnVal) { returnVal =  !String(value[loopCount].CatName).replace(/[a-z0-9\s]/ig, "").length;  }
                    if (returnVal) { returnVal =  !String(value[loopCount].CatDescr).replace(/[a-z0-9\s]/ig, "").length;  }
                    if (returnVal) { returnVal =  !String(value[loopCount].CatIdentity).replace(/[a-z0-9_]/ig, "").length;  }
                }
            }
            return returnVal;
        },
        instructions: "failed core category check"
    };
    // make sure that every element inside the hierarchy group passes some basic tests
    validator.types.hierarchyCheck = {
        validate: function (value) {
            var returnVal = true;
            if (value.length!=4) {
                returnVal = false;
            }
            if (returnVal){
                for (var loopCount = 0;loopCount < value.length ; loopCount++  ) {
                    if (returnVal) { returnVal =  !isNaN(Number(value[loopCount].CatRef));  }
                    if (returnVal) { returnVal =  ((value[loopCount].HierType==='Graph') ||
                        (value[loopCount].HierType==='Tree'));  }
                }
            }
            return returnVal;
        },
        instructions: "failed core hierarchy check"
    };
    // make sure that every element inside the hierarchy group passes some basic tests
    validator.types.assayCheck = {
        validate: function (value) {
            var returnVal = true;

            if (returnVal){
                for (var loopCount = 0;loopCount < value.length ; loopCount++  ) {
                    if (returnVal) { returnVal =  !isNaN(Number(value[loopCount].AssayIdx));  }
                    if (returnVal) {
                        var  currentAssayIdx = Number(value[loopCount].AssayIdx);
                        returnVal = (assayIdList.indexOf(currentAssayIdx)<0);
                        if (!returnVal) {
                            additionalErrorInfo += ('repeated assay IDX='+currentAssayIdx);
                        } else {
                            assayIdList.push(currentAssayIdx);
                        }
                    }
                    if (returnVal) { returnVal =  !String(value[loopCount].AssayName).replace(/[a-z0-9\s\'\(\)\/_:;\-\[\]\,\+\.]/ig, "").length;
                        if (!returnVal) {additionalErrorInfo += ('undesirable character='+String(value[loopCount].AssayName).replace(/[a-z0-9\s\'\(\)\/_:;\-\[\]\,\+\.]/ig, ""));}}
                    if (!returnVal) alert(value[loopCount].AssayName);
                    if (returnVal) { returnVal =  !isNaN(Number(value[loopCount].AssayId));  }
                    if (!returnVal)  {
                        break;
                    }

                }

            }
            return returnVal;
        },
        instructions: "failed core assay check"
    };
    // make sure that every element inside the hierarchy group passes some basic tests
    validator.types.assayCrossCheck = {
        validate: function (value) {
            var returnVal = true;
            if (returnVal){
                for (var loopCount = 0;loopCount < value.length ; loopCount++  ) {
                    if (returnVal) {
                        var assayReferenceNumber = Number(value[loopCount].AssayRef);
                        returnVal =  !isNaN(assayReferenceNumber);
                        if (assayIdList.indexOf(assayReferenceNumber) < 0) {
                            returnVal = false;
                        }
                    }
                    if (!returnVal)  {
                        break;
                    }

                }

            }
            return returnVal;
        },
        instructions: "failed core assay check"
    };




    validator.config = {
        Category: 'categoryCheck',
        Hierarchy: 'hierarchyCheck',
        Assays: 'assayCheck',
        AssayCross: 'assayCrossCheck'
    };

    var linkedData = {},

        additionalErrorInfo = "",

        assayIdList = [],

        parseData = function (incomingData)  {
            linkedData =  incomingData;
        },
        numberOfWidgets = function ()  {
            return linkedData.Category.length;
        },
        validateLinkedData = function ()  {
            var returnVal = true;
            validator.validate(linkedData);
            if (validator.hasErrors()) {
                returnVal = false;
                var errorMessageReport =  validator.messages.join("\n");
                console.log(errorMessageReport);
                alert (errorMessageReport) ;
            }
            return returnVal;
        },

        appendConditionalStatusFields = function ()  {
            var returnVal = true;
            // Make a way to keep track of which elements of been selected as part of the drill down, sunburst visualization
            for (var loopCount = 0;loopCount < linkedData.Hierarchy.length ; loopCount++  ) {
                var hierarchyPointer =  linkedData.Hierarchy[loopCount];
                if (hierarchyPointer.HierType === 'Tree') {
                    hierarchyPointer.Structure['rootName']='/';
                    if ( (!(hierarchyPointer.Structure===undefined))&&
                        (!(hierarchyPointer.Structure.struct===undefined))&&
                        (hierarchyPointer.Structure.struct.length>0))
                        addAMembershipIndicator(hierarchyPointer.Structure.struct[0],loopCount);
                } else if (hierarchyPointer.HierType === 'Graph') {
                    hierarchyPointer.Structure['rootName']='/';
                }
            }
            // Make a way to keep track of which elements have been selected through the cross-linking, pie-based selection mechanism
            for (var loopCount = 0;loopCount < linkedData.AssayCross.length ; loopCount++  ) {
                var AssayCrossPointer =  linkedData.AssayCross[loopCount];
                AssayCrossPointer ["AssaySelected"]  = 1;
            }
            return returnVal;
        },
        findAssayId = function (assayRef)  {
            // new way, which may be more robust
            var listOfAllAssayIdxs = [];
            for (var i=0 ; i<linkedData.Assays.length ; i++) {
                listOfAllAssayIdxs.push (linkedData.Assays[i].AssayIdx);
            }
            var indexOfMatch = listOfAllAssayIdxs.indexOf(assayRef);
            if  (!(indexOfMatch === -1)) {
                return linkedData.Assays[indexOfMatch].AssayId;
            }  else {
                return -1;
            }
            //old way, which seemed to basically work
            //return linkedData.Assays[assayRef].AssayId;
        },
        retrieveLinkedData = function ()  {
            var developingAssayList = [];
            var weHaveDataToDisplay = false;
            if (!(linkedData.AssayCross ===null)) {
                var totalNumberOfAssayCrosses = linkedData.AssayCross.length;
                for (var i = 0; i < totalNumberOfAssayCrosses; i++){
                    if (linkedData.AssayCross[i] != null){
                        weHaveDataToDisplay = true;
                        break;
                    }
                }
                if (weHaveDataToDisplay)  {
                    for (var i = 0; i < totalNumberOfAssayCrosses; i++){
                        var assayCross = linkedData.AssayCross[i];
                        if (assayCross != null){
                            developingAssayList.push({
                                index: assayCross.AssayRef,
                                assayId: findAssayId(assayCross.AssayRef),
                                assayBId: assayCross.AssayBId,
                                GO_biological_process_term: assayCross.data[0],
                                assay_format: assayCross.data[1],
                                assay_type: assayCross.data[2],
                                protein_target: assayCross.data[3]
                            });
                        }
                    }
                }
            }
            return  developingAssayList;
        },


        retrieveCurrentHierarchicalData = function (datatypeIndex)  {
            var returnValue = {}; // this will be the current node in the tree
            var currentRootName  =  linkedData.Hierarchy[datatypeIndex].Structure.rootName;
            var currentRootNode  =  linkedData.Hierarchy[datatypeIndex].Structure.struct;
            returnValue =  findNodeInTreeByName (currentRootNode[0],currentRootName);
            if (returnValue=== undefined)  {
                alert(' problem: could not find node named '+ currentRootName+'.')
            } else {
                return returnValue;
            }
        },

    // Recursive descent: We do this one at the beginning to add both a membership
    //  indicator ( which gets set on an off pending on what the user clicks ) and
    //  a tree identifier ( since we have multiple sunburst trees and we need to be
    //  able to tell them apart in the click handler )
        addAMembershipIndicator  = function (currentNode, treeIdentifier)  {
            if (!(currentNode.children === undefined)) {
                for (var i = 0; i < currentNode.children.length; i++) {
                    addAMembershipIndicator(currentNode.children[i],treeIdentifier);
                    currentNode["member"] = 1;
                    currentNode["treeid"] = treeIdentifier;
                }
            }  else {
                currentNode["member"] = 1;
                currentNode["treeid"] = treeIdentifier;
            }
        },

    // Recursive descent: set the membership of every node at or below the current node
    //  to 'value'
        setMembershipIndicatorToValue  = function (currentNode,value)  {
            if (!(currentNode.children === undefined)) {
                for (var i = 0; i < currentNode.children.length; i++) {
                    setMembershipIndicatorToValue(currentNode.children[i], value);
                    currentNode.member = value;
                }
            }  else {
                currentNode.member = value;
            }
        },
        findNodeInTreeByName = function (currentNode,nameWeAreLookingFor)  {
            if (currentNode.name === nameWeAreLookingFor) {
                return   currentNode;
            }
            if (!(currentNode.children === undefined)) {
                for (var i = 0; i < currentNode.children.length; i++) {
                    var currentAttempt = findNodeInTreeByName(currentNode.children[i],nameWeAreLookingFor);
                    if ( (!( currentAttempt===undefined))&&
                        ( currentAttempt.name === nameWeAreLookingFor) )  {
                        return  currentAttempt;
                    }
//                          setTimeout("findNodeInTreeByName("+currentNode+","+nameWeAreLookingFor+")",1);
                }
            }
        },
        accumulatingAssays,
        getAssaysForActivatedNodes  = function (currentNode)  {
            if (!(currentNode.children === undefined)) {
                for (var i = 0; i < currentNode.children.length; i++) {
                    getAssaysForActivatedNodes(currentNode.children[i]);
                    if (currentNode.member === 1) {
                        if (!(currentNode.assays === undefined))  {
                            accumulatingAssays = accumulatingAssays.concat(currentNode.assays);
                        }
                    }
                }
            }  else {
                if (currentNode.member === 1) {
                    if (!(currentNode.assays === undefined))  {
                        accumulatingAssays = accumulatingAssays.concat(currentNode.assays);
                    }
                }
            }
        },

    // Recursive descent: find a node by name
        generateUniqueListOfActivatedAssays  = function (hierarchyId){
            accumulatingAssays = Array();
            getAssaysForActivatedNodes(linkedData.Hierarchy[hierarchyId].Structure.struct [0]);
            var uniqueArray = [];
            if (accumulatingAssays.length > 0) {
                accumulatingAssays.sort();
                uniqueArray = accumulatingAssays.filter(function(elem, pos) {
                    return accumulatingAssays.indexOf(elem) == pos;
                })
            }
            return uniqueArray;
        },

        adjustMembershipBasedOnSunburstClick  = function (nodeName,possibleNode,hierarchyId)   {
            // This is where we mark things as clicked!
            var retrievedNode =   findNodeInTreeByName (linkedData.Hierarchy[hierarchyId].Structure.struct [0],nodeName);
            // First set the whole tree membership to off
            setMembershipIndicatorToValue(linkedData.Hierarchy[hierarchyId].Structure.struct [0],0);
            // Now set the selected subtree to on
            setMembershipIndicatorToValue(retrievedNode,1);
            // Now we need a list of all the nodes that are turned on
            var activatedAssayList = generateUniqueListOfActivatedAssays(hierarchyId);
            sharedStructures.getAssayIndex().filterFunction(function(d){return (activatedAssayList.indexOf(d)>-1);});
            dc.redrawAll();
            return activatedAssayList;
        },
        filteredHierarchyData = function (hierarchyId)   {
            var originalTree = retrieveCurrentHierarchicalData(hierarchyId);
            var listOfActiveAssays = retrieveListOfActiveAssays ();
            var revisedTree = copyThisTree (originalTree,listOfActiveAssays);
            var activeInactiveCounts = {active: 0, inactive: 0};
            updateActiveInactiveCounts (revisedTree, activeInactiveCounts);
            return revisedTree;
        },
        cleanupOriginalHierarchyData = function (hierarchyId)   {
            var originalTree = retrieveCurrentHierarchicalData(hierarchyId);
            var listOfActiveAssays = retrieveListOfActiveAssays ();
            var activeInactiveCounts = {active: 0, inactive: 0};
            updateActiveInactiveCounts (originalTree, activeInactiveCounts);
            return originalTree;
        },

        updateActiveInactiveCounts  = function (currentNode,activeInactiveCounts)  {
            if (!(currentNode.children === undefined)) {
                // first go through all the children, and add up everything we get
                for (var i = 0; i < currentNode.children.length; i++) {
                    var newActiveInactiveCount = updateActiveInactiveCounts(currentNode.children[i],{active: 0, inactive: 0});
                    activeInactiveCounts.active += newActiveInactiveCount.active;
                    activeInactiveCounts.inactive += newActiveInactiveCount.inactive;
                }
                // now add in anything directly associated with this node
                if ((!(currentNode.assays === undefined)) &&
                    (currentNode.assays.length > 0)){
                    for (var i = 0; i < currentNode.assays.length; i++){
                        var assayAssociatedWithThisNode = currentNode.assays[i];// assayref, so treat as index
                        activeInactiveCounts.active += linkedData.Assays[assayAssociatedWithThisNode].AssayAc;
                        activeInactiveCounts.inactive += linkedData.Assays[assayAssociatedWithThisNode].AssayIn;
                    }
                }
                // we have the numbers we wanted. Store them in the tree, and then passed on to the caller
                currentNode.ac = activeInactiveCounts.active;
                currentNode.inac = activeInactiveCounts.inactive;
                currentNode.size =  activeInactiveCounts.active+activeInactiveCounts.inactive;
                return activeInactiveCounts;
            }  else {
                if ((!(currentNode.assays === undefined)) &&
                    (currentNode.assays.length > 0)){
                    for (var i = 0; i < currentNode.assays.length; i++){
                        var assayAssociatedWithThisNode = currentNode.assays[i];// assayref, so treat as index
                        activeInactiveCounts.active = activeInactiveCounts.active + linkedData.Assays[assayAssociatedWithThisNode].AssayAc;
                        activeInactiveCounts.inactive = activeInactiveCounts.inactive + linkedData.Assays[assayAssociatedWithThisNode].AssayIn;
                    }
                }
                currentNode.ac = activeInactiveCounts.active;
                currentNode.inac = activeInactiveCounts.inactive;
                currentNode.size =  activeInactiveCounts.active+activeInactiveCounts.inactive;
                return activeInactiveCounts;
            }
        },
        copyThisTree  = function (currentNode,listOfActiveAssays)  {
            if (!(currentNode.children === undefined)) {
                var newNodeWithKids = {};
                newNodeWithKids["name"] =  currentNode.name;
                newNodeWithKids["member"] =   currentNode.member;
                newNodeWithKids["treeid"] =  currentNode.treeid;
                if (!(currentNode.assays === undefined)) {
                    newNodeWithKids["assays"] = [];
                    for (var i = 0; i < currentNode.assays.length; i++){
                        // only copy in an assay if it is known to be active
                        var assayPreviouslyAssociatedWithThisNode = currentNode.assays[i];
                        if (listOfActiveAssays.indexOf(assayPreviouslyAssociatedWithThisNode) > -1){
                            newNodeWithKids["assays"].push(assayPreviouslyAssociatedWithThisNode);
                        }
                    }
                }
                newNodeWithKids["ac"] =  currentNode.ac;
                newNodeWithKids["inac"] =   currentNode.inac;
                newNodeWithKids["children"] = [];
                for (var i = 0; i < currentNode.children.length; i++) {
                    if (youOrAnyOfYourChildrenWorthSaving(currentNode.children[i],listOfActiveAssays)) {
                        newNodeWithKids["children"].push(copyThisTree(currentNode.children[i],listOfActiveAssays));
                    }
                }
                // special case. If none of your kids were worth saving then add a size parameter.  You might perhaps
                //  be a valuable node, my friend, even if all your children are worthless.
                if (newNodeWithKids.children.length==0) {
                    newNodeWithKids["size"] = currentNode.ac+currentNode.inac;
                }
                return newNodeWithKids;
            }  else {
                var newNode = {};
                newNode["name"] =  currentNode.name;
                newNode["member"] =   currentNode.member;
                newNode["treeid"] =  currentNode.treeid;
                if (!(currentNode.assays === undefined)) {
                    newNode["assays"] = [];
                    for (var i = 0; i < currentNode.assays.length; i++){
                        // only copy in an assay if it is known to be active
                        var assayPreviouslyAssociatedWithThisNode = currentNode.assays[i];
                        if (listOfActiveAssays.indexOf(assayPreviouslyAssociatedWithThisNode) > -1){
                            newNode["assays"].push(assayPreviouslyAssociatedWithThisNode);
                        }

                    }
                }
                newNode["ac"] =  currentNode.ac;
                newNode["inac"] =  currentNode.inac;
                newNode["size"] =  currentNode.size;
                return newNode;
            }
        },
        youOrAnyOfYourChildrenWorthSaving  = function(currentNode,thoseWorthSaving){
            var worthSaving = false;
            if (thoseWorthSaving.length>0){
                if (!(currentNode.assays === undefined)) {
                    for (var j = 0; j < currentNode.assays.length; j++){
                        if(thoseWorthSaving.indexOf(currentNode.assays[j])>-1){
                            worthSaving = true;
                            return worthSaving;
                        }
                    }
                }
                if (!(currentNode.children === undefined)) {
                    for (var j = 0; j < currentNode.children.length; j++) {
                        for (var i = 0; i < currentNode.children.length; i++) {
                            worthSaving = youOrAnyOfYourChildrenWorthSaving(currentNode.children[i], thoseWorthSaving);
                            if (worthSaving)  {
                                return true;
                            }
                        }
                    }
                }
            }
            return worthSaving;
        },
        adjustedPartitionSize = function(d){
            return d.size;
        },
        retrieveListOfActiveAssays = function (){
            var listOfAssayCrossObjects = sharedStructures.getAssayIndex().top(1000);
            var listOfAssayRef = [];
            for (var i = 0; i < listOfAssayCrossObjects.length; i++ )  {
                listOfAssayRef.push(listOfAssayCrossObjects[i].index);
            }
            return listOfAssayRef;
        },
        resetRootForHierarchy = function (currentNode,hierarchyId){
            var hierarchyOfChoice = linkedData.Hierarchy[hierarchyId];
            if (!(hierarchyOfChoice.Structure === undefined)) {
                // We want to actually the parent of the currently selected node. It isn't always trivial to find your parent, but we can try...
                var potentialParent = parentIdentificationTool (hierarchyOfChoice.Structure.struct [0],currentNode);
                if (!(potentialParent ===null)) {   // We found the parent, make the assignment
                    hierarchyOfChoice.Structure.rootName = potentialParent.name;
                } else {    // Could not find the parent, stick with the existing node.  This should only happen if they picked the root.
                    hierarchyOfChoice.Structure.rootName = currentNode.name;
                }

            }
        },
        resetDerivedHierarchyRouteToOriginalRoot= function (hierarchyId) {
            resetRootForHierarchy(linkedData.Hierarchy[hierarchyId].Structure.struct [0],hierarchyId);
        },
        extendedHierarchyDataExists = function (hierarchyId){
            var hierarchyOfChoice = linkedData.Hierarchy[hierarchyId];
            return (!(hierarchyOfChoice.Structure.struct === undefined));
        }    // It is certainly a bother to identify a node's parent if you don't have a parent pointer. The code below starts at the root
    //  and recursively descends until it finds the node that is the parent of the nodeThatIsTheBasisForOurSearch
        parentIdentificationTool  = function(currentNode,nodeThatIsTheBasisForOurSearch){
            if (!(currentNode === undefined)) {
                // If you have children then you might be the one
                if  (!(currentNode.children === undefined)){
                    // Are you the parent we are searching for?
                    for (var i = 0; i < currentNode.children.length; i++) {
                        var child =  currentNode.children [i];
                        if (!(child === undefined)) {
                            if (child.name === nodeThatIsTheBasisForOurSearch.name) {
                                return currentNode;
                            }
                        }
                    }
                    //  You want the parent we want exactly, but maybe one of your kids is the one
                    for (var i = 0; i < currentNode.children.length; i++) {
                        var child =  currentNode.children [i];
                        if (!(child === undefined)) {
                            var potentialParent = parentIdentificationTool (child,nodeThatIsTheBasisForOurSearch);
                            if (!(potentialParent === null)) {
                                return  potentialParent;
                            }
                        }
                    }
                }
            }
            return(null);
        }
//                ,
//         provideAListOfEveryoneWhoIsnotPointingAtTheirRoot = '


    return {
        parseData:parseData,
        appendConditionalStatusFields:appendConditionalStatusFields,
        validateLinkedData:validateLinkedData,
        numberOfWidgets: numberOfWidgets,
        retrieveCurrentHierarchicalData:retrieveCurrentHierarchicalData,
        retrieveLinkedData:retrieveLinkedData,
        adjustMembershipBasedOnSunburstClick:adjustMembershipBasedOnSunburstClick,
        filteredHierarchyData:filteredHierarchyData,
        adjustedPartitionSize:adjustedPartitionSize,
        resetRootForHierarchy:resetRootForHierarchy,
        cleanupOriginalHierarchyData:cleanupOriginalHierarchyData,
        extendedHierarchyDataExists:extendedHierarchyDataExists,
        resetDerivedHierarchyRouteToOriginalRoot:resetDerivedHierarchyRouteToOriginalRoot
    }

}());

