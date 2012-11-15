package bard.core.rest;

import bard.core.Compound;
import bard.core.interfaces.EntityService;
import bard.core.interfaces.SearchResult;
import bard.core.StructureSearchParams;
import bard.core.Value;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
* Created with IntelliJ IDEA.
* User: jasiedu
* Date: 11/4/12
* Time: 9:18 PM
* To change this template use File | Settings | File Templates.
*/ // this pattern is quite general; it should be refactor somewhere else
public class StructureSearchResult extends SearchResultImp<Compound> {
    public static final String STRUCTURE = "[structure]";
    public static final String TYPE_SUB = "&type=sub";
    public static final String TYPE_SUP = "&type=sup";
    public static final String TYPE_EXACT = "&type=exact";
    public static final String TYPE_SIM = "&type=sim";
    public static final String CUTOFF = "&cutoff=";
    public static final String FILTER = "?filter=";
    volatile StructureSearchParams params;
    volatile String url;
    Map<String, Long> etags = new ConcurrentHashMap<String, Long>();
    private RESTCompoundService restCompoundService;

    StructureSearchResult(RESTCompoundService restCompoundService, StructureSearchParams params) {
        this.restCompoundService = restCompoundService;
        this.searchResults = new ArrayList<Compound>();
        StringBuilder url = new StringBuilder();
        try {
            url.append(restCompoundService.getResource()).append(FILTER).append(
                    URLEncoder.encode(params.getQuery(), EntityService.UTF_8)).append(
                    STRUCTURE);
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
            throw new IllegalArgumentException
                    ("Bogus query: " + params.getQuery());
        }

        switch (params.getType()) {
            case Substructure:
                url.append(TYPE_SUB);
                break;
            case Superstructure:
                url.append(TYPE_SUP);
                break;
            case Exact:
                url.append(TYPE_EXACT);
                break;
            case Similarity:
                url.append(TYPE_SIM);
                if (params.getThreshold() != null) {
                    url.append(CUTOFF).append(String.format
                            ("%1$.3f", params.getThreshold()));
                } else {
                    throw new IllegalArgumentException
                            ("No threshold specified for similarity search!");
                }
                break;
        }
        url.append(EntityService.AMPERSAND);
        url.append(EntityService.EXPAND_TRUE);

        this.url = url.toString();
        this.params = params;
    }

    public StructureSearchResult build() {
        this.searchResults.clear();
        this.etags.clear();
        long skip = params.getSkip() != null ? params.getSkip() : 0;
        long top = params.getTop() != null ? params.getTop() : 100;

        while (true) {
            List<Compound> results = restCompoundService.search(url, top, skip, etags);
            this.searchResults.addAll(results);

            count += results.size();

            if (params.getTop() != null || results.size() < top) {
                break;
            }
            skip += results.size();
        }
        if (this.count == 0) {
            this.count = this.searchResults.size();
        }
        return this;
    }

    @Override
    public Collection<Value> getFacets() {
        String etag = RESTCompoundService.getParentETag(etags);
        return restCompoundService.getFacets(etag);
    }

    @Override
    public Object getETag() {
        return RESTCompoundService.getParentETag(etags);
    }
}
