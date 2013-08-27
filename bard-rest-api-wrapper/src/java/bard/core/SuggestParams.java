package bard.core;

import bard.core.SearchParams;

/**
 * Parameters for autosuggest.
 *
 * @author Rajarshi Guha
 */
public class SuggestParams extends SearchParams {
    private static final long serialVersionUID = 8196705055192706776L;

    protected int numSuggestion = 10;
    public SuggestParams(){
        super();
    }

    public SuggestParams(String query, int numSuggestion) {
        super(query);
        setNumSuggestion(numSuggestion);
    }

    public int getNumSuggestion() {
        return this.numSuggestion;
    }

    public void setNumSuggestion(int numSuggestion) {
        this.numSuggestion = numSuggestion;
    }
}
