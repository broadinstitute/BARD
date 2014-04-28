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
