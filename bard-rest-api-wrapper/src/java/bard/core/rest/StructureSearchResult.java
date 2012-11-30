package bard.core.rest;

import bard.core.Compound;
import bard.core.rest.spring.util.StructureSearchParams;
import bard.core.Value;
import bard.core.interfaces.EntityService;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

public class StructureSearchResult extends SearchResultImpl<Compound> {
    private final StructureSearchParams params;
    private final Map<String, Long> etags;
    private final RESTCompoundService restCompoundService;


    public StructureSearchResult(RESTCompoundService restCompoundService, StructureSearchParams params) {
        this.restCompoundService = restCompoundService;
        this.searchResults = new ArrayList<Compound>();
        this.etags = new HashMap<String, Long>();
        this.params = params;
    }

    protected String createURL(final String resource,final String query,final StructureSearchParams.Type structureType,final Double threshold) {
        final StringBuilder url = new StringBuilder();
        try {
            url.append(resource).append(EntityService.FILTER_QUESTION).append(
                    URLEncoder.encode(query, EntityService.UTF_8)).append(
                    EntityService.STRUCTURE);
        } catch (UnsupportedEncodingException ex) {
            log.error(ex);
            throw new IllegalArgumentException
                    ("Bogus query: " + query);
        }

        switch (structureType) {
            case Substructure:
                url.append(EntityService.TYPE_SUB);
                break;
            case Superstructure:
                url.append(EntityService.TYPE_SUP);
                break;
            case Exact:
                url.append(EntityService.TYPE_EXACT);
                break;
            case Similarity:
                url.append(EntityService.TYPE_SIM);
                 if (threshold != null) {
                    url.append(EntityService.CUTOFF).append(String.format
                            ("%1$.3f", threshold));
                } else {
                    final String message = "No threshold specified for similarity search!";
                    log.error(message);
                    throw new IllegalArgumentException
                            (message);
                }
                break;
        }
        url.append(EntityService.AMPERSAND);
        url.append(EntityService.EXPAND_TRUE);

        return url.toString();
    }
    @Override
    public StructureSearchResult build() {
        this.searchResults.clear();
        this.etags.clear();
        String url = createURL(restCompoundService.getResource(),params.getQuery(),params.getType(),params.getThreshold());
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
