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

import bard.dm.minimumassayannotation.ContextItemDto
import bard.dm.minimumassayannotation.ContextLoadResultsWriter
import maas.ExternalTermMapping
import org.apache.log4j.Logger

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
    private static final String goElementLabel = "GO biological process term"
    private static final String speciesElementLabel = "species name"
    static final Map<String, String> externalTermMap = ExternalTermMapping.build()

    private AssayContextCompare assayContextCompare

    private final Logger logger

    AssayContextsValidatorCreatorAndPersistor(String modifiedBy, ContextLoadResultsWriter loadResultsWriter,
                                              boolean flushSetting, Logger logger = Log.logger) {
        super(modifiedBy, loadResultsWriter, flushSetting)

        assayContextCompare = new AssayContextCompare(logger)

        this.logger = logger
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
     * @param contextList
     */
    boolean createAndPersist(List<ContextDTO> contextDtoList) {

        AssayContext.withTransaction { status ->
            for (ContextDTO contextDTO : contextDtoList) {

                if (contextDTO.contextItemDtoList.size() > 0) {
                    //create the assay-context
                    AssayContext assayContext = new AssayContext()
                    assayContext.assay = getAssayFromAid(contextDTO.aid)

                    assayContext.modifiedBy = modifiedBy

                    if (!assayContext.assay) {
                        writeMessageWhenAidNotFoundInDb(contextDTO)
                        status.setRollbackOnly()
                        return false
                    }

                    assayContext.contextName = contextDTO.name
                    assayContext.contextGroup = contextDTO.contextGroupName

                    //create the assay-context-item and add them to assay-context
                    for (ContextItemDto contextItemDto : contextDTO.contextItemDtoList) {

                        AssayContextItem assayContextItem = new AssayContextItem()
                        assayContextItem.assayContext = assayContext
                        assayContextItem.attributeType = contextItemDto.attributeType
                        assayContextItem.modifiedBy = modifiedBy
                        //populate the attribute key's element
                        Element element = Element.findByLabelIlike(contextItemDto.key)
                        if (! element) {
                            final String message = "Element in spreadsheet for the assay-context-item attribute not found in database (${contextItemDto.key})"
                            loadResultsWriter.write(contextDTO, assayContext.assay.id, ContextLoadResultsWriter.LoadResultType.fail,
                                    null, 0, message)
                            status.setRollbackOnly()
                            return false
                        }

                        assayContextItem.attributeElement = element
                        Element concentrationUnitsElement = contextItemDto.concentrationUnits ? Element.findByLabelIlike(contextItemDto.concentrationUnits) : null
                        String concentrationUnitsAbbreviation = concentrationUnitsElement ? " ${concentrationUnitsElement.abbreviation}" : ""
                        //populate attribute-value type and value
                        element = contextItemDto.value ? Element.findByLabelIlike(contextItemDto.value) : null
                        //if the value string could be matched against an element then add it to the valueElement
                        if (element) {
                            if (contextItemDto.attributeType != AttributeType.Free) {
                                assayContextItem.valueElement = element
                                assayContextItem.valueDisplay = element.label
                            }
                        }
                        //else, if the attribute's value is a number value, store it in the valueNum field
                        else if (contextItemDto.value && (contextItemDto.value instanceof Number)) {
                            if (contextItemDto.attributeType != AttributeType.Free) {
                                Double val = (Number)contextItemDto.value.doubleValue()
                                assayContextItem.valueNum = val
                                assayContextItem.qualifier = "= "
                                //If the value is a number and also has concentration-units, we need to find the units element ID and update the valueDisplay accrdingly
                                assayContextItem.valueDisplay = val.toString() + concentrationUnitsAbbreviation
                            }
                        }
                        //else, if the attribute is a numeric range (e.g., 440-460nm -> 440-460), then store it in valueMin, valueMax and make AttributeType=range.
                        else if (contextItemDto.value && (contextItemDto.value instanceof String) && contextItemDto.value.matches(/^\d+\-\d+$/)) {
                            if (contextItemDto.attributeType != AttributeType.Free) {
                                final String[] rangeStringArray = contextItemDto.value.split("-")
                                assayContextItem.valueMin = new Float(rangeStringArray[0])
                                assayContextItem.valueMax = new Float(rangeStringArray[1])
                                assayContextItem.valueDisplay = contextItemDto.value + concentrationUnitsAbbreviation //range-units are reported separately.
                                assayContextItem.attributeType = AttributeType.Range
                            }
                        }
                        //else, if the attribute's is a type-in or attribute-type is Free, then simply store it the valueDisplay field
                        else if (contextItemDto.typeIn  ){
                            //|| (contextItemDto.attributeType == AttributeType.Free)) {
                            assayContextItem.valueDisplay = contextItemDto.value
                        }
                        else if (contextItemDto.attributeType == AttributeType.Free) {
                            //do nothing - since it is free no need to apply any value
                        }
                        else {
                            final String message = "Value of context item not recognized as element or numerical value: '${contextItemDto.key}'/'${contextItemDto.value}'"
                            logger.info(message)
                            loadResultsWriter.write(contextDTO, assayContext.assay.id, ContextLoadResultsWriter.LoadResultType.fail,
                                    null, 0, message)
                            status.setRollbackOnly()
                            return false
                        }

                        //populate the qualifier field, if exists, and prefix the valueDisplay with it
                        if (contextItemDto.qualifier) {
                            if (contextItemDto.attributeType != AttributeType.Free){
                                assayContextItem.qualifier = String.format('%-2s', contextItemDto.qualifier)
                                assayContextItem.valueDisplay = "${contextItemDto.qualifier}${assayContextItem.valueDisplay}"
                            }
                        }


                        if (! postProcessAssayContextItem(assayContextItem, contextDTO)) {
                            status.setRollbackOnly()
                            return false
                        }

                        if (! assayContextItem.validate()) {
                            final String message = "Problem creating assay context item.  spreadsheet data: $contextItemDto  gorm:  ${convertToString(assayContextItem)} error message:  ${assayContextItem.errors.dump()}"
                            loadResultsWriter.write(contextDTO, assayContext.assay.id, ContextLoadResultsWriter.LoadResultType.fail,
                                    null, 0, message)
                            status.setRollbackOnly()
                            return false
                        }

                        assayContext.addToAssayContextItems(assayContextItem)
                    }

                    contextDTO.wasSaved = checkForDuplicateOrSubsetAndSave(assayContext, contextDTO)
                }
            }
//            status.setRollbackOnly()
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
    boolean postProcessAssayContextItem(AssayContextItem assayContextItem, ContextDTO contextDTO) {
        if (assayContextItem.valueDisplay && assayContextItem.valueDisplay.toLowerCase().find(/^cid\W*:\W*\d+\W*/)) {//'cid:12345678'
            return rebuildAssayContextItem(assayContextItem, 'PubChem CID', contextDTO)
        } else if (assayContextItem.valueDisplay && assayContextItem.valueDisplay.toLowerCase().find(/^uniprot\W*:/)) {//'Uniprot:Q03164'
            return rebuildAssayContextItem(assayContextItem, 'UniProt accession number', contextDTO)
        } else if (assayContextItem.valueDisplay && assayContextItem.valueDisplay.toLowerCase().find(/^uniprotkb\W*:/)) {// 'UnitProtKB:UniProtKB:Q9QUQ5'
            return rebuildAssayContextItem(assayContextItem, 'UniProt accession number', contextDTO)
        }else if (assayContextItem.valueDisplay && assayContextItem.valueDisplay.toLowerCase().find(/^gi\W*:/)) {//'gi:10140845'
            return rebuildAssayContextItem(assayContextItem, 'GenBank ID', contextDTO)
        } else if (assayContextItem.valueDisplay && assayContextItem.valueDisplay.toLowerCase().indexOf("go:") >= 0) {//'go:
            return rebuildAssayContextItem(assayContextItem, goElementLabel, contextDTO)
        }else if (assayContextItem.valueDisplay && assayContextItem.attributeElement.label.equals(speciesElementLabel)) {//'go:
            return rebuildAssayContextItem(assayContextItem, speciesElementLabel, contextDTO)
        }
        return true
    }

    private boolean rebuildAssayContextItem(AssayContextItem assayContextItem, String findByLabelIlike, ContextDTO contextDTO) {
        String extValueId = StringUtils.split(assayContextItem.valueDisplay, ':').toList().last().trim()
        if (findByLabelIlike.equals(goElementLabel) || findByLabelIlike.equals(speciesElementLabel)) {
            if (externalTermMap.containsKey(extValueId.toLowerCase())) {
                extValueId = externalTermMap.get(extValueId.toLowerCase())
            } else if (!StringUtils.isNumeric(extValueId)) {
                //logger.info("possible GO term or species name that is not mapped: ${extValueId}")
                loadResultsWriter.write(contextDTO, assayContextItem.assayContext.assay.id,
                        ContextLoadResultsWriter.LoadResultType.fail, null, 0,
                        "possible GO term or species name that is not mapped: ${extValueId} in ${findByLabelIlike}")
                return false
            }
        }

        Element element = Element.findByLabelIlike(findByLabelIlike)
        if (!element) {
            loadResultsWriter.write(contextDTO, assayContextItem.assayContext.assay.id,
                    ContextLoadResultsWriter.LoadResultType.fail, null, 0,
                    "element ${assayContextItem.valueDisplay} not found in database ${findByLabelIlike}")
            return false
        }

        String newValueDisplay = "${element.externalURL}${extValueId}"
        assayContextItem.attributeElement = element
        assayContextItem.extValueId = extValueId
        assayContextItem.valueDisplay = newValueDisplay
        return true
    }

    boolean checkForDuplicateOrSubsetAndSave(AssayContext assayContext, ContextDTO contextDTO) {
        boolean doSave = true

        Set<AssayContext> assayContextOrigSet = new HashSet<AssayContext>(assayContext.assay.assayContexts)

        for (AssayContext assayContextOrig : assayContextOrigSet) {
            if (!assayContextOrig)
                continue
            if (!assayContextOrig.equals(assayContext)) {
                ComparisonResult<ContextItemComparisonResultEnum> compResult = assayContextCompare.compareContext(assayContextOrig, assayContext)

                if (compResult) {
                    if (compResult.resultEnum.equals(ComparisonResultEnum.ExactMatch)) {
                        //do not save the assayContext because there is an exact match already present for this assay in the database
                        logger.info("assay: ${assayContext.assay.id} not saving a new assay context because it is a duplicate of existing assay context in the database ${assayContextOrig.id}")
                        loadResultsWriter.write(contextDTO, assayContext.assay.id, ContextLoadResultsWriter.LoadResultType.alreadyLoaded,
                                assayContextOrig.assayContextItems.size(), 0, "duplicate context already exists in database $assayContextOrig.id")
                        doSave = false
                        break
                    } else if (compResult.resultEnum.equals(ComparisonResultEnum.SubsetMatch)) {
                        if (assayContext.assayContextItems.size() > assayContextOrig.assayContextItems.size()) {
                            logger.info("assay: ${assayContext.assay.id} deleting original assay context ${assayContextOrig.id} since it is a subset of the new one")
                            assayContext.assay.assayContextItems.removeAll(assayContextOrig.assayContextItems)
                            assayContext.assay.assayContexts.remove(assayContextOrig)
                            assayContextOrig.delete() //the original assay context from the database is a subset of the new one, so delete it
                            loadResultsWriter.write(contextDTO, assayContext.assay.id,
                                    ContextLoadResultsWriter.LoadResultType.deleteOriginal,
                                    assayContextOrig.assayContextItems.size(), contextDTO.contextItemDtoList.size(),
                                    "load contains more information than original, delete original $assayContextOrig.id")
                            //NOTE:  no break here b/c we want to keep looking for duplicates
                        } else {
                            //do not save the assayContext because it is a subset of one that is already in the database
                            logger.info("assay: ${assayContext.assay.id} AID: ${contextDTO.aid} not saving a new assay context because it is a subset of existing assay context in the database ${assayContextOrig.id}")
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
            assayContext.assay.assayContexts.add(assayContext)
            assayContext.save(flush: flushSetting)
            final ContextLoadResultsWriter.LoadResultType loadResultType
            final String message
            final int numLoaded
            if (assayContext.hasErrors()) {
                assayContext.assay.assayContexts.remove(assayContext)
                logger.info("AssayContext errors: ${assayContext.errors.dump()}")
                loadResultType = ContextLoadResultsWriter.LoadResultType.fail

                StringBuilder builder = new StringBuilder()
                builder.append("failed to save.  contextItemDTO's:  ")
                for (ContextItemDto contextItemDto : contextDTO.contextItemDtoList) {
                    builder.append("${contextItemDto.key}:${contextItemDto.value}, ")
                }
                builder.append("database errors:  ${assayContext.errors.dump()}")
                message = builder.toString()
                numLoaded = 0
            } else {
                loadResultType = ContextLoadResultsWriter.LoadResultType.success
                message = null
                numLoaded = assayContext.assayContextItems.size()
            }

            loadResultsWriter.write(contextDTO, assayContext.assay.id, loadResultType, null, numLoaded, message)
        }

        return doSave
    }

    private static String convertToString(AssayContextItem aci) {
        StringBuilder builder = new StringBuilder()

        builder.append("attribute:  ${aci.attributeElement.label} ${aci.attributeElement.id} ")
        builder.append("valueDisplay:  ${aci.valueDisplay} ")
        builder.append("valueElement:  ${aci.valueElement.label} ${aci.valueElement.id} ")
        builder.append("valueNum:  ${aci.valueNum} ")
        builder.append("valueMin valueMax:  ${aci.valueMin} ${aci.valueMax} ")
        builder.append("attributeType:  ${aci.attributeType} ")
        builder.append("qualifier:  ${aci.qualifier} ")

        return builder.toString()
    }
}
