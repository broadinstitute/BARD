package bard.core;


import bard.core.interfaces.ProjectValues;

public class Publication extends Entity implements ProjectValues {
    private static final long serialVersionUID = -3745590675001359198L;

    protected String title, doi, abs;
    protected Long pubmedId;

    public Publication() {
    }

    public Publication(String name) {
        super(name);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String getAbs() {
        return abs;
    }

    public void setAbs(String abs) {
        this.abs = abs;
    }

    public Long getPubmedId() {
        return pubmedId;
    }

    public void setPubmedId(Long pubmedId) {
        this.pubmedId = pubmedId;
    }

    public String toString() {
        return getClass().getName() + "{title=" + title
                + ",pubmedid=" + pubmedId + "," + super.toString() + "}";
    }
}
