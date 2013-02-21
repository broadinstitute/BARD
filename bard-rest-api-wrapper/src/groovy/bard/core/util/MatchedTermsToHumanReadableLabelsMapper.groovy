package bard.core.util

class MatchedTermsToHumanReadableLabelsMapper {

    private static Map matchedTermsToHumanReadableLabelsAssays = [
            "accession": "Uniprot Accession",
            "ak_dict_label": "",
            "assay_component_role": "Assay Component Role",
            "assay_type": "Assay Type",
            "av_dict_label": "",
            "bardAssayId": "do not use",
            "capAssayId": "Assay Definition ID",
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
            "timestamp": "Timestamp"
    ]

    private static Map matchedTermsToHumanReadableLabelsCompounds = [
            "anno_key": "Annotation Key",
            "anno_val": "Annotation Value",
            "cid": "PubChem CID",
            "COLLECTION": "Compound Collection",
            "compoundClass": "Compound Class",
            "iupacName": "IUPAC Name",
            "mwt": "Molecular Weight",
            "name": "Name",
            "probeId": "Probe ID",
            "smiles": "SMILES",
            "timestamp": "Timestamp",
            "tpsa": "TPSA",
            "xlogp": "xLogP"
    ]

    private static Map matchedTermsToHumanReadableLabelsProjects = [
            "ak_dict_label": "",
            "av_dict_label": "",
            "capProjectId": "Project ID",
            "description": "Description",
            "gobp_id": "GO Biological Process ID",
            "gobp_term": "GO Biological Process Term",
            "gocc_id": "GO Cellular Component ID",
            "gocc_term": "GO Cellular Component Term",
            "gomf_id": "GO Molecular Function ID",
            "gomf_term": "GO Molecular Function Term",
            "kegg_disease_cat": "KEGG Disease",
            "kegg_disease_names": "KEGG Disease Names",
            "name": "Name",
            "num_expt": "Number of Experiments",
            "projectId": "do not use",
            "target_gene_id": "Target Gene ID",
            "target_name": "Target Name"
    ]

    private static Map adapterClass = [
            'AssayAdapter': matchedTermsToHumanReadableLabelsAssays,
            'CompoundAdapter': matchedTermsToHumanReadableLabelsCompounds,
            'ProjectAdapter': matchedTermsToHumanReadableLabelsProjects
    ]

    public static String matchTermsToHumanReadableLabels(String term, Class clazz) {
        Map matchingMap = this.adapterClass[clazz.simpleName]

        if (matchingMap.containsKey(term)) {
            String humanReadableLabel = matchingMap[term]

            return humanReadableLabel ?: "No mapping found for term: '${term}'"
        }

        return term
    }
}
