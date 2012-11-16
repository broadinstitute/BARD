package bard.core.interfaces;

import bard.core.*;

import java.io.Serializable;
import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.Map;


public interface EntityService<E extends Entity> extends Serializable {
    final int MAXIMUM_NUMBER_OF_COMPOUNDS = 500;

    //relative path to the experiment resource
    final String EXPERIMENTS_RESOURCE = "/experiments";
    //relative path to the assays resource
    final String ASSAYS_RESOURCE = "/assays";
    //relative path to the projects resource
    final String PROJECTS_RESOURCE = "/projects";
    //relative path to the compounds resource
    final String COMPOUNDS_RESOURCE = "/compounds";
    final String PROMISCUITY_RESOURCE = "/plugins/badapple/prom/cid";
    final String FILTER_ACTIVE = "&filter=active";
    final String SYNONYMS = "/synonyms";
    final int HTTP_SUCCESS_CODE = 200;
    final int DEFAULT_BUFSIZ = HTTP_SUCCESS_CODE;
    final String QUERY_STRING = "query";
    final String UTF_8 = "utf-8";
    final String FORWARD_SLASH = "/";
    final String META_DATA = "metaData";
    final String NHIT = "nhit";
    final String FACETS = "facets";
    final String FACET_NAME = "facetName";
    final String COUNTS = "counts";
    final String SEARCH = "search";
    final String IDS = "ids";
    final String COLLECTION = "collection";
    final String DOCS = "docs";
    final String HIT_COUNT_ = "_HitCount_";
    final String NAME = "name";
    final String ETAG = "etag";
    final String E_TAG = "ETag";
    final String COMMA = ",";
    final String _COUNT = "_count";
    final String SUGGEST = "suggest";
    final String TOP = "&top=";
    final String FILTER = "&filter=";
    final String EXPAND_TRUE = "expand=true";
    final String AMPERSAND = "&";
    final String SOLR_QUERY_PARAM_NAME = "q=";
    final String QUESTION_MARK = "?";
    final String COLON = ":";
    final String RIGHT_PAREN = ")";
    final String FQ = "fq";
    final String LEFT_PAREN = "(";
    final String SKIP = "skip=";
    final String ETAG_ID = "etag_id";
    final String COUNT = "count";
    final String URL_STRING = "url";
    final String DESCRIPTION = "description";
    String APPROVED_DRUGS = "Approved drugs";
    String FDA_APPROVED = "FDA approved";
    String ANNO_KEY = "anno_key";
    String ANNO_VAL = "anno_val";
    String SUBSTANCES_RESOURCE = "/substances";
    String TITLE = "title";
    String DOI = "doi";
    String ABS = "abs";
    String PUBLICATIONS = "publications";
    String CAP_ASSAY_ID = "capAssayId";
    String BARD_ASSAY_ID = "bardAssayId";
    String PROTOCOL = "protocol";
    String COMMENTS = "comments";
    String CATEGORY = "category";
    String TYPE = "type";
    String CLASSIFICATION = "classification";
    String SOURCE = "source";
    String GRANT_NO = "grantNo";
    String AID = "aid";
    String ACC = "acc";
    String GENE_ID = "geneId";
    String TAXONOMY_ID = "TaxonomyID";
    String TAX_ID = "taxId";
    String TARGETS = "targets";
    String ASSAY_ID = "assay_id";
    String COMMENT = "comment";
    String HIGHLIGHT = "highlight";
    String AK_DICT_LABEL = "ak_dict_label";
    String AV_DICT_LABEL = "av_dict_label";
    String GOBP_ID = "gobp_id";
    String GOBP_TERM = "gobp_term";
    String GOMF_ID = "gomf_id";
    String GOMF_TERM = "gomf_term";
    String GOCC_ID = "gocc_id";
    String GOCC_TERM = "gocc_term";
    String KEGG_DISEASE_CAT = "kegg_disease_cat";
    String KEGG_DISEASE_NAMES = "kegg_disease_names";
    String PUBMED_ID = "pubmedId";
    String PROBE_ID = "probeId";
    String IUPAC_NAME = "iupacName";
    String SIDS = "sids";
    String CID = "cid";
    String PREFERRED_TERM = "preferred_term";
    String ISO_SMILES = "iso_smiles";
    String SUBSTANCES = "substances";
    String COMPOUNDS = "compounds";
    String PUBCHEM_AID = "pubchemAid";
    String EXPT_ID = "exptId";
    String OUTCOME = "outcome";
    String POTENCY = "potency";
    String READOUTS = "readouts";
    String SID = "sid";
    String EID = "eid";
    String EXPT_DATA_ID = "exptDataId";
    String CR = "cr";
    String CONC_UNIT = "concUnit";
    String AC_50 = "ac50";
    String HILL = "hill";
    String S_INF = "sInf";
    String S_0 = "s0";
    String EXPTDATA_RESOURCE = "/exptdata";
    String PROJECT_ID = "projectId";
    String EXPERIMENT_COUNT = "experimentCount";
    String PROJ_ID = "proj_id";
    String NUM_EXPT = "num_expt";
    String PROBES = "probes";
    String SMILES = "smiles";

    /*
    * Service metadata
    */
    Class<E> getEntityClass();

    EntityServiceManager getServiceManager();

    // is this service read-only? if so all write operations (e.g., put, ETag)
    //  will throw UnsupportedOperationException
    boolean isReadOnly();

    long size();

    /*
     * Return a unique data source based on name and version
     */
    DataSource getDataSource(); // return default data source

    DataSource getDataSource(String name);

    DataSource getDataSource(String name, String version);

    /*
     * The distinction between get() and iterator is that get() is meant
     * for small data retrieval. As such, each entity returned by get() is
     * fully constructed whereas that's not necessary the case for 
     * iterator().
     */
    E get(Object id);

    Collection<E> get(Collection ids);

    List<E> get(long top, long skip);

    // format for ordering is FIELD [DESC|ASC]
    List<E> get(long top, long skip, String ordering);

    /*
     * 
     */
    // void put (E... entities);
    //void remove (E... entities);


    /*
     * ETag's
     */
    Object newETag(String name);

    Object newETag(String name, Collection ids);

    int putETag(Object etag, Collection ids);

    // return facets based on given etag
    Collection<Value> getFacets(Object etag);

    // return all known etags that are accessible by the
    //  given principal
    SearchResult<Value> etags(Principal principal);

    /*
     * retrieval of related entities
     */
    <T extends Entity> SearchResult<T> searchResult(E entity, Class<T> clazz);

    /*
     * Various entity retrieval methods
     */
    SearchResult<E> searchResult();

    SearchResult<E> searchResult(Object etag);

    SearchResult<E> searchResult(ServiceParams params);

    /*
     * Search & filtering
     */
    SearchResult<E> filter(FilterParams params);

    SearchResult<E> search(SearchParams params);

    // suggestion
    Map<String, List<String>> suggest(SuggestParams params);

    /*
     * clean up
     */
    void shutdown();
}
