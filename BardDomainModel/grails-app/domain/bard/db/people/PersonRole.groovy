package bard.db.people

import org.apache.commons.lang.builder.HashCodeBuilder

class PersonRole implements Serializable {

    Person person
    Role role
    Date dateCreated = new Date()
    Date lastUpdated
    String modifiedBy
    boolean equals(other) {
        if (!(other instanceof PersonRole)) {
            return false
        }

        other.person?.id == person?.id &&
                other.role?.id == role?.id
    }

    int hashCode() {
        def builder = new HashCodeBuilder()
        if (person) builder.append(person.id)
        if (role) builder.append(role.id)
        builder.toHashCode()
    }

    static PersonRole get(long personId, long roleId) {
        find 'from PersonRole where person.id=:personId and role.id=:roleId',
                [personId: personId, roleId: roleId]
    }

    static PersonRole create(Person person, Role role, String modifiedBy,boolean flush = false) {
        new PersonRole(person: person, role: role, modifiedBy: modifiedBy,lastUpdated: new Date()).save(flush: flush, insert: true)
    }

    static boolean remove(Person person, Role role, boolean flush = false) {
        PersonRole instance = PersonRole.findByPersonAndRole(person, role)
        if (!instance) {
            return false
        }

        instance.delete(flush: flush)
        true
    }

    static void removeAll(Person person) {
        executeUpdate 'DELETE FROM PersonRole WHERE person=:person', [person: person]
    }

    static void removeAll(Role role) {
        executeUpdate 'DELETE FROM PersonRole WHERE role=:role', [role: role]
    }


    static mapping = {
        table('PERSON_ROLE')
        id(column: 'PERSON_ROLE_ID', generator: "sequence", params: [sequence: 'PERSON_ROLE_ID_SEQ'])
        id composite: ['role', 'person']

    }

    static constraints = {
        dateCreated(nullable: false)
        lastUpdated(nullable: true)
        modifiedBy(nullable: true, blank: false, maxSize: Person.MODIFIED_BY_MAX_SIZE)
    }
}
