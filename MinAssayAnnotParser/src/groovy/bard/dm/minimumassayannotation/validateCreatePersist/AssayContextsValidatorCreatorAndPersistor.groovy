package bard.dm.minimumassayannotation.validateCreatePersist

import bard.db.registration.AssayContext
import bard.db.registration.AssayContextItem
import bard.db.dictionary.Element
import bard.db.registration.AttributeType
import org.apache.commons.lang.StringUtils
import bard.dm.minimumassayannotation.ContextDTO

import bard.dm.assaycompare.AssayContextCompare
import bard.dm.assaycompare.ComparisonResult
import bard.dm.assaycompare.ContextItemComparisonResultEnum
import bard.dm.assaycompare.ComparisonResultEnum
import bard.dm.Log
import bard.dm.minimumassayannotation.LoadResultsWriter
import bard.dm.minimumassayannotation.ContextItemDto

/**
 * Creates and persists AssayContexts and AssayContextItems from the group of attributes we created earlier.
 * 1. Attribute key are taken always from Element (AssayContextItem.attributeElement), unless it's a qualifier attribute.
 * 2. Attribute value is taken from:
 *  i. if an element exist, use that and update AssayContextItem.valueElement
 *  ii. if the value is a numeric value, update valueNum
 *  iii. if the value is type-in, update valueDisplay only
 *  iv. if the attribute-type is Free, accept empty values
 *  v. otherwise, throw an error
 * 3. Always update valueDisplay
 * 4. If the attribute is a qualifier, update the qualifier field
 */
class AssayContextsValidatorCreatorAndPersistor extends ValidatorCreatorAndPersistor {
    private static final String goElementLabel = "GO process term"

    private Map<String, String> goLookupMap

    private AssayContextCompare assayContextCompare

    AssayContextsValidatorCreatorAndPersistor(String modifiedBy, LoadResultsWriter loadResultsWriter) {
        super(modifiedBy, loadResultsWriter)

        goLookupMap = ["dna damage response, signal transduction by p53 class mediator": "0030330",
                "histone serine kinase activity": "0035174",
                "cell proliferation": "0008283",
                "drug metabolic process": "0017144",
                "drug transport": "0015893",
                "cell death": "0008219"]

        assayContextCompare = new AssayContextCompare()
    }

