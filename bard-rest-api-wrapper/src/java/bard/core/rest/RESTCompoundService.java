package bard.core.rest;

import bard.core.*;
import bard.core.StringValue;
import bard.core.impl.MolecularDataJChemImpl;
import bard.core.interfaces.CompoundService;
import bard.core.interfaces.EntityNamedSources;
import bard.core.interfaces.MolecularData;
import bard.core.interfaces.SearchResult;
import bard.core.rest.spring.compounds.PromiscuityScore;
import bard.core.rest.spring.util.StructureSearchParams;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;


public class RESTCompoundService extends RESTAbstractEntityService<Compound>
        implements CompoundService {
    //this is thread safe
    //  final XStream xstream;

    public RESTCompoundService
            (String baseURL) {
        super(baseURL);
        //initialize xstream
//        this.xstream = new XStream();
//        //step up deserialization. Read XStream docs if you httpGet confused here
//        this.xstream.alias("compound", PromiscuityScore.class);
//        this.xstream.alias("hscaf", Scaffold.class);
//        this.xstream.addImplicitCollection(PromiscuityScore.class, "scaffolds");
    }

    public String getResourceContext() {
        return COMPOUNDS_RESOURCE;
    }



//    public List<PromiscuityScore> getPromiscuityScores(List<Long> cids) {
//        DefaultHttpClient httpclient = new DefaultHttpClient();
//        List<PromiscuityScore> results = new ArrayList<PromiscuityScore>();
//        InputStream is = null;
//        try {
//            final List<NameValuePair> nvp = new ArrayList<NameValuePair>();
//            nvp.add(new BasicNameValuePair(IDS, toString(cids)));
//            final String uri = getPromiscuityResource();
//            final HttpPost post = new HttpPost(uri);
//            post.addHeader("Accept", "application/xml");
//            post.setEntity(new UrlEncodedFormEntity(nvp, HTTP.UTF_8));
//
//            final HttpResponse response = httpclient.execute(post);
//            final HttpEntity entity = response.getEntity();
//            if (entity != null) {
//                is = entity.getContent();
//                return (List<PromiscuityScore>) xstream.fromXML(is);
//            }
//        } catch (IOException ex) {
//            log.error(ex);
//        } finally {
//            closeInputStream(is);
//            httpclient.getConnectionManager().shutdown();
//        }
//        return results;
//
//    }
    //TODO: Redundant since v9
    protected String buildQueryForTestedAssays(final Compound compound,
                                               final boolean activeOnly) {
        StringBuilder url = new StringBuilder();
        url.append(getResource(compound.getId())).append(ASSAYS_RESOURCE).append(QUESTION_MARK).append(EXPAND_TRUE);
        if (activeOnly) {
            url.append(FILTER_ACTIVE);
        }
        return url.toString();

    }

    protected List<Assay> jsonArrayNodeToAssays(final ArrayNode array, final RESTAssayService restAssayService) {
        final List<Assay> assays = new ArrayList<Assay>();
        for (int i = 0; i < array.size(); ++i) {
            JsonNode n = array.get(i);
            Assay assay = jsonNodeToAssay(n, restAssayService);
            if (assay != null) {
                assays.add(assay);
            }
        }
        return assays;
    }

    protected Assay jsonNodeToAssay(final JsonNode jsonNode, final RESTAssayService restAssayService) {
        if (isNotNull(jsonNode)) {
            return restAssayService.getEntity(null, jsonNode);
        }
        return null;
    }

    protected final void addSynonyms(final List<Value> synonyms, final JsonNode node, final DataSource ds) {
        if (isNotNull(node) && node.isArray()) {
            final ArrayNode array = (ArrayNode) node;
            for (int i = 0; i < array.size(); ++i) {
                JsonNode n = array.get(i);
                addSynonym(synonyms, n, ds);
            }
        }
    }

    protected final void addSynonym(final List<Value> synonyms, final JsonNode node, final DataSource ds) {
        if (isNotNull(node)) {
            synonyms.add(new StringValue
                    (ds, Compound.SynonymValue,
                            node.asText()));
        }
    }



    protected void addETagsToHTTPHeader(HttpGet httpGet, final Map<String, Long> etags) {
        if (etags != null && !etags.isEmpty()) {
            String etag = getParentETag(etags);
            httpGet.addHeader("If-Match", "\"" + etag + "\"");
        }
    }

    protected void extractETagFromResponseHeader(final HttpResponse response, long skip, Map<String, Long> etags) {
        if (response.containsHeader(E_TAG) && etags != null) {
            for (Header h : response.getHeaders(E_TAG)) {
                int len = h.getValue().length();
                if (len > 2) {
                    String e = h.getValue().substring(1, len - 1);
                    etags.put(e, skip);
                }
            }
        }
    }



    protected void addCompoundName(final Compound compound, final JsonNode node) {
        final JsonNode n = node.get(NAME);
        if (isNotNull(n)) {
            compound.setName(n.asText());
        }

    }




    protected void addMolecularData(final Compound compound, final JsonNode node) {
        final DataSource ds = getDataSource();
        final MolecularData md = new MolecularDataJChemImpl();
        final JsonNode iso_smiles = node.get(ISO_SMILES);
        if (isNotNull(iso_smiles)) {
            md.setMolecule(iso_smiles.asText());
        } else {
            md.setMolecule(node);
        }
        compound.addValue(new MolecularValue(ds, Compound.MolecularValue, md));
    }

    protected void addCompoundCID(final Compound compound, final JsonNode node) {
        final JsonNode n = node.get(CID);
        if (isNotNull(n)) {
            final DataSource ds = getDataSource();
            final long cid = n.asLong();
            compound.setId(cid);
            compound.addValue(new LongValue(ds, Compound.PubChemCIDValue, cid));
        }
    }

    protected void addIupacName(Compound compound, JsonNode node) {
        final JsonNode n = node.get(IUPAC_NAME);
        if (isNotNull(n)) {
            DataSource ds = getDataSource();
            compound.addValue(new StringValue(ds, Compound.IUPACNameValue, n.asText()));
            if (StringUtils.isBlank(compound.getName())) {
                compound.setName(n.asText());
            }
        }
    }

    protected void addPreferredTerm(Compound compound, JsonNode node) {
        final JsonNode n = node.get(PREFERRED_TERM);
        if (isNotNull(n)) {
            compound.setName(n.asText());
        }
    }

    protected void addHighlight(Compound compound, JsonNode node) {
        final JsonNode n = node.get(HIGHLIGHT);
        if (isNotNull(n)) {
            DataSource ds = getDataSource();
            compound.addValue(new StringValue
                    (ds, Compound.SearchHighlightValue, n.asText()));
        }
    }

  //============ TODO: Methods to change
  protected List<Compound> search(String resource, long top, long skip) {
      return search(resource, top, skip, null);
  }
    //TODO: Moved
    public SearchResult<Compound> structureSearch
    (StructureSearchParams params) {
        return new StructureSearchResult(this, params).build();
    }

    static String getParentETag(Map<String, Long> etags) {
        String mintag = "";
        Long minval = null;
        for (Map.Entry<String, Long> me : etags.entrySet()) {
            if (minval == null || minval > me.getValue()) {
                mintag = me.getKey();
                minval = me.getValue();
            }
        }
        return mintag;
    }

    /**
     * @param cid -
     * @return {@link bard.core.rest.spring.compounds.PromiscuityScore}
     */
    //TODO: Moved
    public PromiscuityScore getPromiscuityScore(Long cid) {
        final String url = getPromiscuityResource(cid);
        DefaultHttpClient httpClient = new DefaultHttpClient();
        InputStream is = null;
        try {
            HttpGet get = new HttpGet(url);
            get.addHeader("Accept", "application/xml");

            HttpResponse response = httpClient.execute(get);

            HttpEntity entity = response.getEntity();
            if (entity != null
                    && response.getStatusLine().getStatusCode() == 200) {
                is = entity.getContent();
                //  return (PromiscuityScore) xstream.fromXML(is);
            }
        } catch (IOException e) {
            log.error(e);
        } finally {
            closeInputStream(is);
            httpClient.getConnectionManager().shutdown();
        }
        return null;

    }
  //TODO: Moved
  public Collection<Value> getSynonyms(Compound compound) {
      final List<Value> synonyms = new ArrayList<Value>();
      final String url = getResource(compound.getId() + SYNONYMS);
      final DataSource ds = getDataSource
              (EntityNamedSources.PubChemSynonymSource);
      ds.setURL(url);
      final JsonNode node = executeGetRequest(url);
      addSynonyms(synonyms, node, ds);
      return synonyms;
  }
  //TODO: Moved
  protected String buildResourceURL(final String resource,
                                    final long top,
                                    final long skip) {
      final StringBuilder stringBuilder =
              new StringBuilder().
                      append(resource).
                      append(TOP).
                      append(top).
                      append(AMPERSAND).
                      append(SKIP).
                      append(skip);
      return stringBuilder.toString();
  }

    protected void addSIDs(final Compound compound, final JsonNode node) {
        final JsonNode n = node.get(SIDS);
        if (isNotNull(n) && n.isArray()) {
            final DataSource ds = getDataSource();
            final ArrayNode nodes = (ArrayNode) n;
            for (int i = 0; i < nodes.size(); ++i) {
                long sid = nodes.get(i).asLong();
                compound.addValue(new LongValue(ds, Compound.PubChemSIDValue, sid));
            }
        }
    }

    protected void addProbe(Compound compound, JsonNode node) {
        final JsonNode n = node.get(PROBE_ID);
        if (isNotNull(n)) {
            DataSource ds = getDataSource();
            compound.addValue(new StringValue(ds, Compound.ProbeIDValue,
                    n.asText()));

        }
    }
    protected List<Compound> search(String resource, long top,
                                    long skip, Map<String, Long> etags) {

        final List<Compound> results = new ArrayList<Compound>();
        final DefaultHttpClient httpclient = new DefaultHttpClient();

        try {
            final String url = buildResourceURL(resource, top, skip);
            HttpGet httpGet = new HttpGet(url);
            addETagsToHTTPHeader(httpGet, etags);


            HttpResponse response = httpclient.execute(httpGet);
            extractETagFromResponseHeader(response, skip, etags);

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream is = entity.getContent();
                try {
                    ArrayNode node = (ArrayNode) this.mapper.readTree(is);
                    for (int i = 0; i < node.size(); ++i) {
                        JsonNode n = node.get(i);
                        results.add(getEntity(null, n));
                    }
                } catch (Exception ex) {
                    log.error(ex);
                } finally {
                    is.close();
                }
            }
        } catch (IOException ex) {
            log.error(ex);
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        return results;
    }
    protected void addAnnotationsToCompound(Compound compound, JsonNode node) {
        final DataSource ds = getDataSource(EntityNamedSources.AnnotationSource);
        final ArrayNode keys = (ArrayNode) node.get(ANNO_KEY);
        final ArrayNode vals = (ArrayNode) node.get(ANNO_VAL);
        if (isNotNull(keys) && isNotNull(vals)) {
            for (int i = 0; i < keys.size(); ++i) {
                String key = keys.get(i).asText();
                String val = vals.get(i).asText();
                if (key.equalsIgnoreCase(COLLECTION) && (val.equalsIgnoreCase(APPROVED_DRUGS) || val.equalsIgnoreCase(FDA_APPROVED))) {
                    compound.setDrug(true);
                }
                compound.addValue(new StringValue(ds, key, val));
            }
        }
    }



    protected Compound getEntity(Compound compound, JsonNode node) {
        if (compound == null) {
            compound = new Compound();
        }
        addCompoundCID(compound, node);
        addCompoundName(compound, node);
        addSIDs(compound, node);
        addProbe(compound, node);
        addIupacName(compound, node);

        addAnnotationsToCompound(compound, node);
        // we should be getting mol format instead of smiles!
        addMolecularData(compound, node);
        return compound;
    }


    protected Compound getEntitySearch(Compound compound, JsonNode node) {
        if (compound == null) {
            compound = new Compound();
        }
        addCompoundCID(compound, node);
        addIupacName(compound, node);
        addProbe(compound, node);
        addPreferredTerm(compound, node);
        addHighlight(compound, node);
        addMolecularData(compound, node);
        addAnnotationsToCompound(compound, node);
        return compound;
    }

    public SearchResult<Compound> search(SearchParams params) {
        if (params instanceof StructureSearchParams) {
            return structureSearch((StructureSearchParams) params);
        }
        return super.search(params);
    }
}
