package bard.core;

import bard.core.interfaces.EntityNamedSources;
import bard.core.interfaces.EntityValues;

import java.util.*;


public class Entity implements EntityValues,
        EntityNamedSources,
        java.io.Serializable {
    private static final long serialVersionUID = 0xed49f700e8675bd0l;


    protected Object id; // transient entity id

    protected String name; // entity name
    protected String description; // short description of the entity
    // creation timestamp
    protected long created = new java.util.Date().getTime();
    protected long modified; // last modified

    protected AccessControl acl; // access control list

    /**
     * Values for entity
     */
    protected List<Value> values = new ArrayList<Value>();

    /**
     * Links
     */
    protected List<Link> links = new ArrayList<Link>();

    protected Entity() {
    }

    public Entity(String name) {
        this.name = name;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public void setCreatedNow() {
        this.created = new java.util.Date().getTime();
    }

    public long getModified() {
        return modified;
    }

    public void setModified(long modified) {
        this.modified = modified;
    }

    public void setModifiedNow() {
        this.modified = new java.util.Date().getTime();
    }

    public AccessControl getACL() {
        return acl;
    }

    public void setACL(AccessControl acl) {
        this.acl = acl;
    }

    public void add(Value a) {
        values.add(a);
    }

    public boolean remove(Value a) {
        return values.remove(a);
    }

    public Collection<Value> getValues() {
        return Collections.unmodifiableCollection(values);
    }

    public Collection<Value> getValues(String id) {
        List<Value> lv = new ArrayList<Value>();
        for (Iterator<Value> iter = this.values.iterator(); iter.hasNext(); ) {
            Value v = iter.next();
            findValues(lv, v, id);
        }
        return lv;
    }

    public Collection<Value> getValues(DataSource ds) {
        List<Value> lv = new ArrayList<Value>();
        for (Iterator<Value> iter = values.iterator(); iter.hasNext(); ) {
            findValues(lv, iter.next(), ds);
        }
        return lv;
    }

    // get singleton 
    public Value getValue(String id) {
        Collection<Value> vals = getValues(id);
        return vals.isEmpty() ? null : vals.iterator().next();
    }

    protected void findValues(List<Value> lv, Value value, String id) {
        if (value.getId().equals(id)) {
            lv.add(value);
        }
        for (Value v : value.children) {
            findValues(lv, v, id);
        }
    }

    protected void findValues(List<Value> lv, Value value, DataSource ds) {
        if (ds.equals(value.getSource())) {
            lv.add(value);
        } else {
            // only traverse the children if the data source doesn't match
            for (Value v : value.children) {
                findValues(lv, v, ds);
            }
        }
    }

    public int getValueCount() {
        return values.size();
    }

    public Iterator<Value> values() {
        return getValues().iterator();
    }

    public void add(Link l) {
        links.add(l);
    }

    public boolean remove(Link l) {
        return links.remove(l);
    }

    public Collection<Link> getLinks() {
        return Collections.unmodifiableCollection(links);
    }

    public int getLinkCount() {
        return links.size();
    }

    public Iterator<Link> links() {
        return getLinks().iterator();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder
                (getClass().getName() + "{id=" + id + ",name=" + name + ",description="
                        + description + ",created=" + created
                        + ",modified=" + modified + ",acl=" + acl + ",values=" + values.size() + "[");
        for (Iterator<Value> it = values(); it.hasNext(); ) {
            Value v = it.next();
            sb.append(v.toString());
            if (it.hasNext()) {
                sb.append(" ");
            }
        }
        sb.append("],links=" + links.size());
        sb.append("}");
        return sb.toString();
    }
}
