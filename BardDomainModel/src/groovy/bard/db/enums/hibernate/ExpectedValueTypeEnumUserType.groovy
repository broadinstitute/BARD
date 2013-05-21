package bard.db.enums.hibernate

import bard.db.enums.ExpectedValueType

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 2/6/13
 * Time: 1:40 AM
 * To change this template use File | Settings | File Templates.
 */
class ExpectedValueTypeEnumUserType extends AbstractEnumUserType<ExpectedValueType> {

    public ExpectedValueTypeEnumUserType() {
        super(ExpectedValueType.class)
    }

}