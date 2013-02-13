package bard.db.enums.hibernate

import bard.db.enums.ReadyForExtraction

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 2/6/13
 * Time: 1:40 AM
 * To change this template use File | Settings | File Templates.
 */
class ReadyForExtractionEnumUserType extends AbstractEnumUserType<ReadyForExtraction> {

    public ReadyForExtractionEnumUserType() {
        super(ReadyForExtraction.class)
    }

}