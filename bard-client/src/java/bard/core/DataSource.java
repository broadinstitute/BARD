package bard.core;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.Date;


public class DataSource implements Serializable {
    private static final long serialVersionUID = 0xf4bcf943bf1d45d4l;
    private static final Logger log = Logger.getLogger(DataSource.class);
    public static final DataSource DEFAULT =
            new DataSource(DataSource.class.getName(), "v1.0",
                    "http://bard.nih.gov");

    protected String name;
    protected String url;
    protected String version;
    protected String description;
    protected Date date;

    protected DataSource() {
    }

    public DataSource(String name) {
        this(name, "*", null);
    }

    public DataSource(String name, String version) {
        this(name, version, null);
    }

    public DataSource(String name, String version, String url) {
        if (StringUtils.isBlank(name) || StringUtils.isBlank(version)) {
            final String message = "DataSource name and version can't be null";
            log.error(message);
            throw new IllegalArgumentException
                    (message);
        }
        this.name = name;
        this.url = url;
        this.version = version;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setURL(String url) {
        this.url = url;
    }

    public String getURL() {
        return url;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }


    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof DataSource)) return false;
        DataSource ds = (DataSource) obj;

        return name.equals(ds.name)
                && (version.equals("*")
                || ds.version.equals("*")
                || version.equals(ds.version));
    }

    public static DataSource getCurrent() {
        return DEFAULT;
    }

    public int hashCode() {
        return name.hashCode() ^ version.hashCode();
    }
}
