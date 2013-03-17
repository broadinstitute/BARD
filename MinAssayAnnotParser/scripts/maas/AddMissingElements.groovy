import maas.ElementHandlerService
import org.springframework.transaction.support.DefaultTransactionStatus
import bard.db.dictionary.Element

/**
 * Run the script to add terms to ontology (Element table), if there is ELEMENT_HIERARCHY relationship, load it also
 *
 *
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 3/6/13
 * Time: 5:00 PM
 * To change this template use File | Settings | File Templates.
 */

Map elementAndDescription = [:]
Map elementParent = [:]
ElementHandlerService.build("data/maas/missingElements.txt", elementAndDescription, elementParent)
ElementHandlerService elementHandlerService = new ElementHandlerService()
Element.withTransaction { DefaultTransactionStatus status ->
    elementHandlerService.addMissingElement("xiaorong-maas", elementAndDescription, elementParent)
}
