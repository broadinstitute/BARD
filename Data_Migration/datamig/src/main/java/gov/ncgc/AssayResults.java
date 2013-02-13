package gov.ncgc;
// $Id: AssayResults.java 2278 2008-05-29 22:27:45Z nguyenda $

import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Collections;

public class AssayResults implements Constants, Serializable, 
                                     Comparable<AssayResults> {
    private static final long serialVersionUID = 12242007;

    /*
      universal unique identifier identifying an instance of this class
    */
    private long uid; 

    /*
      external/source substance ID
    */
    protected String sid;

    /*
      Data Annotation/Qualifier and URL to further Depositor Information
      Annotation or qualifier for this Result
    */
    protected String comment;

    /*
      inactive	-    Substance is considered Inactive
      active	-    Substance is considered Active
      inconclusive	-    Substance is Inconclusive
      unspecified	-    Substance Outcome is Unspecified
    */
    protected Outcome outcome = Outcome.Unspecified;

    /*
      Rank of Assay Outcome (for result ordering)
        Note: Larger numbers are more active
    */
    protected int rank = 0;

    /*
      Depositor provided URL for this Result
    */
    protected String url;

    /*
      Assay Data Reported for this SID (vector)
    */
    protected List<AssayData> data = new ArrayList<AssayData>();


    public AssayResults () {}
    public AssayResults (long uid) { 
	this.uid = uid;
    }

    public long getUID () { return uid; }
    public void setUID (long uid) { this.uid = uid; }

    public String getSID () { return sid; }
    public void setSID (String sid) { this.sid = sid; }

    public void setComment (String comment) { this.comment = comment; }
    public String getComment () { return comment; }

    public void setOutcome (Outcome outcome) { this.outcome = outcome; }
    public Outcome getOutcome () { return outcome; }

    public void setRank (int rank) { this.rank = rank; }
    public int getRank () { return rank; }

    public void setURL (String url) { this.url = url; }
    public String getURL () { return url; }

    public void addData (AssayData data) {
	this.data.add(data);
    }
    public Enumeration<AssayData> getData () { 
	return Collections.enumeration(data); 
    }
    public AssayData getData (int tid) {
	for (AssayData d : data) {
	    if (d.getTID() == tid) {
		return d;
	    }
	}
	return null;
    }
    public AssayData getDataAt (int pos) {
	return data.get(pos);
    }
    public int getDataSize () { return data.size(); }

    public int compareTo (AssayResults ar) {
	if (ar == null) return 1;
	return getRank () - ar.getRank();
    }

    private void writeObject (ObjectOutputStream out) throws IOException {
	out.writeLong(uid);
	out.writeObject(sid);
	out.writeObject(comment);
	out.writeInt(outcome.ordinal());
	out.writeInt(rank);
	out.writeObject(url);
	out.writeInt(data.size());
	for (AssayData ad : data) {
	    out.writeObject(ad);
	}
    }

    private void readObject (ObjectInputStream in) 
	throws IOException, ClassNotFoundException {
	uid = in.readLong();
	sid = (String)in.readObject();
	comment = (String)in.readObject();
	int ord = in.readInt();
	for (Outcome o : Outcome.values()) {
	    if (o.ordinal() == ord) {
		outcome = o;
		break;
	    }
	}
	rank = in.readInt();
	url = (String)in.readObject();
	int size = in.readInt();
	data = new ArrayList<AssayData>();
	for (int i = 0; i < size; ++i) {
	    AssayData ad = (AssayData)in.readObject();
	    data.add(ad);
	}
    }

    public String toString () {
	StringBuffer sb = new StringBuffer 
	    ("[UID="+uid+",SID="+sid+",Rank="+rank+",Outcome="
	     +outcome+",Comment="+comment+",Data="+data.size());
	if (data.size() > 0) {
	    sb.append("[" + data.get(0));
	    for (int i = 1; i < data.size(); ++i) {
		sb.append("," + data.get(i));
	    }
	    sb.append("]");
	}
	sb.append("]");
	return sb.toString();
    }
}
