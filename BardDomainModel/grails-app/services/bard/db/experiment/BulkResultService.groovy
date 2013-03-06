package bard.db.experiment

import bard.db.dictionary.Element

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Types

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 3/6/13
 * Time: 10:53 AM
 * To change this template use File | Settings | File Templates.
 */
class BulkResultService {

    private List<Long> allocateIds(Connection connection, String sequence, int count) {
        List<Long> ids = new ArrayList(count)

        PreparedStatement statement = connection.prepareStatement("select ${sequence}.nextval from dual connect by level <= ${count}",
                ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)
        try {
            statement.setFetchSize(count);
            ResultSet rs = statement.executeQuery();
            try {
                for(int i=0;i<count;i++) {
                    if (!rs.next()) {
                        throw new RuntimeException("failed to advance sequence after ${i} rows");
                    }
                    ids.add(rs.getLong(1))
                }

            } finally {
                rs.close()
            }
        } finally {
            statement.close()
        }

        return ids
    }


    private void insertHierarchies(Connection connection, Collection<ResultHierarchy> relationships, String username) {
        String query = "INSERT INTO RESULT_HIERARCHY (RESULT_HIERARCHY_ID," +
                "RESULT_ID, PARENT_RESULT_ID, HIERARCHY_TYPE," +
                "VERSION,DATE_CREATED,LAST_UPDATED,MODIFIED_BY) VALUES (RESULT_HIERARCHY_ID_SEQ.NEXTVAL, ?,?,?, 1,sysdate,sysdate,?";

        PreparedStatement statement = connection.prepareStatement(query)
        try {
            for(relationship in relationships) {
                statement.setLong(1, relationship.result.id)
                statement.setLong(2, relationship.parentResult.id)
                statement.setString(3, relationship.hierarchyType.value)

                statement.setString(4, username)
            }
        } finally {
            statement.close()
        }
    }

    private void insertItems(Connection connection, Collection<ResultContextItem> items, String username) {
        String query = "INSERT INTO RSLT_CONTEXT_ITEM (RSLT_CONTEXT_ITEM_ID," +
                "RESULT_ID, ATTRIBUTE_ID, VALUE_ID," +
                "DISPLAY_ORDER, EXT_VALUE_ID, QUALIFIER," +
                "VALUE_NUM, VALUE_MIN, VALUE_MAX, " +
                "VALUE_DISPLAY, " +
                "VERSION, DATE_CREATED, LAST_UPDATED, MODIFIED_BY) VALUES ( RSLT_CONTEXT_ITEM_ID_SEQ.NEXTVAL, "+
                "?,?,?, ?,?,?, ?,?,?, ?,1, sysdate, sysdate, ?)"

        PreparedStatement statement = connection.prepareStatement(query)
        try {
            for (item in items) {
                statement.setLong(1, item.result.id)
                statement.setLong(2, item.attributeElement.id)
                statement.setObject(3, item.valueElement?.id, Types.BIGINT)

                statement.setNull(4, Types.INTEGER)
                statement.setNull(5, Types.INTEGER)
                statement.setString(6, item.qualifier)

                statement.setObject(7, item.valueNum, Types.FLOAT)
                statement.setObject(8, item.valueMin, Types.FLOAT)
                statement.setObject(9, item.valueMax, Types.FLOAT)

                statement.setString(10, item.valueDisplay)
                statement.setString(11, username)

                statement.addBatch()
            }
            statement.executeBatch()
        } finally {
            statement.close()
        }
    }

