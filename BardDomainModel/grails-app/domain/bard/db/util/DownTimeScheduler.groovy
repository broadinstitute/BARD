package bard.db.util

import java.text.DateFormat
import java.text.SimpleDateFormat

/**
 * Contains information that would be used to notify users of maintenance work etc
 */
class DownTimeScheduler {
    public static final int DISPLAY_VALUE_MAX_SIZE = 100
    public static final int CREATED_BY_MAX_SIZE = 256
    String createdBy
    Date downTime
    String displayValue
    Date dateCreated = new Date()
    Date lastUpdated = new Date()

    static final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.US)

    static transients = ['downTimeAsLong', 'downTimeAsString', 'displayValueIfGreaterThanCurrentTime']
    static mapping = {
        table('DOWN_TIME_SCHEDULER')
        id(column: "DOWN_TIME_SCHEDULER_ID", generator: "sequence", params: [sequence: 'DOWN_TIME_SCHEDULER_ID_SEQ'])
    }

    String getDownTimeAsString() {
        if (this.downTime) {
            return DownTimeScheduler.dateFormat.format(this.downTime)
        }
        return ""
    }

    Long getDownTimeAsLong() {
        return downTime?.time ?: 0
    }
    /**
     *  get the display value if the current time is less than the down time
     * @return
     */
    String getDisplayValueIfDownTimeGreaterThanCurrentTime() {
        final long downTime = downTimeAsLong
        if (downTime > System.currentTimeMillis()) { //means there is a scheduled down time
            return displayValue
        }
        return ""
    }

    static constraints = {
        createdBy(nullable: false, blank: false, maxSize: CREATED_BY_MAX_SIZE)
        displayValue(nullable: false, blank: false, maxSize: DISPLAY_VALUE_MAX_SIZE)
        downTime(nullable: false)
        lastUpdated(nullable: false)
        dateCreated(nullable: false)
    }


}
