package bard.db.dictionary

import bard.db.command.BardCommand
import bard.db.enums.AddChildMethod
import bard.util.BardCacheUtilsService
import grails.converters.JSON
import grails.plugin.cache.CacheEvict
import grails.plugin.cache.Cacheable
import grails.plugins.springsecurity.Secured
import grails.validation.Validateable
import grails.validation.ValidationErrors
import groovy.transform.InheritConstructors
import org.apache.commons.lang3.StringUtils

import javax.servlet.http.HttpServletResponse

class ElementController {

    private static final String errorMessageKey = "errorMessageKey"
    ElementService elementService
    BuildElementPathsService buildElementPathsService
    BardCacheUtilsService bardCacheUtilsService
    ModifyElementAndHierarchyService modifyElementAndHierarchyService
    OntologyDataAccessService ontologyDataAccessService

    def index() {
        redirect(uri: '/')
    }

    def showTopLevelHierarchyHelp() {
        render(view: 'showTopLevelHierarchyHelp')
    }

    def list() {
        Map parameterMap = generatePaths()

        if (!parameterMap.containsKey(errorMessageKey)) {
            return parameterMap
        } else {
            render(parameterMap.get(errorMessageKey))
        }
    }

    @Secured(['isAuthenticated()'])
    def select() {}

    @Secured(['isAuthenticated()'])
    def listAjax() {
        Map parameterMap = generatePaths()

        if (!parameterMap.containsKey(errorMessageKey)) {
            Map map = [results: elementService.convertPathsToSelectWidgetStructures(parameterMap.list)]
            render map as JSON
        } else {
            render(parameterMap.get(errorMessageKey))
        }
    }

    @Secured(['isAuthenticated()'])
    def getChildrenAsJson(long elementId, boolean doNotShowRetired, String expectedValueType) {
        List elementHierarchyTree = elementService.getChildNodes(elementId, doNotShowRetired, expectedValueType)
        JSON elementHierarchyAsJsonTree = new JSON(elementHierarchyTree)
        render elementHierarchyAsJsonTree
    }

    @Secured(['isAuthenticated()'])
    def buildTopLevelHierarchyTree(boolean doNotShowRetired, String treeRoot, String expectedValueType) {
        List elementHierarchyTree = elementService.createElementHierarchyTree(doNotShowRetired, treeRoot, expectedValueType)
        JSON elementHierarchyAsJsonTree = new JSON(elementHierarchyTree)
        render elementHierarchyAsJsonTree
    }

    /**
     * Handles adding a new term element to the dictionary.
     * First step is to choose the new term's parent ID
     * Second step is to add content and create the new term.
     *
     * @return
     */
    @Secured(['isAuthenticated()'])
    def selectParent() {
        flash.message = ''
        Element parentElement = Element.findById(params.attributeElementId)
        render(view: 'selectParent', model: [termCommand: new TermCommand(parentElementId: parentElement.id, parentLabel: parentElement?.label, parentDescription: parentElement?.description)])
    }

