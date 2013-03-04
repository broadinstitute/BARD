package bard.db.experiment;

import au.com.bytecode.opencsv.CSVReader;
import bard.db.dictionary.Element;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
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
public class Reconstitute {
    Map<Long,Result> results = new HashMap();
    Map<Long, Element> elements = new HashMap();
    Map<Long, Substance> substances =new HashMap<Long, Substance>();
    Map<Long, Experiment> experiments = new HashMap<Long, Experiment>();

    Substance getSubstance(String idstr) {
        Long id = getLong(idstr);
        if(id == null)
            return null;

        Substance substance = substances.get(id);
        if(substance == null) {
            substance = new Substance();
                    substance.setId(id);
            substances.put(id, substance);
        }
        return substance;
    }

    Experiment getExperiment(String idstr) {
        Long id = getLong(idstr);
        if(id == null)
            return null;

        Experiment experiment = experiments.get(id);
        if(experiment == null) {
            experiment = new Experiment();
            experiment.setId(id);
            experiments.put(id, experiment);
        }
        return experiment;

    }

    Element getElement(String idstr) {
        Long id = getLong(idstr);
        if(id == null)
            return null;

        Element e = elements.get(id);
        if(e == null)
            throw new RuntimeException();
        return e;
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

        int count = 0;
        while(true) {
            String [] line = reader.readNext();
            if(line == null)
                break;

            count ++;

            if ((count%1000) == 0) {
                System.out.println(count);
            }

            Result rr = new Result();
            rr.setId(getLong(line[idI]));
            rr.setExperiment(getExperiment(line[expI]));
            rr.setResultType(getElement(line[rti]));
            rr.setSubstance(getSubstance(line[subi]));
            rr.setStatsModifier(getElement(line[stati]));
            rr.setReplicateNumber(getInteger(line[repi]));
            rr.setQualifier(line[qi]);
            rr.setValueNum(getFloat(line[vi]));
            rr.setValueMin(getFloat(line[vmini]));
            rr.setValueMax(getFloat(line[vmaxi]));
            rr.setValueDisplay(line[vdi]);
            results.put(rr.getId(), rr);
        }

        r.close();
    }

    void readHierarchy(String fn) throws Exception {
        if(!new File(fn).exists())
        {
            System.out.println("does not exist: "+fn);
            return;
        }
        Reader r = new FileReader(fn);
        CSVReader reader = new CSVReader(r);
        List<String> cols = Arrays.asList(reader.readNext());
        int childIdI = cols.indexOf("RESULT_ID");
        int parentIdI = cols.indexOf("PARENT_RESULT_ID");
        int hTypeI = cols.indexOf("HIERARCHY_TYPE");

        int dropped = 0;
        while(true) {
            String [] line = reader.readNext();
            if(line == null)
                break;
            if (line.length < 2)
                continue;

            Result result = results.get(getLong(line[childIdI]));
            if(result == null)
                continue;
            Result parent = results.get(getLong(line[parentIdI]));

            if(parent == result) {
                dropped ++;
                continue;
            }

            ResultHierarchy h = new ResultHierarchy();
            h.setResult(result);
            h.setParentResult(parent);

            h.getResult().getResultHierarchiesForResult().add(h);
            h.getParentResult().getResultHierarchiesForParentResult().add(h);
        }
        r.close();

        System.out.println("dropped "+dropped+" self links");
    }

    void readElements(String fn) throws Exception {
        Reader r = new FileReader(fn);
        CSVReader reader = new CSVReader(r);
        List<String> cols = Arrays.asList(reader.readNext());

        int idI = cols.indexOf("ELEMENT_ID");
        int lI = cols.indexOf("LABEL");

        while(true) {
            String [] line = reader.readNext();
            if(line == null)
                break;
            Element element = new Element();
                  element.setId(getLong(line[idI]));
            element.setLabel(getString(line[lI]));
            elements.put(element.getId(), element);
        }
        r.close();
    }

    void readItems(String fn) throws Exception {
        if(!new File(fn).exists())
        {
            System.out.println("does not exist: "+fn);
            return;
        }
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
        while(true) {
            String [] line = reader.readNext();
            if(line == null)
                break;
            if (line.length < 2)
                continue;

            Result result = results.get(getLong(line[ri]));
                        ResultContextItem item = new ResultContextItem();
            if(result != null) {
                item.setResult(result);
                item.getResult().getResultContextItems().add(item);

                item.setAttributeElement(getElement(line[ai]));
                item.setValueElement(getElement(line[vi]));
                item.setQualifier(getString(line[qi]));
                item.setValueNum(getFloat(line[vni]));
                item.setValueMin(getFloat(line[vmini]));
                item.setValueMax(getFloat(line[vmaxi]));
                item.setValueDisplay(getString(line[vdi]));
            }
        }
        r.close();
    }

    public static void main(String args[]) throws  Exception {
       // test = System.properties.hasProperty("testload")
        boolean test = true;
        String dir = "/Users/pmontgom/data/result-deposition-export";

        for(String exp : new String[] {"2596","3375","35","4120","3014","3397","36","334","3402","37"}) {
        Reconstitute x = new Reconstitute();
        if (true) {
            x.readElements(dir+"/elements.csv");
            x.readResults(dir+"/"+exp+"/results");
            x.readHierarchy(dir + "/"+exp+"/hierarchy");
            x.readItems(dir+"/"+exp+"/items");
        } else {
            x.readResults(dir+"/results-export.csv");
            x.readHierarchy(dir+"/hierarchy-export.csv");
            x.readItems(dir+"/item-export.csv");
        }

        ResultsExportService service = new ResultsExportService();
        service.dumpFromListToAbsPath(new File("export-"+exp+".gz"), x.results.values());
        System.out.println("Total "+x.results.size()+" results");
        }
    }
}
