package bard.dm.cars.spreadsheet

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 9/2/12
 * Time: 7:47 AM
 * To change this template use File | Settings | File Templates.
 */
class HeaderNamesReader {
    HeaderNames readHeaderNames(String headerConfigPath) {
        ConfigObject config = (new ConfigSlurper()).parse(new File(headerConfigPath).toURL())

        HeaderNames headerNames = new HeaderNames()

        headerNames.projectUid = config.carsSpreadsheetHeaders.projectUid
        headerNames.aidNumber = config.carsSpreadsheetHeaders.aidNumber
        headerNames.grantNumber = config.carsSpreadsheetHeaders.grantNumber
        headerNames.assayName = config.carsSpreadsheetHeaders.assayName
        headerNames.assayTarget = config.carsSpreadsheetHeaders.assayTarget
        headerNames.assaySubtype = config.carsSpreadsheetHeaders.assaySubtype
        headerNames.assayCenter = config.carsSpreadsheetHeaders.assayCenter
        headerNames.grantTitle = config.carsSpreadsheetHeaders.grantTitle
        headerNames.assayProvider = config.carsSpreadsheetHeaders.assayProvider
        headerNames.summaryAid = config.carsSpreadsheetHeaders.summaryAid


        return headerNames
    }
}
