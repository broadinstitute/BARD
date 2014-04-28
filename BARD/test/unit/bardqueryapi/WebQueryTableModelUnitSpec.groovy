/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
