package gov.ncgc;
// $Id: AssayData.java 2278 2008-05-29 22:27:45Z nguyenda $

import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

public class AssayData implements Serializable {
	private static final long serialVersionUID = 12242007;

	/*
	 * Assay Result Field Type ID (TID) Note: Result Field ID's must be greater
	 * than "0"
	 */
	protected int tid = 0;
	protected Object value;

	public AssayData() {
	}

	public AssayData(int tid) {
		this.tid = tid;
	}

	public AssayData(int tid, Object value) {
		this.tid = tid;
		this.value = value;
	}

	public int getTID() {
		return tid;
	}

	public void setTID(int tid) {
		this.tid = tid;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String toString() {
		return "[TID=" + tid + ",VALUE=" + value + "]";
	}

	public boolean equals(Object obj) {
		if (obj instanceof AssayData) {
			AssayData ad = (AssayData) obj;
			if (tid == ad.tid && value.equals(ad.value)) {
				return true;
			}
		}
		return false;
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(tid);
		out.writeObject(value);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		tid = in.readInt();
		value = in.readObject();
	}
}