    @Secured(['isAuthenticated()'])
    def addTerm() {
        flash.message = ''
        Element parentElement = Element.findById(params.attributeElementId)
        if (!parentElement || parentElement?.addChildMethod != AddChildMethod.DIRECT) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "Could not find Experiment ${params.attributeElementId} or this experiment could not be used as a parent-element for a new proposed term-element")
            return
        }
        render(view: 'addTerm', model: [termCommand: new TermCommand(parentElementId: parentElement.id, parentLabel: parentElement?.label, parentDescription: parentElement?.description)])
    }

    @Secured(['isAuthenticated()'])
    def saveTerm(TermCommand termCommand) {
        Element currentElement = null
        flash.message = ''
        if (termCommand.validate()) {

            if (!termCommand.hasErrors()) {
                //remove duplicate white spaces, then trim the string
                //we probably should also consider removing some other characters
                termCommand.label = termCommand.label.replaceAll("\\s+", " ").trim()
                currentElement =
                        this.elementService.addNewTerm(termCommand)
                if (currentElement.hasErrors()) {
                    termCommand.currentElement = currentElement
                    termCommand.transferErrorsFromCurrentElement()
                } else {
                    termCommand.success = true
                    flash.message = "Proposed term ${currentElement?.label} has been saved"
                    bardCacheUtilsService.refreshDueToNewDictionaryEntry()
                }
            }
        }
        if (request.getHeader('X-Requested-With') == 'XMLHttpRequest') {  //if ajax then render template
            render(template: 'addForm', model: [termCommand: termCommand, currentElement: currentElement])
            return
        }
        render(view: 'addTerm', model: [termCommand: termCommand, currentElement: currentElement])
    }

    @Secured(["hasRole('ROLE_CURATOR')"])
    def editHierarchyPath() {
        Map parameterMap = generatePaths()

        if (!parameterMap.containsKey(errorMessageKey)) {
            return parameterMap
        } else {
            render(parameterMap.get(errorMessageKey))
        }
    }

    @Secured(["hasRole('ROLE_CURATOR')"])
    def editHierarchy(Long id) {
        if (!id) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Element ID is missing")
            return
        }

        Element element = Element.findById(id)
        if (!element) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Could not find element ${id}")
            return
        }

        Map parameterMap = generatePathsForElements([element.id])
        parameterMap.put('element', element)

        if (!parameterMap.containsKey(errorMessageKey)) {
            return parameterMap
        } else {
            render(parameterMap.get(errorMessageKey))
        }
    }

    @Secured(["hasRole('ROLE_CURATOR')"])
    def deleteElementPath() {

        Element element = params.elementId.trim() ? Element.findById(params.elementId) : null
        if (!element || !params.elementPathToDelete.trim()) {
            render "Element ${element.id} does not exist or path to delete is missing"
            return
        }

        Map parameterMap = generatePathsForElements([element.id])
        if (parameterMap.errorMessageKey) {
            flash.message = parameterMap.errorMessageKey
            redirect(action: "editHierarchy", id: element.id)
            return
        }

        List<ElementAndFullPath> elementAndFullPathList = parameterMap.list
        assert elementAndFullPathList, "Could not find an elementAndFullPathList for element ${element.id}"

        ElementAndFullPath elementAndFullPathToDelete = elementAndFullPathList.find { ElementAndFullPath elementAndFullPath ->
            elementAndFullPath.toString() == params.elementPathToDelete.trim()
        }
        if (!elementAndFullPathToDelete) {
            flash.message = "The path ${params.elementPathToDelete.trim()} is not part of the element hieracrchy."
            redirect(action: "editHierarchy", id: element.id)
            return
        }
        //Remove the element's path. If this is the last path, make the element a ROOT.
        //Removing of the path is done by truncating all the parent hierarchy elements in the path.
        NewElementAndPath newElementAndPath = new NewElementAndPath(newPathElementList: [],
                elementHierarchyList: elementAndFullPathToDelete.path,
                newElementLabel: element.label,
                element: element)

        if (!modifyElementAndHierarchyService.updateHierarchyIfNeeded(newElementAndPath)) {
            flash.message = "Failed to delete element-path '${elementAndFullPathToDelete.toString()}'"
        }

        redirect(action: "editHierarchy", id: element.id)
    }

    @Secured(["hasRole('ROLE_CURATOR')"])
    def addElementPath() {
        Element element = Element.findById(params.elementId)
        if (!element || !params.select2FullPath?.trim() || !params.select2ElementId?.trim()) {
            render "Missing params"
            return
        }

        Map parameterMap = generatePathsForElements([element.id])
        List<ElementAndFullPath> elementAndFullPathList = parameterMap.list

        //Add the new path to the element's hierarchy
        String newFullPath = params.select2FullPath.trim() + element.label + buildElementPathsService.pathDelimeter
        if (elementAndFullPathList && elementAndFullPathList*.toString().contains(newFullPath)) {
            flash.message = "The path '${params.select2FullPath.trim()}' is already included in the element hierarchy"
            redirect(action: "editHierarchy", id: element.id)
            return
        }

        try {
            NewElementAndPath newElementAndPath = findElementsFromPathString(newFullPath, buildElementPathsService.pathDelimeter)
            newElementAndPath.elementHierarchyList = []
            newElementAndPath.newElementLabel = element.label
            newElementAndPath.element = element

            if (!modifyElementAndHierarchyService.updateHierarchyIfNeeded(newElementAndPath)) {
                flash.message = "Failed to add a new element-path '${newFullPath}'"
            }
        } catch (UnrecognizedElementLabelException e) {
            flash.message = "This internal section of the path did not contain a recognizable element:  ${e.unrecognizedText}<br/>Whole path: ${elementEditCommand.newPathString}<br/>To edit an element label, edit it when it is the at the end of the path"
        } catch (EmptyPathSectionException e) {
            flash.message = "The section of the path with this index (0-based) did not contain any text:  ${e.sectionIndex}<br/>Whole path:  ${elementEditCommand.newPathString}"
        } catch (InvalidElementPathStringException e) {
            flash.message = "The path entered is invalid:  it must beging and end with the path delimeter ${buildElementPathsService.pathDelimeter}"
        } catch (ModifyElementAndHierarchyLoopInPathException e) {
            StringBuilder idBuilder = new StringBuilder().append(buildElementPathsService.pathDelimeter)
            StringBuilder labelBuilder = new StringBuilder(buildElementPathsService.pathDelimeter)
            for (Element elm : e.pathWithLoop) {
                idBuilder.append(elm.id).append(buildElementPathsService.pathDelimeter)
                labelBuilder.append(elm.label).append(buildElementPathsService.pathDelimeter)
            }
            flash.message = "A loop was found in the proposed path:\n" +
                    "original element id:  ${element.id}\n" +
                    "entered text:  ${newFullPath}\n" +
                    "detected loop labels:  ${labelBuilder.toString()}\n" +
                    "detected loop id's:${idBuilder.toString()}"
        } catch (RuntimeException e) {
            flash.message = "Error adding a new element hierarchy path: ${newFullPath}"
        }

        redirect(action: "editHierarchy", id: element.id)
    }

    @Secured(["hasRole('ROLE_CURATOR')"])
    def edit(Long id) {
        Element element = Element.findById(id)
        if (!element) {
            flash.message = "Element ${id} does not exist"
        }

        [element: element, baseUnits: ontologyDataAccessService.getAllUnits()]
    }

    @Cacheable(value = "elementListPaths")
    private Map generatePaths() {
        Map result = null
        try {
            ElementAndFullPathListAndMaxPathLength elementAndFullPathListAndMaxPathLength = buildElementPathsService.createListSortedByString(buildElementPathsService.buildAll())
            result = [list: elementAndFullPathListAndMaxPathLength.elementAndFullPathList,
                    maxPathLength: elementAndFullPathListAndMaxPathLength.maxPathLength]
        } catch (BuildElementPathsServiceLoopInPathException e) {
            result = [errorMessageKey: """A loop was found in one of the paths based on ElementHierarchy (for the relationship ${
                buildElementPathsService.relationshipType
            }).<br/>
        The path starting before the loop was detected is:  ${e.elementAndFullPath.toString()}<br/>
        Path element hierarchies: ${e.elementAndFullPath.path}<br/>
        The id of the element hierarchy where the loop was detected is:  ${e.nextTopElementHierarchy.id}"""]
        }

        return result
    }

    private Map generatePathsForElements(List<Long> elementIds) {
        Map result
        Set<ElementAndFullPath> elementAndFullPaths = [] as Set<ElementAndFullPath>
        try {
            for (Long elementId in elementIds) {
                Element element = elementId ? Element.findById(elementId) : null
                if (element) {
                    elementAndFullPaths.addAll(buildElementPathsService.build(element))
                }
            }
            ElementAndFullPathListAndMaxPathLength elementAndFullPathListAndMaxPathLength = buildElementPathsService.createListSortedByString(elementAndFullPaths)
            result = [list: elementAndFullPathListAndMaxPathLength.elementAndFullPathList,
                    maxPathLength: elementAndFullPathListAndMaxPathLength.maxPathLength]
        } catch (BuildElementPathsServiceLoopInPathException e) {
            result = [errorMessageKey: "A loop was found in one of the paths based on ElementHierarchy (for the relationship ${buildElementPathsService.relationshipType}).\n" +
                    "The path starting before the loop was detected is:  ${e.elementAndFullPath.toString()}\n" +
                    "Path element hierarchies: ${e.elementAndFullPath.path}\n" +
                    "The id of the element hierarchy where the loop was detected is:  ${e.nextTopElementHierarchy.id}"]
        }

        return result
    }

    @Secured(["hasRole('ROLE_CURATOR')"])
    def updateHierarchyPath(ElementEditCommand elementEditCommand) {
        String errorMessage = null

        elementEditCommand.newPathString = elementEditCommand.newPathString?.trim()

        if (elementEditCommand.newPathString) {
            try {
                NewElementAndPath newElementAndPath = findElementsFromPathString(elementEditCommand.newPathString,
                        buildElementPathsService.pathDelimeter)

                newElementAndPath.elementHierarchyList = findElementHierarchyFromIds(elementEditCommand.elementHierarchyIdList)
                newElementAndPath.element = findElementFromId(elementEditCommand.elementId)

                modifyElementAndHierarchyService.modify(newElementAndPath)

            } catch (UnrecognizedElementLabelException e) {
                errorMessage = "This internal section of the path did not contain a recognizable element:  ${e.unrecognizedText}<br/>Whole path: ${elementEditCommand.newPathString}<br/>To edit an element label, edit it when it is the at the end of the path"
            } catch (EmptyPathSectionException e) {
                errorMessage = "The section of the path with this index (0-based) did not contain any text:  ${e.sectionIndex}<br/>Whole path:  ${elementEditCommand.newPathString}"
            } catch (EmptyPathException e) {
                errorMessage = "The path is empty of any element labels"
            } catch (UnrecognizedElementHierachyIdException e) {
                errorMessage = "internal data was corrupted - ElementHierarchy ID notrecognized - seek help"
            } catch (UnrecognizedElementIdException e) {
                errorMessage = "internal data was corrupted - Element ID not recognized - seek help"
            } catch (NewElementLabelMatchesExistingElementLabelException e) {
                errorMessage = "The new label provided for this element matches an existing element ID=${e.matchingElement.id} ${e.matchingElement.label}"
            } catch (InvalidElementPathStringException e) {
                errorMessage = "The path entered is invalid:  it must beging and end with the path delimeter ${buildElementPathsService.pathDelimeter}"
            } catch (ModifyElementAndHierarchyLoopInPathException e) {
                StringBuilder idBuilder = new StringBuilder().append(buildElementPathsService.pathDelimeter)
                StringBuilder labelBuilder = new StringBuilder(buildElementPathsService.pathDelimeter)
                for (Element element : e.pathWithLoop) {
                    idBuilder.append(element.id).append(buildElementPathsService.pathDelimeter)
                    labelBuilder.append(element.label).append(buildElementPathsService.pathDelimeter)
                }
                errorMessage = """A loop was found in the proposed path:<br>
original element id:  ${elementEditCommand.elementId}<br/>
entered text:  ${elementEditCommand.newPathString}<br/>
detected loop laels:  ${labelBuilder.toString()}<br/>
detected loop id's:${idBuilder.toString()}<br/>"""
            }
        } else {
            errorMessage = "There was no text passed in to parse to edit the element and element hierarchy"
        }

        if (null == errorMessage) {
            redirect(action: 'editHierarchyPath')
        } else {
            render(errorMessage)
        }
    }

    @Secured(["hasRole('ROLE_CURATOR')"])
    @CacheEvict(value = "elementListPaths")
    def update() {
        Element element = Element.findById(params.id)
        if (!element) {
            flash.message = "Could not find element ${params.id} for editing"
            redirect action: "select"
            return
        }

        element.properties['label',
                'elementStatus',
                'unit',
                'abbreviation',
                'synonyms',
                'expectedValueType',
                'addChildMethod',
                'description',
                'externalURL'] = params

        if (element.save(flush: true)) {
            bardCacheUtilsService.refreshDueToNonDictionaryEntry()
            flash.message = "Element ${element.id} saved successfully"
            redirect action: "select"
        } else {
            flash.message = "Failed to update element ${element.id}"
            render view: "edit", model: [element: element]
        }
    }

    static Element findElementFromId(Long id) {
        Element result = Element.findById(id)

        if (!result) {
            throw new UnrecognizedElementIdException()
        }

        return result
    }

    static List<ElementHierarchy> findElementHierarchyFromIds(List<Long> elementHierarchyIdList) throws
            UnrecognizedElementHierachyIdException {

        List<ElementHierarchy> result = new ArrayList<ElementHierarchy>(elementHierarchyIdList.size())

        for (Long id : elementHierarchyIdList) {
            ElementHierarchy elementHierarchy = ElementHierarchy.findById(id)
            if (elementHierarchy) {
                result.add(elementHierarchy)
            } else {
                throw new UnrecognizedElementHierachyIdException()
            }
        }

        return result
    }


    static NewElementAndPath findElementsFromPathString(String pathString, String pathDelimeter) throws
            UnrecognizedElementLabelException, EmptyPathSectionException, InvalidElementPathStringException {

        List<String> tokenList = ElementAndFullPath.splitPathIntoTokens(pathString, pathDelimeter)
        if (tokenList.size() == 0) {
            throw new EmptyPathException()
        }

        NewElementAndPath result = new NewElementAndPath()

        for (int tokenIndex = 0; tokenIndex < tokenList.size() - 1; tokenIndex++) {
            String token = tokenList.get(tokenIndex)

            if (token != "") {
                Element foundElement = Element.findByLabel(token)
                if (foundElement) {
                    result.newPathElementList.add(foundElement)
                } else {
                    throw new UnrecognizedElementLabelException(unrecognizedText: token)
                }
            } else {
                throw new EmptyPathSectionException(sectionIndex: tokenIndex)
            }
        }

        result.newElementLabel = tokenList.get(tokenList.size() - 1)

        return result
    }
}

