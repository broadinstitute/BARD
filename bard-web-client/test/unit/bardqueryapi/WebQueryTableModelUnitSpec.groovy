package bardqueryapi

import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 10/7/12
 * Time: 10:35 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class WebQueryTableModelUnitSpec extends Specification {
    void "test default getters"() {
        when:
        WebQueryTableModel webQueryTableModel = new WebQueryTableModel()
        then:
        assert "" == webQueryTableModel.getColumnName(0)
        assert 0 == webQueryTableModel.getColumnCount()
        assert !webQueryTableModel.getData()
        assert 0 == webQueryTableModel.getRowCount()
    }

    void "test getValueAt with exceptions #label"() {
        given:
        WebQueryTableModel webQueryTableModel = new WebQueryTableModel(headers, rows)
        when:
        webQueryTableModel.getValueAt(row, column)
        then:
        def e = thrown(IllegalArgumentException)
        expectedMessage == e.message

        where:
        label                          | expectedMessage                                     | row | column | headers | rows
        "row < 1"                      | "Column and Row numbers must be >=0"                | -1  | 0      | []      | []
        "column < 1"                   | "Column and Row numbers must be >=0"                | 0   | -1     | []      | []
        "row >= data.size"             | "Row number must not be greater than the data size" | 0   | 1      | []      | []
        "column >= columnHeaders.size" | "Column must not be greater than the column size"   | 0   | 1      | []      | [[new WebQueryValueModel("")]]

    }


    void "test setValueAt with exceptions #label"() {
        given:
        WebQueryTableModel webQueryTableModel = new WebQueryTableModel(headers, rows)
        when:
        webQueryTableModel.setValueAt(new WebQueryValueModel(""), row, column)
        then:
        def e = thrown(IllegalArgumentException)
        expectedMessage == e.message

        where:
        label                          | expectedMessage                                     | row | column | headers | rows
        "row < 1"                      | "Column and Row numbers must be >=0"                | -1  | 0      | []      | []
        "column < 1"                   | "Column and Row numbers must be >=0"                | 0   | -1     | []      | []
        "row >= data.size"             | "Row number must not be greater than the data size" | 0   | 1      | []      | []
        "column >= columnHeaders.size" | "Column must not be greater than the column size"   | 0   | 1      | []      | [[new WebQueryValueModel("")]]

    }


    void "test addColumn #label"() {
        given:
        WebQueryTableModel webQueryTableModel = new WebQueryTableModel()
        when:
        webQueryTableModel.addColumn(columnHeader, columnData)
        then:
        assert !webQueryTableModel.getColumnHeaders() == expectedAnswer
        assert !webQueryTableModel.data == expectedAnswer
        assert webQueryTableModel.getColumnName(0) == expectedColumnName
        where:
        label                        | columnHeader               | columnData                   | expectedAnswer | expectedColumnName
        "With header and data"       | new WebQueryValueModel("") | [new WebQueryValueModel("")] | false          | ""
        "With header and no data"    | new WebQueryValueModel("") | []                           | true           | ""
        "With data, no header"       | null                       | [new WebQueryValueModel("")] | true           | ""
        "With no header and no data" | null                       | []                           | true           | ""
    }


    void "test addRowData #label"() {
        given:
        WebQueryTableModel webQueryTableModel = new WebQueryTableModel()
        when:
        webQueryTableModel.addRowData(rowData)
        then:
        assert expectedRowCount == webQueryTableModel.rowCount
        assert expectedColCount == webQueryTableModel.columnCount
        where:
        label          | rowData                      | expectedRowCount | expectedColCount
        "With no data" | []                           | 0                | 0
        "With data"    | [new WebQueryValueModel("")] | 1                | 0

    }

}
