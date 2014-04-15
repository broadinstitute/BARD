package adf.exp

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 4/9/14
 * Time: 1:57 PM
 * To change this template use File | Settings | File Templates.
 */
class BoxRow {
    final long resultId;
    final long parentResultId;
    final Map<ResultKey, String> data = [:];

    public BoxRow(long resultId, long parentResultId) {
        this.resultId = resultId;
        this.parentResultId = parentResultId;
    }

    public void put(ResultKey key, String value) {
        data[key] = value;
    }

    public String getAt(ResultKey key) {
        return data[key]
    }
}
