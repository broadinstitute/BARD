package bard.core;

import java.io.Serializable;
import java.util.Date;
import java.util.Calendar;



public class DataSource implements Serializable {  
    private static final long serialVersionUID = 0xf4bcf943bf1d45d4l;

    public static final DataSource DEFAULT = 
        new DataSource (DataSource.class.getName(), "v1.0",
                        "http://bard.nih.gov");

    protected String name;
    protected String url;
    protected String version;
    protected String description;
    protected Date date;
    protected AccessControl acl;

    protected DataSource () {}
    public DataSource (String name) {
        this (name, "*", null);
    }
    public DataSource (String name, String version) {
        this (name, version, null);
    }
    public DataSource (String name,  String version, String url) {
        if (name == null || version == null) {
            throw new IllegalArgumentException
                ("DataSource name and version can't be null");
        }
        this.name = name;
        this.url = url;
        this.version = version;
        setDate (Calendar.getInstance().getTime());
    }

    public void setName (String name) { this.name = name; }
    public String getName () { return name; }

    public void setURL (String url) { this.url = url; }
    public String getURL () { return url; }

    public void setVersion (String version) { this.version = version; }
    public String getVersion () { return version; }
    
    public void setDescription (String description) {
        this.description = description;
    }
    public String getDescription () { return description; }

    public void setDate (Date date) { this.date = date; }
    public Date getDate () { return date; }

    public AccessControl getACL () { return acl; }
    public void setACL (AccessControl acl) { this.acl = acl; }

    public boolean equals (Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof DataSource)) return false;
        DataSource ds = (DataSource)obj;

        return name.equals(ds.name) 
            && (version.equals("*") 
                || ds.version.equals("*") 
                || version.equals(ds.version));
    }

    public static DataSource getCurrent () { return DEFAULT; }

    public int hashCode () {
        return name.hashCode() ^ version.hashCode();
    }

    public String toString () {
        return "{name="+name+",url="+url+",version="+version+",acl="+acl+"}";
    }
}
