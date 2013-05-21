package bard.db.enums.hibernate

import bard.db.enums.AddChildMethod

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 2/6/13
 * Time: 1:40 AM
 * To change this template use File | Settings | File Templates.
 */
class AddChildMethodEnumUserType extends AbstractEnumUserType<AddChildMethod> {

    public AddChildMethodEnumUserType() {
        super(AddChildMethod.class)
    }

}