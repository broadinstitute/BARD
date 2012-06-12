package bard;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.id.PersistentIdentifierGenerator;
import org.hibernate.id.SequenceGenerator;
import org.hibernate.type.Type;

import java.util.Properties;

public class SequencePerTableOracleDialect extends Oracle10gDialect {
    public static final String PARAMETERS = "MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 NOCACHE NOCYCLE";

    /**
     * Get the native identifier generator class.
     *
     * @return TableNameSequenceGenerator.
     */
    @Override
    public Class<?> getNativeIdentifierGeneratorClass() {
        return TableNameSequenceGenerator.class;
    }

    /**
     * Creates a sequence per table instead of the default behavior of one
     * sequence.
     */
    public static class TableNameSequenceGenerator extends SequenceGenerator {

        /**
         * {@inheritDoc} If the parameters do not contain a
         * {@link org.hibernate.id.SequenceGenerator#SEQUENCE} name, we assign one based on the
         * table name.
         */
        @Override
        public void configure(final Type type, final Properties params,
                              final Dialect dialect) {
            if (params.getProperty(SEQUENCE) == null
                    || params.getProperty(SEQUENCE).length() == 0) {
                /* Sequence per table */
                String tableName = params
                        .getProperty(PersistentIdentifierGenerator.TABLE);
                if (tableName != null) {
                    params.setProperty(SEQUENCE, createSequenceName(tableName));
                }
                /* Non-Caching Sequence */
                params.setProperty(PARAMETERS, SequencePerTableOracleDialect.PARAMETERS);
            }
            super.configure(type, params, dialect);
        }

        /**
         * Construct a sequence name from a table name.
         *
         * @param tableName
         *            the table name
         * @return the sequence name
         */
        String createSequenceName(final String tableName) {
            return tableName+"_id_seq";
        }
    }
}