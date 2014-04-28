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
         * {@link SequenceGenerator#SEQUENCE} name, we assign one based on the
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
