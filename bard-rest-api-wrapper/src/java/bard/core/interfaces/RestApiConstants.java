package bard.core.interfaces;

public interface RestApiConstants {
    final String STRUCTURE = "[structure]";
    final String TYPE_SUB = "&type=sub";
    final String TYPE_SUP = "&type=sup";
    final String TYPE_EXACT = "&type=exact";
    final String TYPE_SIM = "&type=sim";
    final String CUTOFF = "&cutoff=";
    final String FILTER_QUESTION = "?filter=";
    final String FILTER = "&filter=";

    final int MAXIMUM_NUMBER_OF_COMPOUNDS = 500;
    final int MAXIMUM_NUMBER_OF_EXPERIMENTS = 1000;
    final String ANNOTATIONS = "/annotations";

    //relative path to the experiment resource
    final String EXPERIMENTS_RESOURCE = "/experiments";
    //relative path to the assays resource
    final String ASSAYS_RESOURCE = "/assays";
    //relative path to the projects resource
    final String PROJECTS_RESOURCE = "/projects";
    //relative path to the compounds resource
    final String COMPOUNDS_RESOURCE = "/compounds";
    final String FILTER_ACTIVE = "&filter=active";
    final String SYNONYMS = "/synonyms";
    final String UTF_8 = "utf-8";
    final String FORWARD_SLASH = "/";
    final String FACETS = "facets";
    final String SEARCH = "search";
    final String NAME = "name";
    final String ETAG = "etag";
    final String E_TAG = "ETag";
    final String COMMA = ",";
    final String _COUNT = "_count";
    final String SUGGEST = "suggest";
    final String TOP = "&top=";
    final String EXPAND_TRUE = "expand=true";
    final String AMPERSAND = "&";
    final String SOLR_QUERY_PARAM_NAME = "q=";
    final String QUESTION_MARK = "?";
    final String COLON = ":";
    final String RIGHT_PAREN = ")";
    final String FQ = "fq";
    final String LEFT_PAREN = "(";
    final String SKIP = "skip=";
    final String TITLE = "title";
    final String TYPE = "type";
    final String CID = "cid";
    final String EXPTDATA_RESOURCE = "/exptdata";
    final String SMILES = "smiles";
}
