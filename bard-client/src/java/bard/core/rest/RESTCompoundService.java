package bard.core.rest;

import bard.core.*;
import bard.core.StringValue;
import bard.core.impl.MolecularDataJChemImpl;
import bard.core.interfaces.CompoundService;
import bard.core.interfaces.EntityNamedSources;
import bard.core.interfaces.MolecularData;
import bard.core.interfaces.SearchResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.thoughtworks.xstream.XStream;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;


public class RESTCompoundService extends RESTAbstractEntityService<Compound>
        implements CompoundService {
    //this is thread safe
    final XStream xstream;

    protected RESTCompoundService
            (RESTEntityServiceManager srvman, String baseURL) {
        super(srvman, baseURL);
        //initialize xstream
        this.xstream = new XStream();
        //step up deserialization. Read XStream docs if you get confused here
        this.xstream.alias("compound", PromiscuityScore.class);
        this.xstream.alias("hscaf", Scaffold.class);
        this.xstream.addImplicitCollection(PromiscuityScore.class, "scaffolds");
    }

    public Class<Compound> getEntityClass() {
        return Compound.class;
    }

    public String getResourceContext() {
        return COMPOUNDS_RESOURCE;
    }

    public SearchResult<Compound> structureSearch
            (StructureSearchParams params) {
        return new StructureSearchResult(this, params).build();
    }

    /**
     * @param cid -
     * @return {@link PromiscuityScore}
     */
    public PromiscuityScore getPromiscuityScore(Long cid) {
        final String url = getPromiscuityResource() + cid;
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
                return (PromiscuityScore) xstream.fromXML(is);
            }
        } catch (IOException e) {
            //e.printStackTrace();
        } finally {
            closeInputStream(is);
            httpClient.getConnectionManager().shutdown();
        }
        return null;

    }

    public List<PromiscuityScore> getPromiscuityScores(List<Long> cids) {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        List<PromiscuityScore> results = new ArrayList<PromiscuityScore>();
        InputStream is = null;
        try {
            final List<NameValuePair> nvp = new ArrayList<NameValuePair>();
            nvp.add(new BasicNameValuePair(IDS, toString(cids)));
            final String uri = getPromiscuityResource();
            final HttpPost post = new HttpPost(uri);
            post.addHeader("Accept", "application/xml");
            post.setEntity(new UrlEncodedFormEntity(nvp, HTTP.UTF_8));

            final HttpResponse response = httpclient.execute(post);
            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                is = entity.getContent();
                return (List<PromiscuityScore>) xstream.fromXML(is);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            closeInputStream(is);
            httpclient.getConnectionManager().shutdown();
        }
        return results;

    }

    protected String buildQueryForTestedAssays(final Compound compound,
                                               final boolean activeOnly) {
        StringBuilder url = new StringBuilder();
        url.append(getResource(compound.getId())).append(ASSAYS_RESOURCE).append(QUESTION_MARK).append(EXPAND_TRUE);
        if (activeOnly) {
            url.append(FILTER_ACTIVE);
        }
        return url.toString();

    }


    public Collection<Assay> getTestedAssays(Compound compound,
                                             boolean activeOnly) {
        List<Assay> assays = new ArrayList<Assay>();
        RESTAssayService as = getServiceManager().getService(Assay.class);

        String url = buildQueryForTestedAssays(compound, activeOnly);
        final JsonNode rootNode = executeGetRequest(url);
        if (isNotNull(rootNode)) {
            JsonNode node = rootNode.get(COLLECTION);
            if (isNotNull(node) && node.isArray()) {
                ArrayNode array = (ArrayNode) node;
                for (int i = 0; i < array.size(); ++i) {
                    JsonNode n = array.get(i);
                    if (n != null && !n.isNull()) {
                        Assay a = new Assay();
                        as.getEntity(a, n);
                        assays.add(a);
                    }
                }
            }
        }

        return assays;
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
        if (isNotNull(node) && node.isArray()) {
            synonyms.add(new StringValue
                    (ds, Compound.SynonymValue,
                            node.asText()));
        }
    }

    public Collection<Value> getSynonyms(Compound compound) {
        final List<Value> synonyms = new ArrayList<Value>();
        final String url = getResource(compound.getId() + SYNONYMS);
        final DataSource ds = getDataSource
                (EntityNamedSources.PubChemSynonymSource);
        ds.setURL(url);
        JsonNode node = executeGetRequest(url);
        if (isNotNull(node) && node.isArray()) {
            addSynonyms(synonyms, node, ds);
        }
        return synonyms;
    }

    protected List<Compound> search(String resource, long top, long skip) {
        return search(resource, top, skip, null);
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

    protected List<Compound> search(String resource, long top,
                                    long skip, Map<String, Long> etags) {

        List<Compound> results = new ArrayList<Compound>();
        DefaultHttpClient httpclient = new DefaultHttpClient();

        try {
            final String url =
                    new StringBuilder(resource).
                            append(TOP).
                            append(top).
                            append(AMPERSAND).
                            append(SKIP).
                            append(skip).
                            toString();

            //logger.info("** url="+url);

            HttpGet get = new HttpGet(url);
            if (etags != null && !etags.isEmpty()) {
                String etag = getParentETag(etags);
                get.addHeader("If-Match", "\"" + etag + "\"");
            }

            HttpResponse response = httpclient.execute(get);
            if (response.containsHeader(E_TAG) && etags != null) {
                for (Header h : response.getHeaders(E_TAG)) {
                    int len = h.getValue().length();
                    if (len > 2) {
                        String e = h.getValue().substring(1, len - 1);
                        etags.put(e, skip);
                    }
                }
            }

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
                    ex.printStackTrace();
                } finally {
                    is.close();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        return results;
    }

    protected Compound getEntity(Compound compound, JsonNode node) {
        if (compound == null) {
            compound = new Compound();
        }

        DataSource ds = getDataSource();
        JsonNode n = node.get(CID);
        if (isNotNull(n)) {
            long cid = n.asLong();
            compound.setId(cid);
            // redundant
            compound.add(new LongValue(ds, Compound.PubChemCIDValue, cid));
        }

        n = node.get(SIDS);
        if (isNotNull(n) && n.isArray()) {
            ArrayNode nodes = (ArrayNode) n;
            for (int i = 0; i < nodes.size(); ++i) {
                long sid = nodes.get(i).asLong();
                compound.add(new LongValue(ds, Compound.PubChemSIDValue, sid));
            }
        }

        n = node.get(PROBE_ID);
        if (isNotNull(n)) {
            compound.add(new StringValue(ds, Compound.ProbeIDValue,
                    n.asText()));

        }

        n = node.get(NAME);
        if (isNotNull(n)) {
            compound.setName(n.asText());
        }

        n = node.get(IUPAC_NAME);
        if (isNotNull(n)) {
            compound.add(new StringValue(ds, Compound.IUPACNameValue, n.asText()));
        }

        addAnnotationsToCompound(compound, node);
        // we should be getting mol format instead of smiles!
        MolecularData md = new MolecularDataJChemImpl();
        md.setMolecule(node);
        compound.add(new MolecularValue(ds, Compound.MolecularValue, md));

        return compound;
    }

    protected void addAnnotationsToCompound(Compound compound, JsonNode node) {
        DataSource ds = getDataSource(EntityNamedSources.AnnotationSource);
        final ArrayNode keys = (ArrayNode) node.get(ANNO_KEY);
        final ArrayNode vals = (ArrayNode) node.get(ANNO_VAL);
        if (isNotNull(keys) && isNotNull(vals)) {
            for (int i = 0; i < keys.size(); ++i) {
                String key = keys.get(i).asText();
                String val = vals.get(i).asText();
                if (key.equalsIgnoreCase(COLLECTION) && (val.equalsIgnoreCase(APPROVED_DRUGS) || val.equalsIgnoreCase(FDA_APPROVED))) {
                    compound.setDrug(true);
                }
                compound.add(new StringValue(ds, key, val));
            }
        }
    }

    protected Compound getEntitySearch(Compound compound, JsonNode node) {
        if (compound == null) {
            compound = new Compound();
        }
        long cid = node.get(CID).asLong();
        compound.setId(cid);

        JsonNode n;
        DataSource ds = getDataSource();

        compound.add(new LongValue(ds, Compound.PubChemCIDValue, cid));

        String iupac = null;
        n = node.get(IUPAC_NAME);
        if (isNotNull(n)) {
            compound.setName(iupac = n.asText());
        }
        n = node.get(PROBE_ID);
        if (isNotNull(n)) {
            compound.add(new StringValue(ds, Compound.ProbeIDValue,
                    n.asText()));
        }
        n = node.get(PREFERRED_TERM);
        if (isNotNull(n)) {
            compound.setName(n.asText());
        }

        if (StringUtils.isNotBlank(iupac)) {
            compound.add(new StringValue(ds, Compound.IUPACNameValue, iupac));
        }

        n = node.get(HIGHLIGHT);
        if (isNotNull(n)) {
            compound.add(new StringValue
                    (ds, Compound.SearchHighlightValue, n.asText()));
        }

        MolecularData md = new MolecularDataJChemImpl();
        final JsonNode iso_smiles = node.get(ISO_SMILES);
        if (isNotNull(iso_smiles)) {
            md.setMolecule(iso_smiles.asText());
        }
        compound.add(new MolecularValue(ds, Compound.MolecularValue, md));
        addAnnotationsToCompound(compound, node);
        return compound;
    }

    public SearchResult<Compound> search(SearchParams params) {
        if (params instanceof StructureSearchParams) {
            return structureSearch((StructureSearchParams) params);
        }
        return super.search(params);
    }

    @Override
    public <T extends Entity> SearchResult<T> searchResult
            (Compound compound, Class<T> clazz) {
        RESTAbstractEntityService<T> service =
                (RESTAbstractEntityService) getServiceManager().getService(clazz);

        if (clazz.equals(Project.class)) {
            return service.getSearchResult
                    (getResource(compound.getId() + PROJECTS_RESOURCE), null);
        } else if (clazz.equals(Assay.class)) {
            return service.getSearchResult
                    (getResource(compound.getId() + ASSAYS_RESOURCE), null);
        } else if (clazz.equals(Experiment.class)) {
            return service.getSearchResult
                    (getResource(compound.getId() + EXPERIMENTS_RESOURCE), null);
        } else {
            throw new IllegalArgumentException
                    ("No related searchResults available for " + clazz);
        }
    }
}
