package bard.core;

import java.io.Serializable;
import java.util.*;


public class Value implements Serializable {
    private static final long serialVersionUID = 0x36d23c591aa3c2d4l;

    protected DataSource source;

    protected String id;
    protected String url;
    protected List<Value> children = new ArrayList<Value>();

    protected Value() {
    }

    public Value(DataSource source) {
        this(source, null);
        setId(getClass().getName());
    }

    public Value(Value parent) {
        this(parent.getSource(), null);
        parent.addValue(this);
        setId(getClass().getName());
    }

    public Value(Value parent, String id) {
        this(parent.getSource(), id);
        parent.addValue(this);
    }

    public Value(DataSource source, String id) {
        this.source = source;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DataSource getSource() {
        return source;
    }

    public void setSource(DataSource source) {
        this.source = source;
    }

    public String getURL() {
        return url;
    }

    public void setURL(String url) {
        this.url = url;
    }

    public Object getValue() {
        return null;
    }

    // get (first) value with a given id
    public Value getChild(String id) {
        Collection<Value> children = getChildren(id);
        if (children.isEmpty()) {
            return null;
        }
        return children.iterator().next();
    }

    // get all child values with a given id
    public Collection<Value> getChildren(String id) {
        List<Value> children = new ArrayList<Value>();
        getChildren(children, this, id);
        return children;
    }

    protected static void getChildren
            (List<Value> children, Value value, String id) {
        if(value == null || value.getId() == null){
            return;
        }
        if (value.getId().equals(id)) {
            children.add(value);
        }

        for (Value v : value.children) {
            getChildren(children, v, id);
        }
    }

    public void addValue(Value child) {
        children.add(child);
    }

    public Collection<Value> getChildren() {
        return Collections.unmodifiableCollection(children);
    }

    public int getChildCount() {
        return children.size();
    }

    public boolean isTerminal() {
        return children.isEmpty();
    }
}
