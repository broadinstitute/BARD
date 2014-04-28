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

package bard.core.util

class MatchedTermsToHumanReadableLabelsMapper {

    private static Map matchedTermsToHumanReadableLabelsMap = [
            "accession": "Uniprot Accession",
            "ak_dict_label": "Dictionary Key",
            "assay_component_role": "Assay Component Role",
            "assay_type": "Assay Type",
            "av_dict_label": "Dictionary Value",
            "bardAssayId": "do not use",
            "capAssayId": "Assay Definition ID",
            "class_descr": "Panther class description",
            "class_name": "Panther class name",
            "comment": "Comment",
            "description": "Description",
            "detection_method_type": "Detection Method Type",
            "gobp_id": "GO Biological Process ID",
            "gobp_term": "GO Biological Process Term",
            "gocc_id": "GO Cellular Component ID",
            "gocc_term": "GO Cellular Component Term",
            "gomf_id": "GO Molecular Function ID",
            "gomf_term": "GO Molecular Function Term",
            "name": "Name",
            "protocol": "Protocol",
            "pub_abstract": "Publication Abstract",
            "pub_doi": "Publication DOI",
            "pub_title": "Publication Title",
            "target_gene_id": "Target Gene ID",
            "target_name": "Target Name",
            "timestamp": "Timestamp",
            "anno_key": "Annotation Key",
            "anno_val": "Annotation Value",
            "cid": "PubChem CID",
            "COLLECTION": "Compound Collection",
            "compoundClass": "Compound Class",
            "iupacName": "IUPAC Name",
            "mwt": "Molecular Weight",
            "probeId": "Probe ID",
            "smiles": "SMILES",
            "tpsa": "TPSA",
            "xlogp": "xLogP",
            "capProjectId": "Project ID",
            "kegg_disease_cat": "KEGG Disease",
            "kegg_disease_names": "KEGG Disease Names",
            "num_expt": "Number of Experiments",
            "projectId": "do not use"
    ]


    public static String matchTermsToHumanReadableLabels(String term) {
        if (matchedTermsToHumanReadableLabelsMap.containsKey(term)) {
            return matchedTermsToHumanReadableLabelsMap[term]
        }

        return term
    }
}
