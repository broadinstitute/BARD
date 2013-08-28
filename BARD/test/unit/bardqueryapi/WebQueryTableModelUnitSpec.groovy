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
        TableModel tableModel = new TableModel()
        then:
        assert "" == tableModel.getColumnName(0)
        assert 0 == tableModel.getColumnCount()
        assert !tableModel.getData()
        assert 0 == tableModel.getRowCount()
    }

    void "test getValueAt with exceptions #label"() {
        given:
        TableModel tableModel = new TableModel(headers, rows)
        when:
        tableModel.getValueAt(row, column)
        then:
        def e = thrown(IllegalArgumentException)
        expectedMessage == e.message

        where:
        label                          | expectedMessage                                     | row | column | headers | rows
        "row < 1"                      | "Column and Row numbers must be >=0"                | -1  | 0      | []      | []
        "column < 1"                   | "Column and Row numbers must be >=0"                | 0   | -1     | []      | []
        "row >= data.size"             | "Row number must not be greater than the data size" | 0   | 1      | []      | []
        "column >= columnHeaders.size" | "Column must not be greater than the column size"   | 0   | 1      | []      | [[new StringValue(value: "")]]

    }


    void "test setValueAt with exceptions #label"() {
        given:
        TableModel tableModel = new TableModel(headers, rows)
        when:
        tableModel.setValueAt(new StringValue(value: ""), row, column)
        then:
        def e = thrown(IllegalArgumentException)
        expectedMessage == e.message

        where:
        label                          | expectedMessage                                     | row | column | headers | rows
        "row < 1"                      | "Column and Row numbers must be >=0"                | -1  | 0      | []      | []
        "column < 1"                   | "Column and Row numbers must be >=0"                | 0   | -1     | []      | []
        "row >= data.size"             | "Row number must not be greater than the data size" | 0   | 1      | []      | []
        "column >= columnHeaders.size" | "Column must not be greater than the column size"   | 0   | 1      | []      | [[new StringValue(value: "")]]

    }


    void "test addColumn #label"() {
        given:
        TableModel tableModel = new TableModel()
        when:
        tableModel.addColumn(columnHeader, columnData)
        then:
        assert !tableModel.getColumnHeaders() == expectedAnswer
        assert !tableModel.data == expectedAnswer
        assert tableModel.getColumnName(0) == expectedColumnName
        where:
        label                        | columnHeader               | columnData                   | expectedAnswer | expectedColumnName
        "With header and data"       | new StringValue(value: "") | [new StringValue(value: "")] | false          | ""
        "With header and no data"    | new StringValue(value: "") | []                           | true           | ""
        "With data, no header"       | null                       | [new StringValue(value: "")] | true           | ""
        "With no header and no data" | null                       | []                           | true           | ""
    }


    void "test addRowData #label"() {
        given:
        TableModel tableModel = new TableModel()
        when:
        tableModel.addRowData(rowData)
        then:
        assert expectedRowCount == tableModel.rowCount
        assert expectedColCount == tableModel.columnCount
        where:
        label          | rowData                      | expectedRowCount | expectedColCount
        "With no data" | []                           | 0                | 0
        "With data"    | [new StringValue(value: "")] | 1                | 0

    }

}
