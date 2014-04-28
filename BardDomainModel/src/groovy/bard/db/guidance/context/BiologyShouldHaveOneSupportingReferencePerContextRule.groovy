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

package bard.db.guidance.context

import bard.db.guidance.DefaultGuidanceImpl
import bard.db.guidance.Guidance
import bard.db.guidance.GuidanceAware
import bard.db.guidance.GuidanceRule
import bard.db.model.AbstractContext
import bard.db.model.AbstractContextItem

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/9/13
 * Time: 2:57 PM
 * For any given Context,
 *   if the context contains an item with a 'biology' based on the value associated with biology
 *   then there should be a another contextItem in the context with a supporting reference
 *   So biological process should try and reference specific biological process term
 *   or a molecular target should try and reference a specifc molecular target term
 */
class BiologyShouldHaveOneSupportingReferencePerContextRule implements GuidanceRule {

    private static final String BIOLOGY_LABEL = 'biology'
    private static final List<String> BIOLOGICAL_PROCESS_LABELS = ['biological process']
    private static final List<String> BIOLOGICAL_PROCESS_TERMS = [
            'GO biological process term',
            'NCBI BioSystems term'
    ]

    private static final List<String> MOLECULAR_TARGET_LABELS = [
            'molecular interaction',
            'molecular target',
            'macromolecule',
            'antibody',
            'gene',
            'nucleotide',
            'peptide',
            'protein-DNA complex',
            'protein-reporter complex',
            'protein'
    ]
    private static final List<String> MOLECULAR_TARGET_TERMS = [
            'GO gene-product ID',
            'GenBank ID',
            'NCBI accession number',
            'UniProt accession number',
            'gene Entrez GI',
            'protein Entrez GI'
    ]


    private static final String BIOLOGICAL_PROCESS_SHOULD_HAVE_ONE_BIOLOGICAL_PROCESS_TERM_MSG = "When a context has biology defined as a 'biological process', there should be one other item that references one of the following terms ( ${BIOLOGICAL_PROCESS_TERMS.join(',')} )."
    private static final String MOLECULAR_TARGETS_SHOULD_HAVE_ONE_BIOLOGICAL_PROCESS_TERM_MSG = "When a context has biology defined as a something that can be considered a molecular target,  there should one other item that references one of the following terms ( ${MOLECULAR_TARGET_TERMS.join(',')} )."

    AbstractContext context

    BiologyShouldHaveOneSupportingReferencePerContextRule(AbstractContext context) {
        this.context = context
    }

    @Override
    List<Guidance> getGuidance() {
        List<Guidance> guidanceList = []

        // find first item with 'biology' = 'biological process'
        final List<AbstractContextItem> biologicalProcessItems = context.contextItems.findAll { AbstractContextItem item ->
            item.attributeElement.label == BIOLOGY_LABEL && item.valueElement?.label in BIOLOGICAL_PROCESS_LABELS
        }
        // find first item with 'biology' = 'biological process'
        final List<AbstractContextItem> molecularTargetItems = context.contextItems.findAll { AbstractContextItem item ->
            item.attributeElement.label == BIOLOGY_LABEL && item.valueElement?.label in MOLECULAR_TARGET_LABELS
        }
        // if  the biology is a biological process 'biology' = 'biological process'
        if (biologicalProcessItems) {
            final List<AbstractContextItem> biologicalProcessTerms = context.contextItems.findAll { AbstractContextItem item ->
                item.attributeElement.label in BIOLOGICAL_PROCESS_TERMS
            }
            if (biologicalProcessTerms.size() != 1) {
                guidanceList.add(new DefaultGuidanceImpl(BIOLOGICAL_PROCESS_SHOULD_HAVE_ONE_BIOLOGICAL_PROCESS_TERM_MSG))
            }
        } else if (molecularTargetItems) { // else if  the biology is a molecular target e.g. 'biology' = 'molecular interaction'
            final List<AbstractContextItem> molecularTargetTerms = context.contextItems.findAll { AbstractContextItem item ->
                item.attributeElement.label in MOLECULAR_TARGET_TERMS
            }
            if (molecularTargetTerms.size() != 1) {
                guidanceList.add(new DefaultGuidanceImpl(MOLECULAR_TARGETS_SHOULD_HAVE_ONE_BIOLOGICAL_PROCESS_TERM_MSG))
            }
        }

        return guidanceList
    }
}
