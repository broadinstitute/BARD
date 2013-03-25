package bard.db.experiment

import bard.db.dictionary.Element
import bard.db.enums.ReadyForExtraction
import org.hibernate.Session
import org.hibernate.jdbc.Work

import javax.sql.DataSource
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import java.sql.Types

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 3/6/13
 * Time: 10:53 AM
 * To change this template use File | Settings | File Templates.
 */
class BulkResultService {
    final static int BATCH_SIZE = 10000;

    void insertResults(String username, Experiment experiment, Collection<Result> results) {
        Experiment.withSession {
            Session session ->

            session.doWork(new Work() {
                @Override
                void execute(Connection connection) throws SQLException {
                    assignIdsToResults(connection, results)
                    insertOnlyResults(connection, experiment, results, username)

                    // flatten all items and do a bulk insert
                    List items = results.collectMany([], {Result result -> result.resultContextItems})
                    insertItems(connection, items, username)

                    // flatten and sort all hierarchies
                    List relationships = results.collectMany([], {Result result -> result.resultHierarchiesForParentResult})
                    insertHierarchies(connection, relationships, username)
                }
            })
        }
    }

    void deleteResults(Experiment experiment) {
        Experiment.withSession {
            Session session ->

                session.doWork(new Work() {
                    @Override
                    void execute(Connection connection) throws SQLException {
            executeUpdateWithArgs(connection, "DELETE FROM RSLT_CONTEXT_ITEM rci WHERE EXISTS (SELECT 1 FROM RESULT r WHERE r.RESULT_ID = rci.RESULT_ID AND r.EXPERIMENT_ID = ?)", [experiment.id])
            executeUpdateWithArgs(connection, "DELETE FROM RESULT_HIERARCHY rh WHERE EXISTS (SELECT 1 FROM RESULT r WHERE r.RESULT_ID = rh.RESULT_ID AND r.EXPERIMENT_ID = ?)", [experiment.id])
            executeUpdateWithArgs(connection, "DELETE FROM RESULT r WHERE r.EXPERIMENT_ID = ?", [experiment.id])
                    }
                }
                )
        }
    }

    List<Result> findResults(Experiment experiment) {
        List<Result> results

        Experiment.withSession {
            Session session ->

                session.doWork(new Work() {
                    @Override
                    void execute(Connection connection) throws SQLException {
                        results = loadResults(connection, experiment)
                    }
                })
        }

        return results
    }

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
        String query = "INSERT INTO RESULT_HIERARCHY (RESULT_HIERARCHY_ID, " +
                "RESULT_ID, PARENT_RESULT_ID, HIERARCHY_TYPE," +
                "VERSION, DATE_CREATED, LAST_UPDATED, " +
                "MODIFIED_BY) VALUES (RESULT_HIERARCHY_ID_SEQ.NEXTVAL, ?,?,?, 1,sysdate,sysdate, ?)";