    private void insertResults(Connection connection, Experiment experiment, Collection<Result> results, String username) {
        String query = "INSERT INTO RESULT (" +
                "RESULT_STATUS, READY_FOR_EXTRACTION, EXPERIMENT_ID," +
                "RESULT_TYPE_ID, SUBSTANCE_ID, STATS_MODIFIER_ID," +
                "REPLICATE_NO, QUALIFIER, VALUE_NUM," +
                "VALUE_MIN, VALUE_MAX, VALUE_DISPLAY," +
                "VERSION, DATE_CREATED, LAST_UPDATED," +
                "MODIFIED_BY, RESULT_ID) VALUES (?,?,?, ?,?,?, ?,?,?, ?,?,?, 1, sysdate, sysdate, ?, ?)"

        PreparedStatement statement = connection.prepareStatement(query)
        try {
            for (result in results) {
                statement.setString(1, result.resultStatus)
                statement.setString(2, result.readyForExtraction.name())
                statement.setObject(3, experiment.id)

                statement.setLong(4, result.resultType.id)
                statement.setLong(5, result.substance.id)
                statement.setObject(6, result.statsModifier?.id, Types.BIGINT)

                statement.setObject(7, result.replicateNumber, Types.INTEGER)
                statement.setString(8, result.qualifier)
                statement.setObject(9, result.valueNum, Types.FLOAT)

                statement.setObject(10, result.valueMin, Types.FLOAT)
                statement.setObject(11, result.valueMax, Types.FLOAT)
                statement.setString(12, result.valueDisplay)

                statement.setString(13, username)
                statement.setLong(14, result.id)

                statement.addBatch()
            }

            statement.executeBatch()
        } finally {
            statement.close()
        }
    }

    private void assignIdsToResults(Connection connection, List<Result> results) {
        List<Long> ids = allocateIds(connection, results.size())
        for(int i=0;i<results.size();i++) {
            results.get(i).id = ids.get(i)
        }
    }

    void insert(Experiment experiment, Collection<Result> results) {
        String username;
        Connection connection;

        assignIdsToResults(connection, results)
        insertResults(connection, experiment, results, username)

        // flatten all items and do a bulk insert
        List items = results.collectMany([], {Result result -> result.resultContextItems})
        insertItems(connection, items, username)

        // flatten and sort all hierarchies
        List relationships = results.collectMany([], {Result result -> result.resultHierarchiesForParentResult})
        insertHierarchies(connection, relationships, username)
    }

    void deleteResults(Experiment experiment) {

    }

    void find(Experiment experiment, Long sid) {
        String query = "SELECT "+
                "RESULT_STATUS, READY_FOR_EXTRACTION, EXPERIMENT_ID," +
                "RESULT_TYPE_ID, SUBSTANCE_ID, STATS_MODIFIER_ID," +
                "REPLICATE_NO, QUALIFIER, VALUE_NUM," +
                "VALUE_MIN, VALUE_MAX, VALUE_DISPLAY," +
                "RESULT_ID "+
                "FROM RESULT WHERE EXPERIMENT_ID = ?"

        List<List> columns = queryColumns(URLConnection, query, [experiment.id])
        int rowCount = columns.get(0).size()

        List<String> resultStatuses = columns.get(0)
        List<String> readyForExtracts = columns.get(1)
        List<Experiment> experiments = columns.get(2).collect { Long id -> Experiment.get(id)}

        List<Element> resultTypes = columns.get(3).collect { Long id -> Element.get(id) }
        List<Substance> substances = columns.get(4).collect { Long id -> Element.get(id) }
        List<Element> statsModifiers = columns.get(4).collect { Long id -> Element.get(id) }

        List<Integer> replicateNos = columns.get(5)
        List<String> qualifiers = columns.get(6)
        List<Float> valueNums = columns.get(7)

        List<Float> valueMins = columns.get(8)
        List<Float> valueMaxs = columns.get(9)
        List<Float> valueDisplays = columns.get(10)

        List<Long> resultIds = columns.get(11)

        for(int i=0;i<rowCount;i++) {
            Result r = new Result()
            r.resultStatus = resultStatuses.get(i)
            r.readyForExtraction = readyForExtracts.get(i)
            r.experiment = experiments.get(i)
            r.resultType = resultTypes.get(i)
            r.substance = substances.get(i)
            r.statsModifier = statsModifiers.get(i)
            r.replicateNumber = replicateNos.get(i)
            r.qualifier = qualifiers.get(i)
            r.valueNum = valueNums.get(i)
            r.valueMin = valueMins.get(i)
            r.valueMax = valueMaxs.get(i)
            r.valueDisplay = valueDisplays.get(i)
            r.id = resultIds.get(i)
        }

        Map resultById = resultStatuses.collectEntries { [it.id, it] }

        populateHierarchy(connection, experiment, resultById)
        populateContextItems(connection, experiment, resultById)
    }

