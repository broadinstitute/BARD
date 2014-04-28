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

package bard.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Close quietly
 * 
 * @author southern
 * 
 */
public class SQLUtil {
	private static Logger log = LoggerFactory.getLogger(SQLUtil.class);

	public static void closeQuietly(Connection connection) {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			log.error("An error occurred closing connection.", e);
		}
	}

	public static void closeQuietly(Statement statement) {
		try {
			if (statement != null) {
				statement.close();
			}
		} catch (SQLException e) {
			log.error("An error occurred closing statement.", e);
		}
	}

	public static void closeQuietly(ResultSet resultSet) {
		try {
			if (resultSet != null) {
				resultSet.close();
			}
		} catch (SQLException e) {
			log.error("An error occurred closing result set.", e);
		}
	}

	public static String inClauseParams(int size) {
		StringBuilder sb = new StringBuilder(size * 2);
		sb.append("?");
		for (int ii = 1; ii <= size - 1; ii++)
			sb.append(",?");
		return sb.toString();
	}

	public static void fillInClause(PreparedStatement ps, int startIndex, Collection<?> clauses, int sqlType) throws SQLException {
		fillInClause(ps, startIndex, clauses.size(), clauses.iterator(), sqlType);
	}
	
	public static void fillInClause(PreparedStatement ps, int startIndex, int maxSize, Iterator<?> clauses, int sqlType) throws SQLException {
		int counter = 0;
		for (; clauses.hasNext(); ++counter) {
			ps.setObject(startIndex++, clauses.next(), sqlType);
			if (counter >= maxSize)
				break;
		}
		for (int ii = maxSize; ii > counter; ii--)
			ps.setNull(startIndex++, sqlType);
	}
}
