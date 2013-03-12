package bard.db.experiment

import org.hibernate.Session
import org.hibernate.jdbc.Work

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 3/12/13
 * Time: 9:52 AM
 * To change this template use File | Settings | File Templates.
 */
class BulkSubstanceService {
    int batchSize = 100;

    Collection<Long> findMissingSubstances(Collection<Long> sids) {
        Collection<Long> missing;

        Substance.withSession {
            Session session ->
                session.doWork(new Work() {
                    @Override
                    void execute(Connection connection) throws SQLException {
                        missing = findMissingSubstances(connection, sids)
                    }
                })
        }

        return missing
    }

    static List<List> partition (List items, int partitionSize) {
        List partitions = new ArrayList()
        for(int i=0;i<items.size();i+=partitionSize) {
            int end = i+partitionSize;
            if (end > items.size())
                end = items.size()
            partitions.add(new ArrayList(items.subList(i,end)))
        }
        return partitions
    }

    Collection<Long> findMissingSubstances(Connection connection, Collection<Long> sids) {
        // divide into batches
        List<List<Long>> batches = partition(new ArrayList(sids), batchSize)
        List<Long> missing = batches.collectMany(new ArrayList()) {
            Collection<Long> batch -> findBatchOfMissingSubstances(connection, batch)
        }

        return missing
    }

    Collection<Long> findBatchOfMissingSubstances(Connection connection, Collection<Long> sids) {
        Set<Long> missing = new HashSet(sids)

        StringBuilder query = new StringBuilder("SELECT SUBSTANCE_ID FROM SUBSTANCE WHERE SUBSTANCE_ID IN (")
        for(int i=0;i<sids.size();i++) {
            if (i != 0)
                query.append(",")
            query.append("?")
        }
        query.append(")")

        PreparedStatement statement = connection.prepareStatement(query.toString())
        try {
            for(int i=0;i<sids.size();i++) {
                statement.setLong(i+1, sids[i])
            }
            ResultSet resultSet = statement.executeQuery()
            try {
                while(resultSet.next()) {
                    missing.remove(resultSet.getLong(1))
                }
            } finally {
                resultSet.close()
            }
        } finally {
            statement.close()
        }

        return missing
    }

    void insertSubstances(Connection connection, Collection<Long> sids, String user) {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO SUBSTANCE (SUBSTANCE_ID, VERSION, DATE_CREATED, LAST_UPDATED, MODIFIED_BY) VALUES (?, 1, sysdate, sysdate, ?)")
        try {
            for(int i=0;i<sids.size();i++) {
                statement.setLong(1, sids[i])
                statement.setString(2, user)
                statement.addBatch()
            }
            statement.executeBatch()
        } finally {
            statement.close()
        }
    }

    void insertSubstances(Collection<Long> sids, String user) {
        Substance.withSession {
            Session session ->
                session.doWork(new Work() {
                    @Override
                    void execute(Connection connection) throws SQLException {
                        insertSubstances(connection, sids, user)
                    }
                })
        }
    }
}
