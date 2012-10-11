package postUploadProcessing

import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Row
import bard.dm.minimumassayannotation.ContextDtoFromContextGroupCreator
import bard.db.registration.AssayContext
import bard.dm.postUploadProcessing.ContextItem
import bard.dm.postUploadProcessing.ContextChangeDTO
import bard.dm.postUploadProcessing.ContextChange
import bard.db.registration.Assay
import bard.db.registration.AssayContextItem
import bard.db.dictionary.Element

class ContextGroupService {
    final String MODIFIED_BY = "gwalzer"
    ContextDtoFromContextGroupCreator contextDtoFromContextGroupCreator = new ContextDtoFromContextGroupCreator()

    List<ContextChangeDTO> parseFile(File inputFile, Integer START_ROW) {
        InputStream inp = new FileInputStream(inputFile)
        Workbook wb = new XSSFWorkbook(inp);
        Sheet sheet = wb.getSheetAt(0);
        Integer rowCount = 0 //rows in spreadsheet are zero-based

        List<ContextChangeDTO> contextChangeDTOList = []

        ContextChangeDTO contextChangeDTO
        for (Row row : sheet) {
            if (rowCount < START_ROW - 1) {//skip the header rows
                rowCount++
                continue
            }

            //Get attributes from a spreadsheet's row
            Double adid = contextDtoFromContextGroupCreator.getCellContentByRowAndColumnIds(row, 'A')
            Double sourceAssayContextId = contextDtoFromContextGroupCreator.getCellContentByRowAndColumnIds(row, 'B')
            String sourceAttributeLabel = contextDtoFromContextGroupCreator.getCellContentByRowAndColumnIds(row, 'C')
            String sourceValueLabel = contextDtoFromContextGroupCreator.getCellContentByRowAndColumnIds(row, 'D')
            String modifiedAttributeLabel = contextDtoFromContextGroupCreator.getCellContentByRowAndColumnIds(row, 'E')
            assert modifiedAttributeLabel, 'modifiedAttributeLabel can never be empty; we do not allow empty lines'
            String modifiedValueLabel = contextDtoFromContextGroupCreator.getCellContentByRowAndColumnIds(row, 'F')
            Boolean newGroup = contextDtoFromContextGroupCreator.getCellContentByRowAndColumnIds(row, 'G')

            Boolean newChangeParagraph = sourceAttributeLabel //every time we encounter a new source-attribute, a new change-group (a 'paragraph' in the spreadsheet) begins

            //If we are starting a new context-change paragraph, we should create a new ContextChangeDTO. Otherwise, we simply adding consecutive modified-items to the existing ContextChangeDTO
            if (newChangeParagraph) {
                //do some basic validations
                if (sourceAttributeLabel) {
                    assert sourceAssayContextId, 'In a new line/context-paragraph, if sourceAssayContextId id defined then adid must be defined as well'
                }

                //Create a new ContextChangeDTO and update its relevant properties
                contextChangeDTO = new ContextChangeDTO()
                contextChangeDTO.adid = adid
                contextChangeDTO.sourceAssayContextId = sourceAssayContextId
                ContextItem sourceContextItem = new ContextItem(attributeLabel: sourceAttributeLabel, valueLabel: sourceValueLabel)
                contextChangeDTO.sourceContextItem = sourceContextItem
                ContextItem modifiedContextItem = new ContextItem(attributeLabel: modifiedAttributeLabel, valueLabel: modifiedValueLabel)
                contextChangeDTO.modifiedContextItems.add(modifiedContextItem)
                contextChangeDTO.newGroup = newGroup

                contextChangeDTOList << contextChangeDTO
            }
            else {
                //do some basic validations
                assert !adid && !sourceAssayContextId && !sourceAttributeLabel && !sourceValueLabel, 'ADID, SourceAssayContext, SourceAttribure and SourceValue could only be set in the first line of a new context-paragraph'

                //Add a new modified context-item to the existing ContextChangeDTO
                ContextItem modifiedContextItem = new ContextItem(attributeLabel: modifiedAttributeLabel, valueLabel: modifiedValueLabel)
                contextChangeDTO.modifiedContextItems.add(modifiedContextItem)
                //We currently ignore consecutive NewGroup flag indications; only the one in the first line of the context-paragraph is used.
//                contextChangeDTO.newGroup = newGroup
            }
        }

        return contextChangeDTOList
    }

