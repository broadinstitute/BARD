package bard.dm

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.log4j.FileAppender
import org.apache.log4j.SimpleLayout

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 9/2/12
 * Time: 7:57 AM
 * To change this template use File | Settings | File Templates.
 */
class Log {
    static Logger logger = null

    private static FileAppender fileAppender

    public static Logger initializeLogger(String filePath) {
        logger = Logger.getLogger("parseCarsSpreadsheetScriptLogger")
        logger.setLevel(Level.ALL)

        fileAppender = new FileAppender(new SimpleLayout(),  filePath, false)
        logger.addAppender(fileAppender)
    }

    public static void close() {
        fileAppender.close()
        fileAppender = null
        logger = null
    }


}
