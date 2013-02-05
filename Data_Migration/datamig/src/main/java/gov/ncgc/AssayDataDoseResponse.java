package gov.ncgc;
// $Id: AssayDataDoseResponse.java 2278 2008-05-29 22:27:45Z nguyenda $

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

public class AssayDataDoseResponse extends AssayData {
    private static final long serialVersionUID = 12242007L;
    
    private Double[] dose = null;
    public AssayDataDoseResponse (int tid) {
	super (tid);
    }
    public AssayDataDoseResponse (int tid, Double[] response) {
	super (tid, response);
    }
    public Double[] getResponse () { return (Double[])getValue (); }
    public void setResponse (Double[] response) { setValue(response); }

    /* 
     * if dose is null or empty, then the dosing can be obtained via
     * the corresponding ResultType.getTestConcentration()
     */
    public Double[] getDose () { return dose; }
    public void setDose (Double[] dose) { this.dose = dose; }

    // override this method for different type of dose-response fitting
    //  algorithms.  the first index is the activity response (y) and
    //  second index (x) is log10 of the concentration 
    public double[][] getFittedDoseResponseCurve () { 
	return new double[0][];
    }

    public double[][] getLogDoseResponse () {
	double[][] yx = new double[0][];
	if (dose != null && dose.length > 0) {
	    Double[] response = getResponse ();
	    yx = new double[2][dose.length];

	    for (int i = 0; i < dose.length; ++i) {
		yx[0][i] = response[i];
		yx[1][i] = Math.log10(dose[i]);
	    }
	}
	return yx;
    }

    public String toString () {
	StringBuffer sb = new StringBuffer ("[TID="+getTID());
	if (dose != null && getValue () != null) {
	    if (dose.length > 0) {
		sb.append(",Dose=" + dose.length + "["+dose[0]);
		for (int i = 1; i < dose.length; ++i) {
		    sb.append(","+dose[i]);
		}
		sb.append("]");
	    }
	    Double[] response = getResponse ();
	    if (response.length > 0) {
		sb.append(",Response="+response.length+"["+response[0]);
		for (int i = 1; i < response.length; ++i) {
		    sb.append(","+response[i]);
		}
		sb.append("]");
	    }
	}
	sb.append("]");
	return sb.toString();
    }

    /*
     * override these methods in case we make changes to the
     * class later on, so that it doesn't break old code
     */
    private void writeObject (ObjectOutputStream out) throws IOException {
	out.writeObject(dose);
    }

    private void readObject (ObjectInputStream in) 
	throws IOException, ClassNotFoundException {
	dose = (Double[])in.readObject();
    }    
}
