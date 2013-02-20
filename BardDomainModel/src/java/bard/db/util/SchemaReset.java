package bard.db.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 2/19/13
 * Time: 9:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class SchemaReset {
    static final private Logger log = Logger.getLogger(SchemaReset.class);

    static int MAX_NAME_LENGTH = 30;

    static class MapOfSets extends HashMap<String, Set> {
        @Override
        public Set get(Object o) {
            Set s =  super.get(o);
            if(s == null) {
                s = new HashSet();
                put((String)o, s);
            }
            return s;
        }
    }

    static String shorten(String name) {
        if (name.length() > MAX_NAME_LENGTH){
            name = name.substring(0, MAX_NAME_LENGTH-15)+"_"+ DigestUtils.md5Hex(name.getBytes()).substring(0,14);
        }

        return name;
    }

    static String getShadowName(String prefix, String tableName) {
        return shorten(prefix+"_"+tableName);
    }

    static String getTriggerName(String prefix, String tableName) {
        return shorten(prefix+"_TR_"+tableName);
    }

    static void restoreBaseline(Connection connection, String prefix) throws SQLException {
        Statement statement = connection.createStatement();
        log.info("Restoring baseline");

        // populate map with all constraints dependant on a table
        MapOfSets constraintsPerTable = new MapOfSets();

        List<Object[]> constraintInfos = executeQuery(statement, "select constraint_name, child_table, parent_table from "+prefix+"_FKS");
        for(Object[] row : constraintInfos) {
            String constraintName = (String)row[0];
            String childTable = (String)row[1];
            String  parentTable = (String)row[2];

            constraintsPerTable.get(childTable).add(childTable+"."+constraintName);
            constraintsPerTable.get(parentTable).add(childTable+"."+constraintName);
        }

        List<String> tables = executeQuery(statement,"select TABLE_NAME from "+prefix+"_status where dirty = 'Y'");

        // disable all fks
        for(String tableName :tables) {
            Collection<String> constraintNames = constraintsPerTable.get(tableName);
            log.info("disabling constraints: "+constraintNames);
            for(String fullConstraintName : constraintNames) {
                String [] parts = fullConstraintName.split("\\.");
                statement.execute("ALTER TABLE \"" + parts[0] + "\" DISABLE CONSTRAINT \"" + parts[1] + "\"");
            }
        }

        // drop and restore tables
        log.info("Restoring the following tables: "+tables);
        for(String tableName :tables) {
            statement.execute("DELETE FROM \"" + tableName + "\"");
            statement.execute("INSERT INTO \""+tableName+"\" SELECT * FROM \"" + getShadowName(prefix, tableName) + "\"");
        }

        // enable all fks
        for(String tableName :tables) {
            Collection<String> constraintNames = constraintsPerTable.get(tableName);
            log.info("enabling "+constraintNames.size()+" constraints");
            for(String fullConstraintName : constraintNames) {
                String [] parts = fullConstraintName.split("\\.");

                statement.execute("ALTER TABLE \"" + parts[0] + "\" ENABLE NOVALIDATE CONSTRAINT \"" + parts[1] + "\"");
            }
        }

        statement.execute("UPDATE "+prefix+"_status set DIRTY = 'N'");
        connection.commit();

        log.info("Baseline restore complete");
        statement.close();
    }
/*
    static void dropBaseline(Session session, String prefix) {
        SQLQuery query = session.createSQLQuery("select TABLE_NAME from "+prefix+"_status");
        query.setCacheable(false);
        List<String> tables = query.list();
        for(String tableName :tables) {
            session.createSQLQuery("drop table "+getShadowName(prefix, tableName)+"").executeUpdate();
            session.createSQLQuery("DROP TRIGGER "+getTriggerName(prefix, tableName)+"").executeUpdate();
        }
        session.createSQLQuery("drop table "+prefix+"_FKS").executeUpdate();
        session.createSQLQuery("drop table "+prefix+"_STATUS").executeUpdate();
    }
*/
    static void createOrReplace(Statement statement, String createSql, String dropSql) throws SQLException {
        try {
            statement.execute(createSql);
        } catch(SQLException ex) {
            statement.execute(dropSql);
            statement.execute(createSql);
        }
    }

    static List executeQuery(Statement statement, String sql) throws SQLException {
        List result = new ArrayList();

        ResultSet resultSet = statement.executeQuery(sql);
        int columnCount = resultSet.getMetaData().getColumnCount();
        while(resultSet.next()) {
            if(columnCount == 1) {
                result.add(resultSet.getString(1));
            } else {
                Object [] row = new Object[columnCount];
                for(int i=0;i<columnCount;i++) {
                    row[i] = resultSet.getString(i+1);
                }
                result.add(row);
            }
        }
        resultSet.close();

        return result;
    }

    static  public void createBaseline(Connection connection, String prefix, Collection<String> exclusions) throws SQLException {
        Statement statement = connection.createStatement();

        // create list of tables we pay attention to
        String createStatusTableSql = "create table " + prefix + "_STATUS as select TABLE_NAME, 'N' DIRTY from USER_TABLES where TABLE_NAME not like '" + prefix + "%'";
        if(exclusions.size() > 0) {
            createStatusTableSql += " and TABLE_NAME NOT IN ('"+StringUtils.join(exclusions, "','")+"')";
        }

        createOrReplace(statement, createStatusTableSql, "drop table " + prefix + "_STATUS");
        statement.execute("create index "+prefix+"_STATUS_IDX on "+prefix+"_STATUS (TABLE_NAME)");

        // store the list of foreign keys we might need to worry about
        createOrReplace(statement, "create table "+prefix+"_FKS as select c.constraint_name, c.table_name child_table, p.table_name parent_table from USER_CONSTRAINTS c join user_constraints p on p.constraint_name = c.r_constraint_name where c.constraint_Type = 'R'",
                "drop table "+prefix+"_FKS");

        // create copy of the table's current state
        // and create a trigger for each

        List<String> tables = executeQuery(statement, "select TABLE_NAME from " + prefix + "_status");

        for(String tableName : tables) {
            createOrReplace(statement, "create table "+getShadowName(prefix, tableName)+" as select * from "+tableName+"", "drop table "+getShadowName(prefix, tableName)+"");
            statement.execute("CREATE OR REPLACE TRIGGER "+getTriggerName(prefix, tableName)+" \n" +
                    "AFTER INSERT OR DELETE OR UPDATE ON \""+tableName+"\" \n" +
                    "BEGIN\n" +
                    "  UPDATE "+prefix+"_STATUS SET DIRTY = 'Y' WHERE TABLE_NAME = '"+tableName+"';\n" +
                    "END;");
        }
        statement.close();
    }
}
