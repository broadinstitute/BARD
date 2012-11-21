package bard.core

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 11/16/12
 * Time: 1:11 PM
 * To change this template use File | Settings | File Templates.
 */
class Link extends Entity{

    protected final boolean directed;
    protected final Entity source;
    protected final Entity target;


    public Link(Entity source, Entity target) {
        this(source, target, false);
    }

    public Link(Entity source, Entity target, boolean directed) {
        if (source == null || target == null) {
            final String message = "Neither source nor target can be null!";
            log.error(message);
            throw new IllegalArgumentException
            (message);
        }
        this.source = source;
        this.target = target;
        this.directed = directed;
        source.addLink(this);
        target.addLink(this);
    }

    public Entity getSource() {
        return this.source;
    }

    public boolean isDirected() {
        return this.directed;
    }
}
