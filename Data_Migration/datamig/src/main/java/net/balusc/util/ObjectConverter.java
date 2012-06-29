/*
 * net/balusc/util/ObjectConverter.java
 * 
 * Copyright (C) 2007 BalusC
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package net.balusc.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.commons.io.IOUtils;

/**
 * Generic object converter.
 * <p>
 * <h3>Use examples</h3>
 * 
 * <pre>
 * Object o1 = Boolean.TRUE;
 * Integer i = ObjectConverter.convert(o1, Integer.class);
 * System.out.println(i); // 1
 * 
 * Object o2 = "false";
 * Boolean b = ObjectConverter.convert(o2, Boolean.class);
 * System.out.println(b); // false
 * 
 * Object o3 = new Integer(123);
 * String s = ObjectConverter.convert(o3, String.class);
 * System.out.println(s); // 123
 * </pre>
 * 
 * Not all possible conversions are implemented. You can extend the <tt>ObjectConverter</tt>
 * easily by just adding a new method to it, with the appropriate logic. For example:
 * 
 * <pre>
 * public static ToObject fromObjectToObject(FromObject fromObject) {
 *     // Implement.
 * }
 * </pre>
 * 
 * The method name doesn't matter. It's all about the parameter type and the return type.
 * 
 * @author BalusC
 * @link http://balusc.blogspot.com/2007/08/generic-object-converter.html
 */
public final class ObjectConverter {

    // Init ---------------------------------------------------------------------------------------

    private static final Map<MultiKey, Method> CONVERTERS = new HashMap<MultiKey, Method>();
    
    static {
        // Preload converters.
        Method[] methods = ObjectConverter.class.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getParameterTypes().length == 1) {
                // Converter should accept 1 argument. This skips the convert() method.
//                CONVERTERS.put(method.getParameterTypes()[0].getName() + "_" + method.getReturnType().getName(), method);
            	CONVERTERS.put(new MultiKey(method.getParameterTypes()[0], method.getReturnType()), method);
            }
        }
    }

    private ObjectConverter() {
        // Utility class, hide the constructor.
    }

    // Action -------------------------------------------------------------------------------------

    /**
     * Convert the given object value to the given class.
     * @param from The object value to be converted.
     * @param to The type class which the given object should be converted to.
     * @return The converted object value.
     * @throws NullPointerException If 'to' is null.
     * @throws UnsupportedOperationException If no suitable converter can be found.
     * @throws RuntimeException If conversion failed somehow. This can be caused by at least
     * an ExceptionInInitializerError, IllegalAccessException or InvocationTargetException.
     */
    public static <T> T convert(Object from, Class<T> to) {
        // Null is just null.
        if (from == null)
            return null;

        Class fromClass = from.getClass();
        // Can we cast? Then just do it.
        if (to.isAssignableFrom(fromClass))
            return to.cast(from);

        // Lookup the suitable converter.
        MultiKey key = new MultiKey(fromClass,to);
        Method converter = CONVERTERS.get(key);
        if( converter == null )
        	for(Class clazz: fromClass.getInterfaces()) {
        		key = new MultiKey(clazz,to);
        		converter = CONVERTERS.get(key);
        		if( converter != null )
        			break;
        	}
        if (converter == null) {
        	Class clazz = fromClass;
        	while( ! clazz.equals(Object.class) ) {
        		Class superClass = clazz.getSuperclass();
        		key = new MultiKey(superClass,to);
        		converter = CONVERTERS.get(key);
        		if( converter != null )
        			break;
        		clazz = superClass;
        	}
       	}
    	if( converter == null )
            throw new UnsupportedOperationException("Cannot convert from " 
                + fromClass.getName() + " to " + to.getName()
                + ". Requested converter does not exist.");

        // Convert the value.
        try {
            return to.cast(converter.invoke(to, from));
        } catch (Exception e) {
            throw new RuntimeException("Cannot convert from " 
                + fromClass.getName() + " to " + to.getName()
                + ". Conversion failed with " + e.getMessage(), e);
        }
    }
    
    @SuppressWarnings("unchecked")
    public static void convertList(List list, Class toClass) {
    	ListIterator li = list.listIterator();
    	while(li.hasNext()) {
    		li.set(convert(li.next(), toClass));
    	}
    }
    

    // Converters ---------------------------------------------------------------------------------

    /**
     * Converts Integer to Boolean. If integer value is 0, then return FALSE, else return TRUE.
     * @param value The Integer to be converted.
     * @return The converted Boolean value.
     */
    public static Boolean integerToBoolean(Integer value) {
        return value.intValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
    }

    /**
     * Converts Boolean to Integer. If boolean value is TRUE, then return 1, else return 0.
     * @param value The Boolean to be converted.
     * @return The converted Integer value.
     */
    public static Integer booleanToInteger(Boolean value) {
        return value.booleanValue() ? Integer.valueOf(1) : Integer.valueOf(0);
    }

    /**
     * Converts Double to BigDecimal.
     * @param value The Double to be converted.
     * @return The converted BigDecimal value.
     */
    public static BigDecimal doubleToBigDecimal(Double value) {
        return new BigDecimal(value.doubleValue());
    }

    /**
     * Converts BigDecimal to Double.
     * @param value The BigDecimal to be converted.
     * @return The converted Double value.
     */
    public static Double bigDecimalToDouble(BigDecimal value) {
        return new Double(value.doubleValue());
    }

    public static Integer numberToInteger(Number value) {
    	return value.intValue();
    }
    
    public static Long numberToLong(Number value) {
    	return value.longValue();
    }
    
    /**
     * Converts Integer to String.
     * @param value The Integer to be converted.
     * @return The converted String value.
     */
    public static String integerToString(Integer value) {
        return value.toString();
    }

    /**
     * Converts String to Integer.
     * @param value The String to be converted.
     * @return The converted Integer value.
     */
    public static Integer stringToInteger(String value) {
        return Integer.valueOf(value);
    }
    
    public static Long stringToLong(String value) {
        return Long.valueOf(value);
    }

    /**
     * Converts Boolean to String.
     * @param value The Boolean to be converted.
     * @return The converted String value.
     */
    public static String booleanToString(Boolean value) {
        return value.toString();
    }
    
    public static String objectToString(Object obj) {
    	return obj.toString();
    }

    /**
     * Converts String to Boolean.
     * @param value The String to be converted.
     * @return The converted Boolean value.
     */
    public static Boolean stringToBoolean(String value) {
        return Boolean.valueOf(value);
    }
    
    public static String byteArrayToString(byte[] bytes) {
    	return new String(bytes);
    }
    
    public static String blobToString(Blob blob) throws IOException, SQLException {
    	InputStream is = blob.getBinaryStream();
		String str = IOUtils.toString(is);
		is.close();
		return str;
	}
    
    public static String clobToString(Clob clob) throws IOException, SQLException {
    	Reader reader = clob.getCharacterStream();
    	String str = IOUtils.toString(reader);
    	reader.close();
    	return str;
	}
	
    // You can implement more converter methods here.

}