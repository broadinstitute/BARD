import maas.ElementHandlerService
import org.springframework.transaction.support.DefaultTransactionStatus
import bard.db.dictionary.Element
import bard.db.audit.BardContextUtils

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

String fileName = System.getProperty("fileName")
Map elementAndDescription = [:]
Map elementParent = [:]
// data format with element, description, parentid
ElementHandlerService.build(fileName, elementAndDescription, elementParent)
ElementHandlerService elementHandlerService = new ElementHandlerService()
Element.withTransaction { DefaultTransactionStatus status ->
    BardContextUtils.setBardContextUsername(ctx.sessionFactory.currentSession, "xiaorong-maas")
    elementHandlerService.addMissingElement("xiaorong-maas", elementAndDescription, elementParent)
}

//elementAndDescription = [:]
//elementParent = [:]
//// data format with element, description
//// data format with element, parentid
//ElementHandlerService.buildElementDescription("data/maas/elementDescription.txt", elementAndDescription)
//ElementHandlerService.buildElementParent("data/maas/elementParent.txt", elementParent)
//
//elementHandlerService = new ElementHandlerService()
//Element.withTransaction { DefaultTransactionStatus status ->
//    elementHandlerService.addMissingElement("xiaorong-maas", elementAndDescription, elementParent)
//}
