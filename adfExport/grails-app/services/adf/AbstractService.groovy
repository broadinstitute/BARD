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

package adf

import org.openrdf.model.ValueFactory
import org.openrdf.model.URI
import org.openrdf.model.impl.ValueFactoryImpl
import org.openrdf.model.Model
import org.openrdf.model.impl.TreeModel
import org.openrdf.model.vocabulary.RDFS

class AbstractService {

    static final String adfOntology = "http://bard.broadinstitute.org/adf#";
    static final String internalBardOntology = "http://bard.broadinstitute.org/bard#";
    static final String publicBardOntology = "http://www.bard.nih.gov/ontology/bard#"
    ValueFactory factory;

    URI definedBy;
    URI hasDocument;
    URI hasContent;
    URI hasContext;
    URI hasItem;
    URI hasAttribute;
    URI hasValue;
    URI hasQualifier;
    URI hasNumericValue;
    URI hasMaxValue;
    URI hasMinValue;
    URI hasExperiment;
    URI identifiedBy;
    URI hasContextType;

    URI documentClass;
    URI experimentClass;
    URI projectClass;
    URI itemClass;
    URI contextClass;
    URI assayClass;

    URI hasExternalOnto
    URI hasExternalOntoId

    URI hasValueType
    URI hasAttributeType
    URI hasValueDisplay
    URI hasDocumentType

    URI hasExternalSystem
    URI hasExternalRef

    AbstractService() {
        factory = ValueFactoryImpl.getInstance()

        definedBy = factory.createURI(adfOntology, "definedBy");
        hasDocument = factory.createURI(adfOntology, "hasDocument");
        hasContent = factory.createURI(adfOntology, "hasContent");
        hasItem = factory.createURI(adfOntology, "hasItem");
        hasExperiment = factory.createURI(adfOntology, "hasExperiment");
        hasAttribute = factory.createURI(adfOntology, "hasAttribute");
        hasValue = factory.createURI(adfOntology, "hasValue");
        hasQualifier = factory.createURI(adfOntology, "hasQualifier");
        hasNumericValue = factory.createURI(adfOntology, "hasNumericValue");
        hasMaxValue = factory.createURI(adfOntology, "hasMaxValue");
        hasMinValue = factory.createURI(adfOntology, "hasMinValue");
        identifiedBy = factory.createURI(adfOntology, "identifiedBy");
        hasContextType = factory.createURI(adfOntology, "hasContextType");
        hasContext = factory.createURI(adfOntology, "hasContext");

        documentClass = factory.createURI(adfOntology, "Document");
        contextClass = factory.createURI(adfOntology, "Context");
        itemClass = factory.createURI(adfOntology, "Item");
        experimentClass = factory.createURI(adfOntology, "Experiment");
        assayClass = factory.createURI(adfOntology, "Assay");
        projectClass = factory.createURI(adfOntology, "Project");

        hasExternalOnto = factory.createURI(adfOntology, "hasExternalOnto");
        hasExternalOntoId = factory.createURI(adfOntology, "hasExternalOntoId")

        hasValueType = factory.createURI(adfOntology, "hasValueType")
        hasAttributeType = factory.createURI(adfOntology, "hasAttributeType")
        hasValueDisplay =  factory.createURI(adfOntology, "hasValueDisplay")
        hasDocumentType = factory.createURI(adfOntology, 'hasDocumentType')
        hasExternalSystem = factory.createURI(adfOntology, 'hasExternalSystem')
        hasExternalRef = factory.createURI(adfOntology, 'hasExternalRef')
    }
}
