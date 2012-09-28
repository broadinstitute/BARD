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
    static Logger logger = initializeLogger()

    static FileAppender fileAppender

    private static String filePath = "test/exampleData/dnaRepairLoad.log"
    //"test/exampleData/assayDeDuplication.log"//"test/exampleData/parseCarsSpreadsheet.log"

    private static Logger initializeLogger() {
        Logger logger = Logger.getLogger("parseCarsSpreadsheetScriptLogger")
        logger.setLevel(Level.ALL)

        fileAppender = new FileAppender(new SimpleLayout(),  filePath, false)
        logger.addAppender(fileAppender)

        return logger
    }
}
