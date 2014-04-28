/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package bard.core;

import java.io.Serializable;
import java.util.*;


public class Value implements Serializable, Comparable {
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
         Collections.sort(children);
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
        Collections.sort(children);
    }

    public void addValue(Value child) {
        children.add(child);
    }

    public Collection<Value> getChildren() {
        Collections.sort(children);
        return Collections.unmodifiableCollection(children);
    }

    public int getChildCount() {
        return children.size();
    }

    public boolean isTerminal() {
        return children.isEmpty();
    }

    /**
     * Subclasses should override this
     * @param o
     * @return
     */
    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
