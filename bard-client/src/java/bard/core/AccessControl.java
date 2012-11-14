package bard.core;

import java.io.Serializable;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

public class AccessControl implements Serializable {
    private static final long serialVersionUID = 0xc5adea8931394cf8l;

    /*
     * permissions (can be OR'ed together
     */
    public static final int PERM_NONE = 0;
    public static final int PERM_READ = 1;
    public static final int PERM_CREATE = 2;
    public static final int PERM_UPDATE = 4;
    public static final int PERM_EXPORT = 8;
    public static final int PERM_SEARCH = 16;

    public static final int PERM_ALL = 
        PERM_READ|PERM_CREATE|PERM_UPDATE|PERM_EXPORT|PERM_SEARCH;
    
    /*
     * access
     */
    public static final int ACC_PRIVATE = 0;
    public static final int ACC_GROUP = 1;
    public static final int ACC_PUBLIC = 2;


    protected int access; // one of ACC_*
    protected int permission; // one of PERM_*
    protected List<String> groups = new ArrayList<String>();

    protected AccessControl () {}
    protected AccessControl (int access, int permission) {
        this.access = access;
        this.permission = permission;
    }

    public int getAccess () { return access; }
    public int getPermission () { return permission; }
    public Collection<String> getGroups () { 
        return Collections.unmodifiableCollection(groups); 
    }
    public boolean remove (String g) { return groups.remove(g); }
    public void add (String g) { groups.add(g); }
    public Iterator<String> groups () { return getGroups().iterator(); }
    public int getGroupCount () { return groups.size(); }

    public static AccessControl getInstance (int access, int permission) {
        return new AccessControl (access, permission);
    }

    public String toString () { 
        return "{access="+access+",perm="+permission
            +",groups="+groups.size()+"}";
    }
}
