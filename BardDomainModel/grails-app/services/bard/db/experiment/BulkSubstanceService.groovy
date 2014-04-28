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
