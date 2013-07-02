package bard.acl;

import bard.db.people.Role;
import org.springframework.security.acls.model.Permission;

public interface CapPermissionInterface {
    public void addPermission(Object domainObjectInstance);

    public void addPermission(Object domainObjectInstance, Role role, Permission permission);

    public void removePermission(Object domainObjectInstance);
}
