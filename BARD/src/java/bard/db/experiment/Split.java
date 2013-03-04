package bard.db.experiment;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import bard.db.dictionary.Element;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 3/1/13
 * Time: 3:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class Split {
    Map<Long, Long> resultToExperiment = new HashMap();
    static String GLOBAL_PREFIX = "/Users/pmontgom/data/result-deposition-export/";

    static class WritePerExperiment {
        public String[] header;
        String prefix;

        WritePerExperiment(String prefix) {
            this.prefix = prefix;
        }

        Map<Long, CSVWriter> writers = new HashMap();

        public void write(long expId, String [] row) throws Exception {
            CSVWriter w = writers.get(expId);
            if(w == null) {
                File file = new File(GLOBAL_PREFIX+"/"+expId+"/"+prefix);
                if(!file.getParentFile().exists())
                    file.getParentFile().mkdirs();
                w = new CSVWriter(new BufferedWriter(new FileWriter(file)));
                w.writeNext(header);
                writers.put(expId, w);
            }
            w.writeNext(row);
        }

        public void close() throws Exception {
            for(CSVWriter w : writers.values()) {
                w.close();
            }
        }
    }

    Float getFloat(String s) {
        if(s.isEmpty())
            return null;
        else
            return Float.parseFloat(s);
    }

    Integer getInteger(String s) {
        if(s.isEmpty())
            return null;
        else
            return Integer.parseInt(s);
    }

    Long getLong(String s) {
        if(s.isEmpty())
            return null;
        else
            return Long.parseLong(s);
    }

    String getString(String s) {
        if (s.isEmpty())
            return null;
        return s;
    }

    void readResults(String fn) throws Exception {
        Reader r = new FileReader(fn);
        CSVReader reader = new CSVReader(r);
        List<String> cols = Arrays.asList(reader.readNext());

        int idI = cols.indexOf("RESULT_ID");
        int expI = cols.indexOf("EXPERIMENT_ID");
        int rti = cols.indexOf("RESULT_TYPE_ID");
        int subi = cols.indexOf("SUBSTANCE_ID");
        int stati = cols.indexOf("STATS_MODIFIER_ID");
        int repi = cols.indexOf("REPLICATE_NO");
        int qi  = cols.indexOf("QUALIFIER");
        int vi = cols.indexOf("VALUE_NUM");
        int vmini = cols.indexOf("VALUE_MIN");
        int vmaxi = cols.indexOf("VALUE_MAX");
        int vdi = cols.indexOf("VALUE_DISPLAY");

        WritePerExperiment w = new WritePerExperiment("results");
        w.header = cols.toArray(new String[0]);

        int count = 0;
        while(true) {
            String [] line = reader.readNext();
            if(line == null)
                break;

            count ++;

            if ((count%1000) == 0) {
                System.out.println(count);
            }
            long expId = Long.parseLong(line[expI]);
            w.write(expId, line);
            resultToExperiment.put(Long.parseLong(line[idI]), expId);
        }

        w.close();
        r.close();
    }

    void readHierarchy(String fn) throws Exception {
        Reader r = new FileReader(fn);
        CSVReader reader = new CSVReader(r);
        List<String> cols = Arrays.asList(reader.readNext());
         int childIdI = cols.indexOf("RESULT_ID");
        int parentIdI = cols.indexOf("PARENT_RESULT_ID");
        int hTypeI = cols.indexOf("HIERARCHY_TYPE");

        WritePerExperiment w = new WritePerExperiment("hierarchy");
        w.header = cols.toArray(new String[0]);

        while(true) {
            String [] line = reader.readNext();
            if(line == null)
                break;
            if (line.length < 2)
                continue;

            long expId = resultToExperiment.get(getLong(line[childIdI]));
            w.write(expId, line);
        }
        w.close();
        r.close();
    }

    void readItems(String fn) throws Exception {
        Reader r = new FileReader(fn);
        CSVReader reader = new CSVReader(r);
        List<String> cols = Arrays.asList(reader.readNext());
        int ri = cols.indexOf("RESULT_ID");
        int ai = cols.indexOf("ATTRIBUTE_ID");
        int vi = cols.indexOf("VALUE_ID");
        int qi = cols.indexOf("QUALIFIER");
        int vni = cols.indexOf("VALUE_NUM");
        int vmini = cols.indexOf("VALUE_MIN");
        int vmaxi = cols.indexOf("VALUE_MAX");
        int vdi  = cols.indexOf("VALUE_DISPLAY");

        WritePerExperiment w = new WritePerExperiment("items");
        w.header = cols.toArray(new String[0]);

        while(true) {
            String [] line = reader.readNext();
            if(line == null)
                break;
            if (line.length < 2)
                continue;

            long expId = resultToExperiment.get(getLong(line[ri]));
            w.write(expId, line);
        }
        w.close();
        r.close();
    }

    public static void main(String args[]) throws  Exception {
       // test = System.properties.hasProperty("testload")
        boolean test = true;
        String dir = "/Users/pmontgom/data/result-deposition-export";

        Split x = new Split();
            x.readResults(dir+"/results-export.csv");
            x.readHierarchy(dir+"/hierarchy-export.csv");
            x.readItems(dir+"/item-export.csv");
    }
}