@InheritConstructors
@Validateable
class TermCommand extends BardCommand {
    BuildElementPathsService buildElementPathsService
    Long parentElementId
    String parentLabel
    String parentDescription
    String label
    String description
    String abbreviation
    String synonyms
    String curationNotes
    String relationship = "subClassOf"
    Element currentElement
    Boolean success = false

    /**
     * Copy errors from the current element into the Command object for display
     */
    void transferErrorsFromCurrentElement() {
        this.addToErrors(new ValidationErrors(currentElement))
    }

    static def validateLabelUniqueness(String label, TermCommand termCommand) {
        if (label) {
            Element currentElement = Element.findByLabel(label.replaceAll("\\s+", " ").trim())
            if (currentElement) {
                String path = termCommand.buildElementPathsService.buildSinglePath(currentElement)
                return ['unique', label, path]
            }
        }
    }

    static def validateParentLabelMustExist(String parentLabel, TermCommand termCommand) {
        if (parentLabel) {
            //this value must exist
            if (!Element.findByLabel(parentLabel.replaceAll("\\s+", " ").trim())) {
                return 'mustexist'
            }
        }
    }

    static constraints = {
        parentLabel(blank: false, nullable: false, maxSize: Element.LABEL_MAX_SIZE, validator: { value, command ->
            validateParentLabelMustExist(value, command)
        })
        label(blank: false, nullable: false, maxSize: Element.LABEL_MAX_SIZE, validator: { value, command ->
            validateLabelUniqueness(value, command)
        })
        parentDescription(blank: true, nullable: true, maxSize: Element.DESCRIPTION_MAX_SIZE)
        description(blank: false, nullable: false, maxSize: Element.DESCRIPTION_MAX_SIZE)
        curationNotes(blank: false, nullable: false, maxSize: Element.DESCRIPTION_MAX_SIZE)
        abbreviation(blank: true, nullable: true, maxSize: Element.ABBREVIATION_MAX_SIZE)
        synonyms(blank: true, nullable: true, maxSize: Element.SYNONYMS_MAX_SIZE)
    }
}

class NewElementAndPath {
    List<Element> newPathElementList

    String newElementLabel

    List<ElementHierarchy> elementHierarchyList

    Element element

    NewElementAndPath() {
        newPathElementList = new LinkedList<Element>()
    }
}

class ElementEditCommand {
    List<Long> elementHierarchyIdList

    Long elementId

    String newPathString

    ElementEditCommand() {
        elementHierarchyIdList = new LinkedList<Long>()
    }
}


class UnrecognizedElementLabelException extends Exception {
    String unrecognizedText
}

class EmptyPathSectionException extends Exception {
    int sectionIndex
}

class EmptyPathException extends Exception {}

class UnrecognizedElementHierachyIdException extends Exception {}

class UnrecognizedElementIdException extends Exception {}