import bard.dm.Log
import org.apache.log4j.Level
import bard.dm.assaycompare.assaycleanup.TestAssayCleanup

Log.initializeLogger("test/exampleData/assayDeDuplication.log")
Log.logger.setLevel(Level.DEBUG)

TestAssayCleanup testAssayCleanup = new TestAssayCleanup()

testAssayCleanup.test()

Log.close()
