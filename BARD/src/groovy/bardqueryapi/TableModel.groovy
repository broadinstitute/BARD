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

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 2/8/13
 * Time: 8:02 PM
 * To change this template use File | Settings | File Templates.
 *
 * Borrowed mostly from DefaultTableModel in java
 *
 */
class TableModel implements Serializable {

    private Map<String, Object> additionalProperties = [:]

    //data (list of rows)
    private List<List<WebQueryValue>> data = []

    /**
     * column Headers
     */
    private List<WebQueryValue> columnHeaders = []

    /**
     * Creates an empty table
     */
    public TableModel() {
    }


    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperties(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    /**
     * Creates an empty table
     */
    public void setColumnHeaders(List<WebQueryValue> columnHeaders) {
        this.columnHeaders = columnHeaders
    }
    /**
     * Creates an empty table
     */
    public TableModel(List<WebQueryValue> columnHeaders, List<List<WebQueryValue>> data) {
        this.columnHeaders = columnHeaders
        this.data = data
    }
    /**
     * Returns the list containing the row data for the table.
     *
     * @returns The data list.
     */
    public List<List<WebQueryValue>> getData() {
        return this.data;
    }
/**
 * Returns the list containing the row data for the table.
 *
 * @returns The data list.
 */
    public List<WebQueryValue> getColumnHeaders() {
        return this.columnHeaders;
    }
/**
 * Adds a column with the specified name and data values to the table.
 *
 * @param columnHeader the column name (<code>null</code> permitted).
 * @param columnData the column data.
 */
    public void addColumn(WebQueryValue columnHeader, List<WebQueryValue> columnData) {
        if (!columnHeader) {
            return
        }
        if (!columnData) {
            return
        }
        for (int i = 0; i < columnData.size(); ++i) { //add the list of rows
            data.add(new ArrayList<WebQueryValue>(new ArrayList<WebQueryValue>()))
        }
        for (int i = 0; i < data.size(); ++i) { //add the columns
            ((List<WebQueryValue>) data.get(i)).add(columnData[i]);
        }
        columnHeaders.add(columnHeader);
    }

/**
 * Adds a new row containing the specified data to the table
 \     *
 * @param rowData the row data (<code>null</code> permitted).
 */
    public void addRowData(List<WebQueryValue> rowData) {
        if (!rowData) {
            return
        }
        data.add(rowData);
    }

/**
 * getRowCount
 * @returns int
 */
    public int getRowCount() {
        return data.size();
    }

/**
 * Returns the number of columns in the model.
 *
 * @return The column count.
 */
    public int getColumnCount() {
        return (columnHeaders ? columnHeaders.size() : 0);
    }

/**
 * Returns the name of the specified column.
 *
 * @param column the column index.
 *
 * @returns The column name.
 */
    public String getColumnName(int column) {
        String result = "";

        if (column < getColumnCount()) {
            Object id = columnHeaders.get(column);
            if (id != null) {
                result = id.toString();
            }
        }
        return result;
    }

/**
 * Returns the value at the specified cell in the table.
 *
 * @param row the row index.
 * @param column the column index.
 *
 * @returns The value (<code>WebQueryValue</code>, possibly <code>null</code>) at
 *          the specified cell in the table.
 */
    public WebQueryValue getValueAt(int row, int column) {
        if (row < 0 || column < 0) {
            throw new IllegalArgumentException("Column and Row numbers must be >=0")
        }
        if (row >= data.size()) {
            throw new IllegalArgumentException("Row number must not be greater than the data size")
        }
        if (column >= columnHeaders.size()) {
            throw new IllegalArgumentException("Column must not be greater than the column size")
        }
        return data.get(row).get(column)
    }

/**
 * Sets the value for the specified cell in the table and sends a
 *
 * @param value the value (<code>WebQueryValue</code>, <code>null</code> permitted).
 * @param row the row index. - 0 based index
 * @param column the column index.  - o based index
 */
    public void setValueAt(WebQueryValue value, int row, int column) {
        if (row < 0 || column < 0) {
            throw new IllegalArgumentException("Column and Row numbers must be >=0")
        }
        if (row >= data.size()) {
            throw new IllegalArgumentException("Row number must not be greater than the data size")
        }
        if (column >= columnHeaders.size()) {
            throw new IllegalArgumentException("Column must not be greater than the column size")
        }

        ((List<WebQueryValue>) data.get(row)).set(column, value);
    }
}
