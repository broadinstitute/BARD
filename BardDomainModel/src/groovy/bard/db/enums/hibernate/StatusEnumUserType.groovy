package bard.db.enums.hibernate

import bard.db.enums.Status

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 2/6/13
 * Time: 1:40 AM
 * To change this template use File | Settings | File Templates.
 */
class StatusEnumUserType extends AbstractEnumUserType<Status> {

    public StatusEnumUserType() {
        super(Status.class)
    }

}