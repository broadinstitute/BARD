package gov.ncgc;
// $Id: Assay.java 2278 2008-05-29 22:27:45Z nguyenda $

import java.io.InputStream;
import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

import java.util.Enumeration;
import java.util.Vector;
import java.util.BitSet;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class Assay implements Serializable {
    private static final long serialVersionUID = 12242007;

    // activity outcome method
    public static enum AOM { 
   	    Other, 
	    Primary,  // single point concentration
	    Confirmatory,  // dose response concentration
	    Summary;

	static public AOM getInstance (int ord) {
	    for (AOM e : values ()) {
		if (ord == e.ordinal()) {
		    return e;
		}
	    }
	    return Other;
	}
    };

    public static final int DISPLAY_AID = 1;
    public static final int DISPLAY_NAME = 2;
    public static final int DISPLAY_SOURCEID = 4;

    protected int aid; // assay unique's id
    protected String name; // short descriptive name for the assay
    protected String sourceID;
    protected String sourceName;
    protected String description;
    protected String protocol;
    protected String comment;
    protected AOM outcomeMethod = AOM.Other;
    protected int activitySummaryTID = 0;
    protected List<Integer> genes = new ArrayList<Integer>();
    protected List<Integer> aids = new ArrayList<Integer>();
    protected List<ResultType> results = new ArrayList<ResultType>();
    protected String url;
    protected String grant;
    protected int category; // project category
    protected List<Long> pmids = new ArrayList<Long>();

    protected int displayOptions = DISPLAY_AID | DISPLAY_NAME;

    public Assay () {}
    public Assay (int aid) { this.aid = aid; }
    public Assay (String name) { this.name = name; }
    
    public int getAID () { return aid; }
    public void setAID (int aid) { this.aid = aid; }
    public String getName () { return name; }
    public void setName (String name) { this.name = name; }
    public String getSourceID () { return sourceID; }
    public void setSourceID (String sourceID) { this.sourceID = sourceID; }
    public String getSourceName () { return sourceName; }
    public void setSourceName (String sourceName) { 
	this.sourceName = sourceName; 
    }
    public String getDescription () { return description; }
    public void setDescription (String desc) { this.description = desc; }
    public String getProtocol () { return protocol; }
    public void setProtocol (String protocol) { this.protocol = protocol; }
    public String getComment () { return comment; }
    public void setComment (String comment) { this.comment = comment; }
    public AOM getOutcomeMethod () { return outcomeMethod; }
    public void setOutcomeMethod (AOM aom) { outcomeMethod = aom; }

    public void setURL (String url) { this.url = url; }
    public String getURL () { return url; }

    public void addGene (int gene) { genes.add(gene); }
    public List<Integer> getGenes () { return genes; }

    public void addAID (int aid) { aids.add(aid); }
    public List<Integer> getAIDs () { return aids; }

    public void addPublication (long pmid) { pmids.add(pmid); }
    public List<Long> getPublications () { return pmids; }

    public void setGrant (String grant) { this.grant = grant; }
    public String getGrant () { return grant; }

    public void setCategory (int category) { this.category = category; }
    public int getCategory () { return category; }

    public int getActivitySummaryTID () { return activitySummaryTID; }
    public void setActivitySummaryTID (int tid) { activitySummaryTID = tid; }

    public void setDisplayOptions (int masks) { displayOptions = masks; }
    public boolean checkDisplay (int masks) {
	return (displayOptions & masks) == masks;
    }

    public static String getAssayPathName (Assay[] assay) {
	StringBuffer sb = new StringBuffer ();
	for (int i = 0; i < assay.length; ++i) {
	    sb.append("/" + i + ":" + assay[i].getName());
	}
	return sb.toString();
    }

    public static String[] parseAssayPathName (String pathname) {
	char []array = pathname.toCharArray();

	BitSet bs = new BitSet (array.length);
	for (int i = 0; i < array.length-1; ++i) {
	    if (array[i] == '/' && Character.isDigit(array[i+1])) {
		int j = i+1;
		for (; j < array.length-1; ++j) {
		    if (!Character.isDigit(array[j])) {
			if (array[j] != ':') {
			    j = i;
			}
			break;
		    }
		}
		if (j > i) {
		    for (int k = i; k <= j; ++k) {
			bs.set(k);
		    }		    
		}
		i = j;
	    }
	}

	Vector<String> path = new Vector<String>();
	for (int i = 0, j = 0; i >= 0 && i < pathname.length(); i = j) {
	    i = bs.nextClearBit(i+1);
	    j = bs.nextSetBit(i+1);
	    path.add(pathname.substring(i, j<0 ? pathname.length() : j));
	}
	
	return path.toArray(new String[0]);
    }

    public void addResult (ResultType result) {
	if (result.isActiveConcentration()) {
	    setActivitySummaryTID (result.getTID());
	}
	results.add(result);
    }
    public Enumeration<ResultType> getResults () { 
	return Collections.enumeration(results); 
    }
    public ResultType getResult (int tid) {
	for (ResultType t : results) {
	    if (t.getTID() == tid) {
		return t;
	    }
	}
	return null;
    }
    public int getResultSize () { return results.size(); }

    public String toString () { 
	if (checkDisplay (DISPLAY_AID | DISPLAY_NAME | DISPLAY_SOURCEID)) {
	    if (aid != 0 && name != null && sourceID != null) {
		return "(" + aid + ") " + name + " [" + sourceID + "]";
	    }
	}
	if (checkDisplay (DISPLAY_AID | DISPLAY_NAME)) {
	    if (aid != 0 && name != null) {
		return "(" + aid + ") " + name;
	    }
	}
	if (name != null) {
	    return name;
	}
	return "";
    }

    public String toVerboseString () {
	StringBuilder sb = new StringBuilder 
	    ("[AID="+aid+",Name="+name+",SourceID="+sourceID
	     +",SourceName="+sourceName+",Description="+description
	     +",Protocol="+protocol+",Comment="+comment
	     +",OutcomeMethod=" + outcomeMethod+",URL="+url+",Grant="
	     + getGrant()+",Category="+getCategory()+",Genes=[");
	if (!genes.isEmpty()) {
	    sb.append(genes.get(0));
	    for (int i = 1; i < genes.size(); ++i) {
		sb.append(","+genes.get(i));
	    }
	}
	sb.append("]");
	sb.append(",AID=[");
	if (!aids.isEmpty()) {
	    sb.append(aids.get(0));
	    for (int i = 1; i < aids.size(); ++i) {
		sb.append(","+aids.get(i));
	    }
	}
	sb.append("]");
	sb.append(",PMID=[");
	if (!pmids.isEmpty()) {
	    sb.append(pmids.get(0));
	    for (int i = 1; i < pmids.size(); ++i) {
		sb.append(","+pmids.get(i));
	    }
	}
	sb.append("],Columns=" +results.size());
	for (ResultType r : results) {
	    sb.append("," + r);
	}
	sb.append("]");
	return sb.toString();
    }

    public boolean equals (Object obj) {
	if (obj instanceof Assay) {
	    Assay assay = (Assay)obj;
	    if (aid != 0 && aid == assay.aid) {
		return true;
	    }
	    else if (name != null && name.equals(assay.name)) {
		return true;
	    }
	}
	return false;
    }


    private void writeObject (ObjectOutputStream out) throws IOException {
	out.writeInt(aid);
	out.writeObject(name);
	out.writeObject(sourceID);
	out.writeObject(sourceName);
	out.writeObject(description);
	out.writeObject(protocol);
	out.writeObject(comment);
	out.writeInt(outcomeMethod.ordinal());
	out.writeInt(activitySummaryTID);
	out.writeInt(results.size());
	for (ResultType rt : results) {
	    out.writeObject(rt);
	}
	out.writeInt(displayOptions);
	out.writeObject(url);
	out.writeInt(genes.size());
	for (Integer gi : genes) {
	    out.writeInt(gi);
	}
	out.writeInt(aids.size());
	for (Integer a : aids) {
	    out.writeInt(a);
	}
	out.writeObject(grant);
	out.writeInt(category);
	out.writeInt(pmids.size());
	for (Long p : pmids) {
	    out.writeLong(p);
	}
    }

    private void readObject (ObjectInputStream in) 
	throws IOException, ClassNotFoundException {
	aid = in.readInt();
	name = (String)in.readObject();
	sourceID = (String)in.readObject();
	sourceName = (String)in.readObject();
	description = (String)in.readObject();
	protocol = (String)in.readObject();
	comment = (String)in.readObject();
	outcomeMethod = AOM.getInstance(in.readInt());
	activitySummaryTID = in.readInt();
	results = new ArrayList<ResultType>();
	int size = in.readInt();
	for (int i = 0; i < size; ++i) {
	    ResultType rt = (ResultType)in.readObject();
	    results.add(rt);
	}
	displayOptions = in.readInt();
	url = (String)in.readObject();
	genes = new ArrayList<Integer>();
	size = in.readInt();
	for (int i = 0; i < size; ++i) {
	    genes.add(in.readInt());
	}
	size = in.readInt();
	aids = new ArrayList<Integer>();
	for (int i = 0; i < size; ++i) {
	    aids.add(in.readInt());
	}
	grant = (String)in.readObject();
	category = in.readInt();
	pmids = new ArrayList<Long>();
	size = in.readInt();
	for (int i = 0; i < size; ++i) {
	    pmids.add(in.readLong());
	}
    }

    public static void main (String argv[]) throws Exception {
	if (argv.length == 0) {
	    System.out.println("Assay BIOASSAY...");
	    System.exit(1);
	}
	for (int i = 0; i < argv.length; ++i) {
	    Assay assay = PubChemAssayParser.parseBioassayXML
		(new java.io.FileInputStream (argv[i]));
	    System.out.println(assay.toVerboseString());
	}
    }
}
