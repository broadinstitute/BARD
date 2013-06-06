package bard.core.interfaces;

public interface RestApiConstants {
    final String QUESTION_MARK = "?";
    final String AMPERSAND = "&";
    final String STEPS = "/steps";
    final String STRUCTURE = "[structure]";
    final String TYPE_SUB = AMPERSAND + "type=sub";
    final String TYPE_SUP = AMPERSAND + "type=sup";
    final String TYPE_EXACT = AMPERSAND + "type=exact";
    final String TYPE_SIM = AMPERSAND + "type=sim";
    final String CUTOFF = AMPERSAND + "cutoff=";
    final String FILTER = "filter=";
    final String ACTIVE = "active";

    final String FILTER_QUESTION = QUESTION_MARK + FILTER;

    final String AMPERSAND_FILTER = AMPERSAND + FILTER;

    final String FILTER_ACTIVE = AMPERSAND_FILTER + ACTIVE;
    final int MAXIMUM_NUMBER_OF_COMPOUNDS = 500;
    final int MAXIMUM_NUMBER_OF_HISTOGRAM_BARS = 40;
    final int MAXIMUM_NUMBER_OF_EXPERIMENTS = 1000;
    final String ANNOTATIONS = "/annotations";
    final String SUBSTANCES_RESOURCE = "/substances";
    //for targets resource
    final String TARGETS_RESOURCE ="/targets";

    final String ACCESSION ="/accession";

    final String CLASSIFICATION ="/classification";

    //relative path to the experiment resource
    final String EXPERIMENTS_RESOURCE = "/experiments";
    //relative path to the biology resource
    final String BIOLOGY_RESOURCE = "/biology";
    //relative path to the result types, as used to request histogram data
    final String RESULT_TYPES = "/resulttypes";
    final String COLLAPSE_RESULTS = "collapse=";


    //relative path to the assays resource
    final String ASSAYS_RESOURCE = "/assays";

    //relative path to the assays resource
    final String DICTIONARY_RESOURCE = "/cap/dictionary";
    //relative path to the projects resource
    final String PROJECTS_RESOURCE = "/projects";
    //relative path to the compounds resource
    final String COMPOUNDS_RESOURCE = "/compounds";

    final String SYNONYMS = "/synonyms";
    final String UTF_8 = "utf-8";
    final String FORWARD_SLASH = "/";
    final String FACETS = "facets";
    final String SEARCH = "search";
    final String NAME = "name";
    final String ETAGS_RESOURCE="/etags";
    final String ETAG = "etag";
    final String E_TAG = "ETag";
    final String COMMA = ",";
    final String _COUNT = "_count";
    final String SUGGEST = "suggest";
    final String TOP = AMPERSAND + "top=";
    final String EXPAND_TRUE = "expand=true";
    final String SOLR_QUERY_PARAM_NAME = "q=";
    final String EXPTDATABYIDS_RESOURCE = "/getExptDataByIds";

    final String SUMMARY = "/summary";
    final String COLON = ":";
    final String RIGHT_PAREN = ")";
    final String FQ = "fq";
    final String LEFT_PAREN = "(";
    final String SKIP = "skip=";
    final String TITLE = "title";
    final String TYPE = "type";
    final String CID = "cid";
    final String EXPTDATA_RESOURCE = "/exptdata";
    final String PROBEID = "/probeid";
    final String SMILES = "smiles";
}
