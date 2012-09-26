package bard.dm.minimumassayannotation.validateCreatePersist

import bard.db.registration.AssayContext
import org.springframework.transaction.TransactionStatus
import bard.db.registration.AssayContextItem
import bard.db.dictionary.Element
import bard.db.registration.AttributeType
import org.apache.commons.lang.StringUtils
import bard.dm.minimumassayannotation.ContextDTO
import bard.dm.minimumassayannotation.Attribute

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

    AssayContextsValidatorCreatorAndPersistor(String modifiedBy) {
        super(modifiedBy)
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
    void createAndPersist(List<ContextDTO> assayContextList) {
        super.validate(assayContextList)

        def out = new File('DnaSpreadsheetParserResultAssayContext' + '_' + System.currentTimeMillis() + '.txt')
        out.withWriterAppend { writer ->
            Integer totalAssayContextItems = 0
            assayContextList.each { ContextDTO assayContextDTO -> totalAssayContextItems += assayContextDTO.attributes.size()}
            Integer tally = 0

            assayContextList.each { ContextDTO assayContextDTO ->
                AssayContext.withTransaction { TransactionStatus status ->
                    //create the assay-context
                    AssayContext assayContext = new AssayContext()
                    assayContext.assay = super.getAssayFromAid(assayContextDTO.aid)
                    assayContext.modifiedBy = super.modifiedBy
                    //TODO DELETE DELETE DELETE the following line should be deleted once all assays have been uploaded to CAP
                    if (!assayContext.assay) {//skip this assay context
                        totalAssayContextItems -= assayContextDTO.attributes.size()
                        return
                    }

                    assayContext.contextName = assayContextDTO.name

                    //create the assay-context-item and add them to assay-context
                    assayContextDTO.attributes.each { Attribute attribute ->
                        AssayContextItem assayContextItem = new AssayContextItem()
                        assayContextItem.assayContext = assayContext
                        assayContextItem.attributeType = attribute.attributeType
                        assayContextItem.modifiedBy = super.modifiedBy
                        //populate the attribute key's element
                        Element element = Element.findByLabelIlike(attribute.key)
                        assert element, "We must have an element for the assay-context-item attribute"
                        assayContextItem.attributeElement = element

                        //populate attribute-value type and value
                        element = attribute.value ? Element.findByLabelIlike(attribute.value) : null
                        if (element) {//if the value string could be matched against an element then add it to the valueElement
                            assayContextItem.valueElement = element
                            assayContextItem.valueDisplay = element.label
                        } else if (attribute.value && (!(attribute.value instanceof String) || attribute.value.isNumber())) {//if the attribute's value is a number value, store it in the valueNum field
                            Float val = new Float(attribute.value)
                            assayContextItem.valueNum = val
                            assayContextItem.valueDisplay = val.toString()
                        } else if (attribute.typeIn || (attribute.attributeType == AttributeType.Free)) {//if the attribute's value is a type-in or attribute-type is Free, then simply store it the valueDisplay field
                            assayContextItem.valueDisplay = attribute.value
                        } else {
                            //throw an error
                            println("Illage attribute value: '${attribute.key}'/'${attribute.value}'")
                            assert false, "Illage attribute value"
                        }

                        //populate the qualifier field, if exists
                        if (attribute.qualifier) {
                            assayContextItem.qualifier = String.format('%-2s', attribute.qualifier)
                        }

                        postProcessAssayContextItem(assayContextItem)

                        assayContext.addToAssayContextItems(assayContextItem)
                        println("AssayContext Assay ID: ${assayContext.assay.id} (${tally++}/${totalAssayContextItems})")
                    }

                    assayContext.save()
                    if (assayContext.hasErrors()) {
                        println("AssayContext errors")
                        writer.writeLine("AssayContext Errors: ${assayContext.errors}")
                        assert false, "Errors during AssayContext saving"
                    } else {
                        writer.writeLine("Assay ID: ${assayContext.assay.id}")
                        writer.writeLine("ContextName: ${assayContext.contextName}")
                        assayContext.assayContextItems.each { AssayContextItem assayContextItem ->
                            writer.writeLine("new attribute")
                            writer.writeLine("\tAttributeElement: '${assayContextItem?.attributeElement?.label}'")
                            writer.writeLine("\tValueElement: '${assayContextItem?.valueElement?.label}'")
                            writer.writeLine("\tAttributeType: '${assayContextItem?.attributeType}'")
                            writer.writeLine("\tExtValueId: '${assayContextItem?.extValueId}'")
                            writer.writeLine("\tValueDisplay: '${assayContextItem?.valueDisplay}'")
                            writer.writeLine("\tValueNum: '${assayContextItem?.valueNum}'")
                            writer.writeLine("\tQualifier: '${assayContextItem?.qualifier}'")
                        }
                    }

                    //comment out to commit the transaction
                    //status.setRollbackOnly()
                }
            }
        }
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
        private void postProcessAssayContextItem(AssayContextItem assayContextItem) {
            if (assayContextItem.valueDisplay && assayContextItem.valueDisplay.toLowerCase().find(/^cid\W*:\W*\d+\W*/)) {//'cid:12345678'
                rebuildAssayContextItem(assayContextItem, 'PubChem CID')
            } else if (assayContextItem.valueDisplay && assayContextItem.valueDisplay.toLowerCase().find(/^uniprot\W*:/)) {//'Uniprot:Q03164'
                rebuildAssayContextItem(assayContextItem, 'UniProt')
            } else if (assayContextItem.valueDisplay && assayContextItem.valueDisplay.toLowerCase().find(/^gi\W*:/)) {//'gi:10140845'
                rebuildAssayContextItem(assayContextItem, 'protein')
            }
        }

        private void rebuildAssayContextItem(AssayContextItem assayContextItem, String findByLabelIlike) {
            String extValueId = StringUtils.split(assayContextItem.valueDisplay, ':').toList().last().trim()
            Element element = Element.findByLabelIlike(findByLabelIlike)
            assert element, "Element '${findByLabelIlike}' must exist"
            String newValueDisplay = "${element.externalURL}${extValueId}"
            assayContextItem.attributeElement = element
            assayContextItem.extValueId = extValueId
            assayContextItem.valueDisplay = newValueDisplay
        }
}