    /**
     * Creates and persists AssayContexts and AssayContextItems from the group of attributes we created earlier.
     * 1. Attribute key are taken always from Element (AssayContextItem.attributeElement), unless it's a qualifier attribute.
     * 2. Attribute value is taken from:
     *  i. if an element exist, use that and update AssayContextItem.valueElement
     *  ii. if the value is a numeric value, update valueNum
     *  iii. if the value is type-in, update valueDisplay only
     *  iv. if the attribute-type is Free, accept empty values
     *  v. otherwise, throw an error
     * 3. Always update valueDisplay
     * 4. If the attribute is a qualifier, update the qualifier field
     *
     * @param assayContextList
     */
    boolean createAndPersist(List<ContextDTO> assayContextList) {

        Integer totalAssayContextItems = 0
        assayContextList.each { ContextDTO assayContextDTO -> totalAssayContextItems += assayContextDTO.attributes.size()}
        Integer tally = 0

        AssayContext.withTransaction { status ->
            assayContextList.each { ContextDTO assayContextDTO ->

                //create the assay-context
                AssayContext assayContext = new AssayContext()
                assayContext.assay = super.getAssayFromAid(assayContextDTO.aid)
                assayContext.modifiedBy = super.modifiedBy
                //TODO DELETE DELETE DELETE the following line should be deleted once all assays have been uploaded to CAP
                if (!assayContext.assay) {//skip this assay context
                    totalAssayContextItems -= assayContextDTO.attributes.size()
                    super.writeMessageWhenAidNotFoundInDb(assayContextDTO.aid, assayContextDTO.name)
                    status.setRollbackOnly()
                    return false
                }

                assayContext.contextName = assayContextDTO.name

                //create the assay-context-item and add them to assay-context
                assayContextDTO.attributes.each { ContextItemDto attribute ->
                    AssayContextItem assayContextItem = new AssayContextItem()
                    assayContextItem.assayContext = assayContext
                    assayContextItem.attributeType = attribute.attributeType
                    assayContextItem.modifiedBy = super.modifiedBy
                    //populate the attribute key's element
                    Element element = Element.findByLabelIlike(attribute.key)
                    if (! element) {
                        final String message = "Element in spreadsheet for the assay-context-item attribute not found in database (${attribute.key})"
                        super.loadResultsWriter.write(assayContextDTO.aid, assayContext.assay.id, assayContextDTO.name,
                                                    LoadResultsWriter.LoadResultType.fail, message)
                        status.setRollbackOnly()
                        return false
                    }

                    assayContextItem.attributeElement = element
                    Element concentrationUnitsElement = attribute.concentrationUnits ? Element.findByLabelIlike(attribute.concentrationUnits) : null
                    String concentrationUnitsAbbreviation = concentrationUnitsElement ? " ${concentrationUnitsElement.abbreviation}" : ""
                    //populate attribute-value type and value
                    element = attribute.value ? Element.findByLabelIlike(attribute.value) : null
                    //if the value string could be matched against an element then add it to the valueElement
                    if (element) {
                        assayContextItem.valueElement = element
                        assayContextItem.valueDisplay = element.label
                    }
                    //else, if the attribute's value is a number value, store it in the valueNum field
                    else if (attribute.value && (!(attribute.value instanceof String) || attribute.value.isNumber())) {
                        Float val = new Float(attribute.value)
                        assayContextItem.valueNum = val
                        //If the value is a number and also has concentration-units, we need to find the units element ID and update the valueDisplay accrdingly
                        assayContextItem.valueDisplay = val.toString() + concentrationUnitsAbbreviation
                    }
                    //else, if the attribute is a numeric range (e.g., 440-460nm -> 440-460), then store it in valueMin, valueMax and make AttributeType=range.
                    else if (attribute.value && (attribute.value instanceof String) && attribute.value.matches(/^\d+\-\d+$/)) {
                        final String[] rangeStringArray = attribute.value.split("-")
                        assayContextItem.valueMin = new Float(rangeStringArray[0])
                        assayContextItem.valueMax = new Float(rangeStringArray[1])
                        assayContextItem.valueDisplay = attribute.value + concentrationUnitsAbbreviation //range-units are reported separately.
                        assayContextItem.attributeType = AttributeType.Range
                    }
                    //else, if the attribute's is a type-in or attribute-type is Free, then simply store it the valueDisplay field
                    else if (attribute.typeIn || (attribute.attributeType == AttributeType.Free)) {
                        assayContextItem.valueDisplay = attribute.value
                    }
                    else {
                        final String message = "Value of context item not recognized as element or numerical value: '${attribute.key}'/'${attribute.value}'"
                        Log.logger.info(message)
                        super.loadResultsWriter.write(assayContextDTO.aid, assayContext.assay.id, assayContextDTO.name, LoadResultsWriter.LoadResultType.fail, message)
                        status.setRollbackOnly()
                        return false
                    }

                    //populate the qualifier field, if exists, and prefix the valueDisplay with it
                    if (attribute.qualifier) {
                        assayContextItem.qualifier = String.format('%-2s', attribute.qualifier)
                        assayContextItem.valueDisplay = "${attribute.qualifier}${assayContextItem.valueDisplay}"
                    }

                    if (! postProcessAssayContextItem(assayContextItem, assayContextDTO.aid, assayContextDTO.name)) {
                        status.setRollbackOnly()
                        return false
                    }

                    assayContext.addToAssayContextItems(assayContextItem)
                    Log.logger.info("AssayContext Assay ID: ${assayContext.assay.id} (${tally++}/${totalAssayContextItems})")
                }

                checkForDuplicateOrSubsetAndSave(assayContext, assayContextDTO.aid, assayContextDTO.name)
            }
        }
        return true
    }

    /**
     *  if the value field is of the format 'cid:12345678' then:
     *  1. lookup in the Element table for the element.label='PubChem CID' and take the element.externalUrl value from it
     *  2. Strip out the 'cid:' part and use the numeric value to concatenate: externalUrl + cid_number (e.g., 'http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?cid=12345678'
     *  3. Use that to populate the valueDisplay
     *  4. Use the element.id as the ValueElement.id
     *
     *  if the value field is of the format 'Uniprot:Q03164' do same as above but for 'UniProt' in the element table
     */
    private boolean postProcessAssayContextItem(AssayContextItem assayContextItem, Long aid, String contextName) {
        if (assayContextItem.valueDisplay && assayContextItem.valueDisplay.toLowerCase().find(/^cid\W*:\W*\d+\W*/)) {//'cid:12345678'
            return rebuildAssayContextItem(assayContextItem, 'PubChem CID', aid, contextName)
        } else if (assayContextItem.valueDisplay && assayContextItem.valueDisplay.toLowerCase().find(/^uniprot\W*:/)) {//'Uniprot:Q03164'
            return rebuildAssayContextItem(assayContextItem, 'UniProt', aid, contextName)
        } else if (assayContextItem.valueDisplay && assayContextItem.valueDisplay.toLowerCase().find(/^gi\W*:/)) {//'gi:10140845'
            return rebuildAssayContextItem(assayContextItem, 'protein', aid, contextName)
        } else if (assayContextItem.valueDisplay && assayContextItem.valueDisplay.toLowerCase().indexOf("go:") >= 0) {//'go:
            return rebuildAssayContextItem(assayContextItem, goElementLabel, aid, contextName)
        }
    }

