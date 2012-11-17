package bard.core;

import bard.core.interfaces.EntityNamedSources;
import bard.core.interfaces.EntityValues;
import org.apache.log4j.Logger;

import java.util.*;


public class Entity implements EntityValues,
        EntityNamedSources,
        java.io.Serializable {
    private static final long serialVersionUID = 0xed49f700e8675bd0l;
    static final Logger log = Logger.getLogger(Entity.class);

    protected Object id; // transient entity id

    protected String name; // entity name
    protected String description; // short description of the entity

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

    public void addValue(Value a) {
        values.add(a);
    }

    public Collection<Value> getValues() {
        return Collections.unmodifiableCollection(values);
    }

    public Collection<Value> getValues(String id) {
        final List<Value> lv = new ArrayList<Value>();

        for (Value v : this.values) {
            findValues(lv, v, id);
        }
        return lv;
    }

    public Collection<Value> getValues(DataSource ds) {
        List<Value> lv = new ArrayList<Value>();
        for (Value v : this.values) {
            findValues(lv, v, ds);
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

    public void addLink(Link l) {
        links.add(l);
    }

    public Collection<Link> getLinks() {
        return Collections.unmodifiableCollection(links);
    }

    public int getLinkCount() {
        return links.size();
    }
}
