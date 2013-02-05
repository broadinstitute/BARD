/**
 * ========================================
 * JFreeReport : a free Java report library
 * ========================================
 *
 * Project Info:  http://reporting.pentaho.org/
 *
 * (C) Copyright 2000-2007, by Object Refinery Limited, Pentaho Corporation and Contributors.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc.
 * in the United States and other countries.]
 *
 * ------------
 * $Id: TypeMapper.java,v 1.8 2007/04/01 18:49:32 taqua Exp $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report.modules.misc.tablemodel;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Ref;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Struct;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;

import org.jfree.util.ObjectUtilities;

/**
 * @author $Author: taqua $
 * @version $Id: TypeMapper.java,v 1.8 2007/04/01 18:49:32 taqua Exp $
 */
public class TypeMapper {
	private static Class byteArrayClass = (new byte[0]).getClass();

	private static Class mapSQLType(final int t) {
		switch (t) {
		case Types.ARRAY:
			return (new Object[0]).getClass();
		case Types.BIGINT:
			return Long.class;
		case Types.BINARY:
			return byteArrayClass;
		case Types.BIT:
			return Boolean.class;
		case Types.BLOB:
			return Blob.class;
		case 16: // Types.BOOLEAN was not part of JDK1.2.2
			return Boolean.class;
		case Types.CHAR:
			return String.class;
		case Types.CLOB:
			return Clob.class;
		case 70: // Types.DATALINK was not part of JDK 1.2.2
			return Object.class;
		case Types.DATE:
			return java.sql.Date.class;
		case Types.DECIMAL:
			return java.math.BigDecimal.class;
		case Types.DISTINCT:
			return Object.class;
		case Types.DOUBLE:
			return Double.class;
		case Types.FLOAT:
			return Double.class;
		case Types.INTEGER:
			return Integer.class;
		case Types.JAVA_OBJECT:
			return Object.class;
		case Types.LONGVARBINARY:
			return byteArrayClass;
		case Types.LONGVARCHAR:
			return String.class;
		case Types.NULL:
			return Object.class;
		case Types.NUMERIC:
			return java.math.BigDecimal.class;
		case Types.OTHER:
			return Object.class;
		case Types.REAL:
			return Float.class;
		case Types.REF:
			return Ref.class;
		case Types.SMALLINT:
			return Short.class;
		case Types.STRUCT:
			return Struct.class;
		case Types.TIME:
			return Time.class;
		case Types.TIMESTAMP:
			return Timestamp.class;
		case Types.TINYINT:
			return Byte.class;
		case Types.VARBINARY:
			return byteArrayClass;
		case Types.VARCHAR:
			return String.class;
		default:
			return Object.class;
		}
	}

	public static Class[] mapTypes(final ResultSetMetaData rsmd) {
		final Class[] types;
		try {
			types = new Class[rsmd.getColumnCount()];
		} catch (SQLException sqle) {
			return null;
		}

		final ClassLoader cl = ObjectUtilities.getClassLoader(TypeMapper.class);
		for (int i = 0; i < types.length; i++) {
			try {
				try {
					final String tn = rsmd.getColumnClassName(i + 1);
					types[i] = cl.loadClass(tn);
				} catch (Exception oops) {
					final int colType = rsmd.getColumnType(i + 1);
					types[i] = mapSQLType(colType);
				}
			} catch (Exception e) {
				types[i] = Object.class;
			}
		}

		return types;
	}

	private TypeMapper() {
	}
}