    private boolean rebuildAssayContextItem(AssayContextItem assayContextItem, String findByLabelIlike, Long aid, String contextName) {
        String extValueId = StringUtils.split(assayContextItem.valueDisplay, ':').toList().last().trim()

        if (findByLabelIlike.equals(goElementLabel)) {
            if (goLookupMap.containsKey(extValueId.toLowerCase())) {
                extValueId = goLookupMap.get(extValueId.toLowerCase())
            } else if (!extValueId.isNumber()) {
                Log.logger.info("possible GO term that is not mapped: ${extValueId}")
            }
        }

        Element element = Element.findByLabelIlike(findByLabelIlike)
        if (!element) {
            super.loadResultsWriter.write(aid, assayContextItem.assayContext.assay.id, contextName,
                    LoadResultsWriter.LoadResultType.fail, "element not found in database ${findByLabelIlike}")
            return false
        }

        String newValueDisplay = "${element.externalURL}${extValueId}"
        assayContextItem.attributeElement = element
        assayContextItem.extValueId = extValueId
        assayContextItem.valueDisplay = newValueDisplay

        return true
    }

    private void checkForDuplicateOrSubsetAndSave(AssayContext assayContext, Long aid, String assayContextDtoName) {
        boolean doSave = true

        Set<AssayContext> assayContextOrigSet = new HashSet<AssayContext>(assayContext.assay.assayContexts)

        for (AssayContext assayContextOrig : assayContextOrigSet) {
            if (!assayContextOrig.equals(assayContext)) {
                ComparisonResult<ContextItemComparisonResultEnum> compResult = assayContextCompare.compareContext(assayContextOrig, assayContext)

                if (compResult) {
                    if (compResult.resultEnum.equals(ComparisonResultEnum.ExactMatch)) {
                        //do not save the assayContext because there is an exact match already present for this assay in the database
                        Log.logger.info("assay: ${assayContext.assay.id} not saving a new assay context because it is a duplicate of existing assay context in the database ${assayContextOrig.id}")
                        super.loadResultsWriter.write(aid, assayContext.assay.id, assayContextDtoName, LoadResultsWriter.LoadResultType.alreadyLoaded,
                                "duplicate context already exists in database $assayContextOrig.id")
                        doSave = false
                        break;
                    } else if (compResult.resultEnum.equals(ComparisonResultEnum.SubsetMatch)) {
                        if (assayContext.assayContextItems.size() > assayContextOrig.assayContextItems.size()) {
                            Log.logger.info("assay: ${assayContext.assay.id} deleting original assay context ${assayContextOrig.id} since it is a subset of the new one")
                            assayContext.assay.assayContextItems.removeAll(assayContextOrig.assayContextItems)
                            assayContext.assay.assayContexts.remove(assayContextOrig)
                            assayContextOrig.delete() //the original assay context from the database is a subset of the new one, so delete it
                            super.loadResultsWriter.write(aid, assayContext.assay.id, assayContextDtoName,
                                    LoadResultsWriter.LoadResultType.deleteOriginal,
                                    "load contains more information than original, delete original $assayContextOrig.id")
                            //NOTE:  no break here b/c we want to keep looking for duplicates
                        } else {
                            //do not save the assayContext because it is a subset of one that is already in the database
                            Log.logger.info("assay: ${assayContext.assay.id} not saving a new assay context because it is a subset of existing assay context in the database ${assayContextOrig.id}")
                            doSave = false
                            break
                        }
                    } else {
                        //it was either a partial match or not a match, so save it
                    }
                } else {
                    //no context items to compare, so it is not a duplicate, so it can be saved
                }
            }
        }

        if (doSave) {
            assayContext.save()
            final LoadResultsWriter.LoadResultType loadResultType
            final String message
            if (assayContext.hasErrors()) {
                Log.logger.info("AssayContext errors: ${assayContext.errors.dump()}")
                loadResultType = LoadResultsWriter.LoadResultType.fail
                message = "failed to load b/c of database errors:  ${assayContext.errors.dump()}"
            } else {
                loadResultType = LoadResultsWriter.LoadResultType.success
                message = ""
            }

            super.loadResultsWriter.write(aid, assayContext.assay.id, assayContextDtoName, loadResultType, message)
        }
    }
}
