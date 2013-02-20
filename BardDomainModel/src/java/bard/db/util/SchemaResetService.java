package bard.db.util;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 2/19/13
 * Time: 10:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class SchemaResetService {
    private String prefix;
    private DataSource dataSource;

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void restoreSchema() {
        try {
            Connection connection = dataSource.getConnection();
            SchemaReset.restoreBaseline(connection, prefix);
            connection.close();
        } catch(SQLException exc) {
            throw new RuntimeException(exc);
        }
    }
}
