package bard.dm.cars.spreadsheet

class CarsExperiment {

    int spreadsheetLineNumber
    Integer aid
    String assayName
    String assayTarget
    String assaySubtype
    String assayProvider

    //mainly in CarsProject
    String grantNumber
    String grantTitle
    String assayCenter

    static constraints = {
    }

    @Override
    String toString() {
        return "spreadsheetLineNumber:" + String.valueOf(spreadsheetLineNumber) + " aid:" + aid
    }
}
