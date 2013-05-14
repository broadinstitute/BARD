package bard.db.enums.hibernate

import bard.db.enums.AssayType

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 2/6/13
 * Time: 1:40 AM
 * To change this template use File | Settings | File Templates.
 */
class AssayTypeEnumUserType extends AbstractEnumUserType<AssayType> {

    public AssayTypeEnumUserType() {
        super(AssayType.class)
    }

}