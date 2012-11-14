package bard.core;

public class Link extends Entity {
    private static final long serialVersionUID = 0x8577c90a926e9417l;

    protected boolean directed;
    protected Entity source;
    protected Entity target;

    public Link() {
    }

    public Link(Entity source, Entity target) {
        this(source, target, false);
    }

    public Link(Entity source, Entity target, boolean directed) {
        if (source == null || target == null) {
            throw new IllegalArgumentException
                    ("Neither source nor target can be null!");
        }
        this.source = source;
        this.target = target;
        this.directed = directed;
        source.add(this);
        target.add(this);
    }

    public Entity getSource() {
        return source;
    }

    public void setSource(Entity source) {
        this.source = source;
    }

    public Entity getTarget() {
        return target;
    }

    public void setTarget(Entity target) {
        this.target = target;
    }

    public void setDirected(boolean directed) {
        this.directed = directed;
    }

    public boolean isDirected() {
        return directed;
    }

    public String toString() {
        return getClass().getName() + "{source=" + source + ",target=" + target
                + ",directed=" + directed + "," + super.toString() + "}";
    }
}
