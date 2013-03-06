/*
 * Copyright 2012 Sebastian Prehn <sebastian.prehn@planetswebdesign.de>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package bard.db.enums.hibernate;

import bard.db.enums.IEnumUserType;
import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Based the implementation code found at https://code.google.com/p/hibernate-enum-usertype/
 * <p/>
 * Modified it so, subclasses really, only need to pass in their type, no need for creating a map to handle
 * converting.
 *
 * @author Sebastian Prehn <sebastian.prehn@planetswebdesign.de>
 * @author Dan Durkin <ddurkin@broadinstitute.org>
 */
public abstract class AbstractEnumUserType<E extends IEnumUserType> implements UserType {

    private final Class<E> clazz;
    private static final int[] SQL_TYPES = {Types.VARCHAR};

    public AbstractEnumUserType(final Class<E> clazz) {
        this.clazz = clazz;
    }

    @Override
    public int[] sqlTypes() {
        return SQL_TYPES;
    }

    @Override
    public Class<?> returnedClass() {
        return this.clazz;
    }

    @Override
    public Object nullSafeGet(final ResultSet resultSet, final String[] names, final Object owner) throws HibernateException, SQLException {
        String value = resultSet.getString(names[0]);
        if(StringUtils.isNotBlank(value)){
            return convert(value);
        }
        return null;
    }

    @Override
    public void nullSafeSet(final PreparedStatement preparedStatement, final Object value, final int index) throws HibernateException, SQLException {
        if (null == value) {
            preparedStatement.setNull(index, Types.VARCHAR);
        } else {
            preparedStatement.setString(index, convert((E) value));
        }
    }

    @Override
    public Object deepCopy(final Object value) throws HibernateException {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Object assemble(final Serializable cached, final Object owner) throws HibernateException {
        return cached;
    }

    @Override
    public Serializable disassemble(final Object value) throws HibernateException {
        return (Serializable) value;
    }

    @Override
    public Object replace(final Object original, final Object target, final Object owner) throws HibernateException {
        return original;
    }

    @Override
    public int hashCode(final Object x) throws HibernateException {
        return x == null ? 0 : x.hashCode();
    }

    @Override
    public boolean equals(final Object x, final Object y) throws HibernateException {
        if (x == y) {
            return true;
        }
        if (null == x || null == y) {
            return false;
        }
        return x.equals(y);
    }

    private E convert(final String value) {
        for (E e : this.clazz.getEnumConstants()) {
            if (StringUtils.equals(e.getId(), value)) {
                return e;
            }
        }
        throw new IllegalArgumentException("unkown value: " + value);
    }

    private String convert(final E value) {
        return value.getId();
    }

}