    /**
     * Converts the list of ContextChangeDTOs (representing the spreadsheet's input groups) into a ContextChange with types properties that can then could be call on its doChange() method
     *
     * @param contextChangeDTOs
     * @return
     */
    List<ContextChange> buildContextChangeListFromDTOs(List<ContextChangeDTO> contextChangeDTOs) {
        List<ContextChange> contextChangeList = []
        for (ContextChangeDTO contextChangeDTO : contextChangeDTOs) {
            List<ContextChange> contextChanges = buildContextChangeRecursive(contextChangeDTO.adid as Long,
                    contextChangeDTO.sourceAssayContextId,
                    contextChangeDTO.sourceContextItem,
                    contextChangeDTO.modifiedContextItems,
                    contextChangeDTO.newGroup)
            contextChangeList.addAll(contextChanges)
        }

        return contextChangeList
    }

    /**
     * 1. If ADID is null, we need to search across all matching assays in the database
     * 2. If AssayContext was left null, the for a given assay, we need to search across all its matching AssayContexts
     * 3. If ValueLabel is null it mean the item (AssayContextItem) has a value_num instead of a dictionary element; in that case we need to use that numeric value
     *
     * @param adid
     * @param sourceAssayContextId
     * @param sourceContextItem
     * @param modifiedContextItems
     * @param newGroup
     * @return
     */
    List<ContextChange> buildContextChangeRecursive(Long adid, Double sourceAssayContextId, ContextItem sourceContextItem, List<ContextItem> modifiedContextItems, Boolean newGroup) {
        List<ContextChange> contextChangeList = []
        // #1
        if (!adid) {
            Assay.list().each {Assay assay ->
                contextChangeList.addAll(buildContextChangeRecursive(assay.id,
                        sourceAssayContextId,
                        sourceContextItem,
                        modifiedContextItems,
                        newGroup)
                )
            }
        }
        else if (!sourceAssayContextId) {// #2
            Assay assay = Assay.findById(adid)
            assert assay, 'At this point, assay must be defined'
            assay.assayContexts.each {AssayContext assayContext ->
                contextChangeList.addAll(buildContextChangeRecursive(assay.id,
                        assayContext.id,
                        sourceContextItem,
                        modifiedContextItems,
                        newGroup)
                )
            }
        }
        else {// #3
            Assay assay = Assay.findById(adid)
            AssayContext assayContext = AssayContext.findById(sourceAssayContextId)
            assert assay.assayContexts.contains(assayContext), 'assayContext is not associated with the provided assay'
            Element sourceAttributeElement = Element.findByLabelIlike(sourceContextItem.attributeLabel.toLowerCase().trim())
            assert sourceAttributeElement, 'source-attribute-element must exist'
            AssayContextItem sourceAssayContextItem = AssayContextItem.findByAttributeElementAndAssayContext(sourceAttributeElement, assayContext)
            assert sourceAssayContextItem, 'sourceAssayContext must exist'

            //Iterate over all the modified items and create a list of AssayContextItems out of them
            List<AssayContextItem> modifiedAssayContextItems = modifiedContextItems.collect {ContextItem modifiedContextItem ->
                AssayContextItem newAssayContextItem = new AssayContextItem()
//                assayContext.addToAssayContextItems(newAssayContextItem)
                newAssayContextItem.assayContext = assayContext
                newAssayContextItem.dateCreated = new Date()
                newAssayContextItem.modifiedBy = MODIFIED_BY

                Element attributeElement = Element.findByLabelIlike(modifiedContextItem.attributeLabel.trim())
                assert attributeElement, 'The Modified Attribute element must exist'
                newAssayContextItem.attributeElement = attributeElement
                //If the value-element exist, use valueElement in the AssayContextItem; otherwise, use the valueNum property instead
                if (modifiedContextItem.valueLabel.trim()) {
                    Element valueElement = Element.findByLabelIlike(modifiedContextItem.valueLabel.trim())
                    assert valueElement, 'The Modified Value element must exist'
                    newAssayContextItem.valueElement = valueElement
                    newAssayContextItem.valueDisplay = newAssayContextItem.valueElement.label
                }
                else {
                    newAssayContextItem.valueNum = sourceAssayContextItem.valueNum
                    newAssayContextItem.valueDisplay = sourceAssayContextItem.valueDisplay
                    newAssayContextItem.valueMax = sourceAssayContextItem.valueMax
                    newAssayContextItem.valueMin = sourceAssayContextItem.valueMin
                    newAssayContextItem.qualifier = sourceAssayContextItem.qualifier
                    newAssayContextItem.extValueId = sourceAssayContextItem.extValueId
                }
                newAssayContextItem.attributeType = sourceAssayContextItem.attributeType

//                assert newAssayContextItem.save(flush: true)
                return newAssayContextItem
            }

//            assert assayContext.save()
            //create the ContextChange from the ContextChangeDTO
            ContextChange contextChange = new ContextChange(assay: assay,
                    sourceAssayContext: assayContext,
                    sourceItem: sourceAssayContextItem,
                    modifiedItems: modifiedAssayContextItems,
                    newGroup: newGroup)
            contextChangeList.add(contextChange)
        }

        return contextChangeList
    }
}