        PreparedStatement statement = connection.prepareStatement(query)
        try {
            int inBatch = 0;
            for(relationship in relationships) {
                if (inBatch > BATCH_SIZE) {
                    println(""+new Date()+" writing ${inBatch} hierarchy records");
                    statement.executeBatch();
                }

                statement.setLong(1, relationship.result.id)
                statement.setLong(2, relationship.parentResult.id)
                statement.setString(3, relationship.hierarchyType.value)

                statement.setString(4, username)
                statement.addBatch()
                inBatch++;
            }
            statement.executeBatch()
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
            int inBatch = 0;
            for (item in items) {
                if (inBatch > BATCH_SIZE) {
                    println(""+new Date()+" writing ${inBatch} items");
                    statement.executeBatch();
                }

                int displayOrder = 0 // Not defined because owning collection is a set

                statement.setLong(1, item.result.id)
                statement.setLong(2, item.attributeElement.id)
                statement.setObject(3, item.valueElement?.id, Types.BIGINT)

                statement.setInt(4, displayOrder)
                statement.setNull(5, Types.INTEGER)
                statement.setString(6, item.qualifier)

                statement.setObject(7, item.valueNum, Types.FLOAT)
                statement.setObject(8, item.valueMin, Types.FLOAT)
                statement.setObject(9, item.valueMax, Types.FLOAT)

                statement.setString(10, item.valueDisplay)
                statement.setString(11, username)

                statement.addBatch()
                inBatch++;
            }
            statement.executeBatch()
        } finally {
            statement.close()
        }
    }

    private void insertOnlyResults(Connection connection, Experiment experiment, Collection<Result> results, String username) {
        String query = "INSERT INTO RESULT (" +
                "RESULT_STATUS, READY_FOR_EXTRACTION, EXPERIMENT_ID," +
                "RESULT_TYPE_ID, SUBSTANCE_ID, STATS_MODIFIER_ID," +
                "REPLICATE_NO, QUALIFIER, VALUE_NUM," +
                "VALUE_MIN, VALUE_MAX, VALUE_DISPLAY," +
                "VERSION, DATE_CREATED, LAST_UPDATED," +
                "MODIFIED_BY, RESULT_ID) VALUES (?,?,?, ?,?,?, ?,?,?, ?,?,?, 1, sysdate, sysdate, ?, ?)"

        PreparedStatement statement = connection.prepareStatement(query)
        try {
            int inBatch = 0;
            for (result in results) {
                if (inBatch > BATCH_SIZE) {
                    println(""+new Date()+" writing ${inBatch} results");
                    statement.executeBatch();
                }

                statement.setString(1, result.resultStatus)
                statement.setString(2, result.readyForExtraction.id)
                statement.setObject(3, experiment.id)

                statement.setLong(4, result.resultType.id)
                statement.setLong(5, result.substanceId)
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
                inBatch++;
            }

            statement.executeBatch()
        } finally {
            statement.close()
        }
    }

    private void assignIdsToResults(Connection connection, Collection<Result> results) {
        List<Long> ids = allocateIds(connection, "RESULT_ID_SEQ", results.size())
        List<Result> ordered = new ArrayList(results);
        for(int i=0;i<ordered.size();i++) {
            ordered.get(i).id = ids.get(i)
        }
    }

    private void executeUpdateWithArgs(Connection connection, String sql, List args) {
        PreparedStatement statement = connection.prepareStatement(sql);
        try {
            for(int i=0;i<args.size();i++) {
                statement.setObject(i+1, args[i])
            }
            statement.executeUpdate()
        } finally {
            statement.close()
        }
    }

    private List<Result> loadResults(Connection connection, Experiment experiment) {
        String query = "SELECT "+
                "RESULT_STATUS, READY_FOR_EXTRACTION, EXPERIMENT_ID," +
                "RESULT_TYPE_ID, SUBSTANCE_ID, STATS_MODIFIER_ID," +
                "REPLICATE_NO, QUALIFIER, VALUE_NUM," +
                "VALUE_MIN, VALUE_MAX, VALUE_DISPLAY," +
                "RESULT_ID "+
                "FROM RESULT WHERE EXPERIMENT_ID = ?"

        List<List> columns = queryColumns(connection, query, [experiment.id], [
                Types.VARCHAR, Types.VARCHAR, Types.BIGINT,
                Types.BIGINT, Types.BIGINT, Types.BIGINT,
                Types.INTEGER, Types.VARCHAR, Types.FLOAT,
                Types.FLOAT, Types.FLOAT, Types.VARCHAR,
                Types.BIGINT])
        int rowCount = columns.get(0).size()

        List<String> resultStatuses = columns.get(0)
        List<String> readyForExtracts = columns.get(1)
        List<Experiment> experiments = columns.get(2).collect { Long id -> Experiment.get(id)}

        List<Element> resultTypes = columns.get(3).collect { Long id -> Element.get(id) }
        List<Long> substanceIds = columns.get(4)
        List<Element> statsModifiers = columns.get(5).collect { Long id -> Element.get(id) }

        List<Integer> replicateNos = columns.get(6)
        List<String> qualifiers = columns.get(7)
        List<Float> valueNums = columns.get(8)

        List<Float> valueMins = columns.get(9)
        List<Float> valueMaxs = columns.get(10)
        List<Float> valueDisplays = columns.get(11)

        List<Long> resultIds = columns.get(12)

        List<Result> results = new ArrayList(rowCount)
        for(int i=0;i<rowCount;i++) {
            Result r = new Result()
            r.resultStatus = resultStatuses.get(i)
            r.readyForExtraction = ReadyForExtraction.byId(readyForExtracts.get(i))
            r.experiment = experiments.get(i)
            r.resultType = resultTypes.get(i)
            r.substanceId = substanceIds.get(i)
            r.statsModifier = statsModifiers.get(i)
            r.replicateNumber = replicateNos.get(i)
            r.qualifier = qualifiers.get(i)
            r.valueNum = valueNums.get(i)
            r.valueMin = valueMins.get(i)
            r.valueMax = valueMaxs.get(i)
            r.valueDisplay = valueDisplays.get(i)
            r.id = resultIds.get(i)

            results.add(r)
        }

        Map resultById = results.collectEntries { [it.id, it] }

        populateHierarchy(connection, experiment, resultById)
        populateContextItems(connection, experiment, resultById)

        return results
    }

    private void populateContextItems(Connection connection, Experiment experiment, Map<Long, Result> resultById) {
        String query = "SELECT RESULT_ID, ATTRIBUTE_ID, VALUE_ID," +
                "DISPLAY_ORDER, EXT_VALUE_ID, QUALIFIER," +
                "VALUE_NUM, VALUE_MIN, VALUE_MAX, " +
                "VALUE_DISPLAY FROM RSLT_CONTEXT_ITEM rci " +
                "WHERE EXISTS (SELECT 1 FROM RESULT r where rci.RESULT_ID = r.RESULT_ID AND r.EXPERIMENT_ID = ?)"

        List<List> columns = queryColumns(connection, query, [experiment.id],
            [Types.BIGINT, Types.BIGINT, Types.BIGINT,
            Types.INTEGER, Types.VARCHAR, Types.VARCHAR,
            Types.FLOAT, Types.FLOAT, Types.FLOAT,
            Types.VARCHAR])
        int rowCount = columns.get(0).size()

        List<Result> results = columns.get(0).collect { Long id -> resultById[id] }
        List<Element> attributes = columns.get(1).collect { Long id -> Element.get(id) }
        List<Element> values = columns.get(2).collect { Long id -> Element.get(id) }

        List<String> qualifiers = columns.get(5)

        List<Float> valueNum = columns.get(6)
        List<Float> valueMin = columns.get(7)
        List<Float> valueMax = columns.get(8)

        List<String> valueDisplay = columns.get(9)

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
        List<List> columns = queryColumns(connection,
                "SELECT RESULT_ID, PARENT_RESULT_ID, HIERARCHY_TYPE FROM RESULT_HIERARCHY rh WHERE EXISTS (SELECT 1 FROM RESULT r where rh.RESULT_ID = r.RESULT_ID AND r.EXPERIMENT_ID = ?)", [experiment.id],
            [Types.BIGINT, Types.BIGINT, Types.VARCHAR])
        int rowCount = columns.get(0).size()

        List<Result> results = columns.get(0).collect { Long id -> resultById[id] }
        List<Result> parentResults = columns.get(1).collect { Long id -> resultById[id] }
        List<String> resultHierarchies = columns.get(2)

        for(int i=0;i<rowCount;i++) {
            ResultHierarchy rh = new ResultHierarchy()

            rh.parentResult = parentResults.get(i)
            rh.parentResult.resultHierarchiesForParentResult.add(rh)

            rh.result = results.get(i)
            rh.result.resultHierarchiesForResult.add(rh)

            rh.hierarchyType = HierarchyType.getByValue(resultHierarchies.get(i))
        }
    }

    private List<List> queryColumns(Connection connection, String query, List args, List<Types> types) {
        Map internedStrings = [:]
        PreparedStatement statement = connection.prepareStatement(query)
        for(int i=0;i<args.size();i++) {
            statement.setObject(i+1, args[i])
        }

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
                        int t = types[i]
                        def v
                        if (t == Types.BIGINT) {
                            v = resultSet.getLong(i+1)
                            if (resultSet.wasNull())
                                v = null;
                        } else if (t == Types.INTEGER) {
                            v = resultSet.getInt(i+1)
                            if (resultSet.wasNull())
                                v = null;
                        } else if (t == Types.FLOAT) {
                            v = resultSet.getFloat(i+1)
                            if (resultSet.wasNull())
                                v = null;
                        } else if (t == Types.VARCHAR) {
                            String record = resultSet.getString(i+1)
                            // collapse strings to point to a single instance (just to save memory)
                            if (internedStrings.containsKey(record)) {
                                record = internedStrings.get(record)
                            } else {
                                internedStrings.put(record, record)
                            }
                            v = record

                        } else {
                            throw new RuntimeException("Unknown type ${t} in column ${i}")
                        }
                        result.get(i).add(v)
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
