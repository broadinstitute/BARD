package bard.db.enums.hibernate

import bard.db.enums.AssayStatus

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 2/6/13
 * Time: 1:40 AM
 * To change this template use File | Settings | File Templates.
 */
class AssayStatusEnumUserType extends AbstractEnumUserType<AssayStatus> {

    public AssayStatusEnumUserType() {
        super(AssayStatus.class)
    }

}