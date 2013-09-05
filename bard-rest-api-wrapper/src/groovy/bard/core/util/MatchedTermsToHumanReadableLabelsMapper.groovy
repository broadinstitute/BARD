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
