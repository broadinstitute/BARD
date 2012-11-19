package bard.core.adapter;


import bard.core.DataSource
import bard.core.Entity
import bard.core.Value

public class EntityAdapter<E extends Entity> {
    protected E entity;

    protected EntityAdapter() {}

    public EntityAdapter(E entity) {
        setEntity(entity);
    }

    public Long getId() {
        return new Long(getEntity().getId().toString());
    }

    public void setEntity(E entity) {
        this.entity = entity;
    }

    public Entity getEntity() { return entity; }

    public String getName() {
        return entity?.getName();
    }

    public List<String> getSynonyms() {
        Collection<Value> values = entity.getValues(Entity.SynonymValue);
        List<String> syns = new ArrayList<String>();
        for (Value v : values) {
            syns.add((String) v.getValue());
        }
        return syns;
    }

    public String getSearchHighlight() {
        Value hl = entity.getValue(Entity.SearchHighlightValue);
        if (hl != null) {
            return (String) hl.getValue();
        }
        return null;
    }

    public Collection<Value> getAnnotations() {
        return entity.getValues(new DataSource(Entity.AnnotationSource));
    }
}
