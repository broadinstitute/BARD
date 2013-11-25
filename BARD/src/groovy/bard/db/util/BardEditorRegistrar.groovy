package bard.db.util

import bard.db.people.Role
import bard.person.RoleEditor
import org.springframework.beans.PropertyEditorRegistrar
import org.springframework.beans.PropertyEditorRegistry
import org.springframework.beans.propertyeditors.StringTrimmerEditor

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 11/13/13
 * Time: 12:27 PM
 */
class BardEditorRegistrar implements PropertyEditorRegistrar {

    public void registerCustomEditors(PropertyEditorRegistry registry) {
        // We want all strings to be trimmed
     //   registry.registerCustomEditor(String.class, new StringTrimmerEditor(false));
        registry.registerCustomEditor(Role.class, new RoleEditor())
    }
}
