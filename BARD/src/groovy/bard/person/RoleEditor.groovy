package bard.person

import bard.db.people.Role
import org.springframework.beans.PropertyEditorRegistrar
import org.springframework.beans.PropertyEditorRegistry

import java.beans.PropertyEditorSupport

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 6/30/13
 * Time: 2:55 PM
 * To change this template use File | Settings | File Templates.
 */
class RoleEditor extends PropertyEditorSupport {
    void setAsText(String text) {
        value = Role.get(text)
    }
}
class RoleEditorRegistrar implements PropertyEditorRegistrar {
    void registerCustomEditors(PropertyEditorRegistry registry) {
        registry.registerCustomEditor(Role, new RoleEditor())
    }
}