    private void populateContextItems(Connection connection, Experiment experiment, Map<Long, Result> resultById) {
        String query = "SELECT RESULT_ID, ATTRIBUTE_ID, VALUE_ID," +
                "DISPLAY_ORDER, EXT_VALUE_ID, QUALIFIER," +
                "VALUE_NUM, VALUE_MIN, VALUE_MAX, " +
                "VALUE_DISPLAY FROM RSLT_CONTEXT_ITEM WHERE ..."

        List<List> columns = queryColumns(connection, query, [experiment.id])
        int rowCount = columns.get(0).size()

        List<Result> results = columns.get(0).collect { Long id -> resultById[id] }
        List<Element> attributes = columns.get(1).collect { Long id -> Element.get(id) }
        List<Element> values = columns.get(2).collect { Long id -> Element.get(id) }

        List<String> qualifiers = columns.get(4)

        List<Float> valueNum = columns.get(5)
        List<Float> valueMin = columns.get(6)
        List<Float> valueMax = columns.get(7)

        List<String> valueDisplay = columns.get(8)

        for(int i=0;i<rowCount;i++) {
            ResultContextItem item = new ResultContextItem();
            item.result = results.get(i)
            item.result.resultContextItems.add(item)

            item.attributeElement = attributes.get(i)
            item.valueElement = values.get(i)
            item.qualifier = qualifiers.get(i)
            item.valueNum = valueNum.get(i)
            item.valueMin = valueMin.get(i)
            item.valueMax = valueMax.get(i)
            item.valueDisplay = valueDisplay.get(i)
        }
    }

    private void populateHierarchy(Connection connection, Experiment experiment, Map<Long, Result> resultById) {
        List<List> columns = queryColumns(connection, "SELECT RESULT_ID, PARENT_RESULT_ID, HIERARCHY_TYPE FROM RESULT_HIERARCHY WHERE ...", [experiment.id])
        int rowCount = columns.get(0).size()

        List<Result> results = columns.get(0).collect { Long id -> resultById[id] }
        List<Result> parentResults = columns.get(1).collect { Long id -> resultById[id] }
        String resultHierarchies = columns.get(2)

        for(int i=0;i<rowCount;i++) {
            ResultHierarchy rh = new ResultHierarchy()

            rh.parentResult = parentResults.get(i)
            rh.parentResult.resultHierarchiesForParentResult.add(rh)

            rh.result = results.get(i)
            rh.result.resultHierarchiesForResult.add(rh)

            rh.hierarchyType = HierarchyType.getByValue(resultHierarchies.get(i))
        }
    }

    private List<List> queryColumns(Connection connection, String query, List args) {
        Map internedStrings = [:]
        PreparedStatement statement = connection.prepareStatement(query)
        try {
            ResultSet resultSet = statement.executeQuery()
            int columnCount = resultSet.getMetaData().getColumnCount()
            List<List> result = new ArrayList(columnCount)
            for(int i=0;i<columnCount;i++) {
                result.add(new ArrayList())
            }
            try {
                while(resultSet.next()) {
                    for(int i=0;i<columnCount;i++) {
                        Object record = resultSet.getObject(columnCount+1)
                        if (record instanceof String) {
                            // collapse strings to point to a single instance (just to save memory)
                            if (internedStrings.containsKey(record)) {
                                record = internedStrings.get(record)
                            } else {
                                internedStrings.put(record, record)
                            }
                        }
                        result.get(i).add(record)
                    }
                }
                return result
            } finally {
                resultSet.close()
            }

        } finally {
            statement.close();
        }
    }
}
