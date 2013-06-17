package bard.db.command

import grails.validation.ValidationErrors

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 3/27/13
 * Time: 10:02 AM
 * To change this template use File | Settings | File Templates.
 */
abstract class BardCommand {

    def attemptFindById(Class domain, Long id) {
        def instance
        if (id) {
            instance = domain.findById(id)
        }
        if (!instance) {
            ValidationErrors localErrors = new ValidationErrors(this)
            localErrors.reject('default.not.found.message', [domain, id] as Object[], 'not found')
            addToErrors(localErrors)
        }
        return instance
    }

    boolean attemptSave(Object domain) {
        if (!domain?.save(flush:true)) {
            domain?.errors?.fieldErrors?.each { error ->
                if (properties.containsKey(error.field)){  // if the command object has a property with the same name as the field copy the fieldError
                    getErrors().rejectValue(error.field, error.code)
                }
                else { // otherwise register a global error
                    getErrors().reject(error.code)
                }
            }
            domain?.errors?.globalErrors?.each { error ->
                getErrors().reject(error.code)
            }
            return false
        }
        return true
    }

    protected addToErrors(ValidationErrors localErrors) {
        if (errors) {
            errors.addAllErrors(localErrors)
        } else {
            setErrors(localErrors)
        }
    }

}
