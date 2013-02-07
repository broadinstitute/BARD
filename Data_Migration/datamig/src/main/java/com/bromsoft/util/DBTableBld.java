/*
 * DBTableBld.java
 *
 * Created on November 18, 2000, 10:35 PM
 */

package com.bromsoft.util;
import quick.dbtable.*;
import java.util.*;


/**
 *
 * @author  bromley
 * @version
 */
public class DBTableBld extends Object {
    
    
    public static DBTable  updateDBMethods(String selectString, DBTable table, String tableName, String [] keyArray, boolean nullKey) {
        try {
            table.setSelectSql(selectString);
            table.refresh();
            bldDelete(table, tableName, keyArray);
            bldUpdate(table, tableName, keyArray);
            bldInsert(table, tableName, keyArray, nullKey);
            readOnlyOnKeys(table, tableName, keyArray);
            table.refresh();
            
        } catch (Exception e12) {
            javax.swing.JOptionPane.showMessageDialog(null, e12);
        }  
        return table;
    }
    /*
     * Method to remove commans or comma and blank from a string
     */
    private static String removeEndComma(String stringIn){
        if( stringIn.endsWith(","))
            stringIn = stringIn.substring(0, stringIn.length()-1);
        if( stringIn.endsWith(", "))
            stringIn = stringIn.substring(0, stringIn.length()-2);
        return stringIn;
    }
    
    /*
     * Method to create the DBTable update method from the existing database.
     */
    
    public static DBTable bldUpdate(DBTable table, String filename, String [] keyArray) {
        String updateSql = null;
        String columnPointer = null;
        Hashtable ht1 = new Hashtable();
        try {
            if (table == null)
                table = new DBTable();
            
            table.clearAllUpdateSql();// delete preexisting update instructions
            
            //the following codes should be called after you call the refresh method in dbtable
            updateSql = "update " + filename.trim() + " set ";
            columnPointer="";
            
            for(int i=0; i< table.getTable().getColumnCount(); i++)
            {
                updateSql += " " + table.getColumn(i).getHeaderValue() + "=?,";
                columnPointer += (i+1) + ",";
                Integer ii = new Integer(i+1);
                String head = table.getColumn(i).getHeaderValue()  + " ";
                ht1.put(head.trim().toUpperCase(), new String(ii.toString()));
            }
            
            //remove the final comma in the updateSql
            if( updateSql.endsWith(","))
                updateSql = updateSql.substring(0, updateSql.length()-1);
            
            updateSql += " where ";
            for(int i=0; i < keyArray.length; i++) {
                updateSql += keyArray[i].trim() + "=?,";
                columnPointer += ht1.get(keyArray[i].toUpperCase()) + ",";
            }
            
            //remove the final comma in the updateSql
            updateSql = DBTableBld.removeEndComma(updateSql);
            //remove the final comma in the columnPointer
            columnPointer = DBTableBld.removeEndComma(columnPointer);
            
            table.addUpdateSql(updateSql, columnPointer);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return table;
    }
    
    /*
     * three parameter method to call insert method, defaulting to "false" the requirement
     * to convert key fields to null
     */
    public static DBTable bldInsert(DBTable table, String filename, String [] keyArray) {
        return bldInsert(table, filename, keyArray, false);
    }
    
    /*
     * Method to create the insert method for the DBTable
     */
    public static DBTable bldInsert(DBTable table, String filename, String [] keyArray, boolean nullKey) {
        String updateSql = null;
        String updateSql1 = null;
        String columnPointer = null;
        Hashtable ht1 = new Hashtable();
        
        // put key values into a vector for easier handling.
        Vector hold1 = new Vector();
        for(int x = 0; x < keyArray.length; x++)
            hold1.add(keyArray[x].toUpperCase());
        
        try {
            if (table == null)
                table = new DBTable();
            
            table.clearAllInsertSql();  // clear preexisting insert instructions.
            
            //the following codes should be called after you call the refresh method in dbtable
            updateSql = "insert into " + filename.trim() + " (";
            updateSql1 = " values(";
            columnPointer="";
            
            for(int i=0; i< table.getTable().getColumnCount(); i++)
            {
                String headerValue =  table.getColumn(i).getHeaderValue().toString();
                
                if (nullKey) {
                    if (hold1.contains((String)headerValue.toUpperCase())) {
                        updateSql += " " + "null" + ",";
                    } else {
                        updateSql += " " + headerValue + ",";
                        updateSql1 += "?,";
                        columnPointer += (i+1) + ",";
                    }
                }
                else {
                    updateSql += " " + headerValue + ",";
                    updateSql1 += "?,";
                    columnPointer += (i+1) + ",";
                }
                
                Integer ii = new Integer(i+1);
                String head = table.getColumn(i).getHeaderValue()  + " ";
                ht1.put(head.trim().toUpperCase(), new String(ii.toString()));
            }
            
            //remove the final comma in the updateSql, updateSql1, and columnPointer
            //remove the final comma in the updateSql
            updateSql = DBTableBld.removeEndComma(updateSql);
            updateSql1 = DBTableBld.removeEndComma(updateSql1);
            columnPointer = DBTableBld.removeEndComma(columnPointer);
            
            updateSql += ") ";
            updateSql1 += ") ";
            updateSql = updateSql + updateSql1;
            
            table.addInsertSql(updateSql, columnPointer);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return table;
    }
    
    /*
     * method to create the delete method from the existing dbtable.
     */
    
    public static DBTable bldDelete(DBTable table, String filename, String [] keyArray) {
        String deleteSql = null;
        String columnPointer = null;
        Hashtable ht1 = new Hashtable();
        try {
            if (table == null)
                table = new DBTable();
            table.clearAllDeleteSql();  // clear preexisting delete instructions.
            //the following codes should be called after you call the refresh method in dbtable
            deleteSql = "delete from " + filename.trim() + " where ";
            columnPointer="";
            for(int i=0; i< table.getTable().getColumnCount(); i++)
            {
                Integer ii = new Integer(i+1);
                String head = table.getColumn(i).getHeaderValue()  + " ";
                ht1.put(head.trim().toUpperCase(), new String(ii.toString()));
            }
            
            //remove the final comma in the updateSql
            if( deleteSql.endsWith(","))
                deleteSql = deleteSql.substring(0, deleteSql.length()-1);
            
            for(int i=0; i < keyArray.length; i++) {
                deleteSql += keyArray[i].trim() + "=?,";
                columnPointer += ht1.get(keyArray[i].toUpperCase()) + ",";
            }
            
            //remove the final comma in the updateSql
            deleteSql = DBTableBld.removeEndComma(deleteSql);
            //remove the final comma in the columnPointer
            columnPointer = DBTableBld.removeEndComma(columnPointer);
            
            table.addDeleteSql(deleteSql, columnPointer);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return table;
    }
    
    public static DBTable readOnlyOnKeys(DBTable table, String filename, String [] keyArray) {
        
        // put key values into a vector for easier handling.
        Vector hold1 = new Vector();
        for(int x = 0; x < keyArray.length; x++)
            hold1.add(keyArray[x].toUpperCase());
        
        try {
            if (table == null)
                table = new DBTable();
            //the following codes should be called after you call the refresh method in dbtable
            
            for(int i=0; i< table.getTable().getColumnCount(); i++)
            {
                String headerValue =  table.getColumn(i).getHeaderValue().toString();
                if (hold1.contains((String)headerValue.toUpperCase())) {
                    table.getColumn(i).setReadOnly(true);
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return table;
    }
}