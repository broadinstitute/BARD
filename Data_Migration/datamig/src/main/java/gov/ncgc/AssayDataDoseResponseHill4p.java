package gov.ncgc;
// $Id: AssayDataDoseResponseHill4p.java 2278 2008-05-29 22:27:45Z nguyenda $

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

public class AssayDataDoseResponseHill4p extends AssayDataDoseResponse {
	private static final long serialVersionUID = 12242007L;
	private static final double ln10 = 2.30258509299404568401;

	private double hillCoef;
	private double zeroAct;
	private double infAct;
	private double ac50; // in Molar
	private double logac50;

	public AssayDataDoseResponseHill4p(int tid) {
		super(tid);
	}

	public AssayDataDoseResponseHill4p(int tid, Double[] response) {
		super(tid, response);
	}

	public void setHillCoef(double hillCoef) {
		this.hillCoef = hillCoef;
	}

	public double getHillCoef() {
		return hillCoef;
	}

	public void setZeroAct(double zeroAct) {
		this.zeroAct = zeroAct;
	}

	public double getZeroAct() {
		return zeroAct;
	}

	public void setInfAct(double infAct) {
		this.infAct = infAct;
	}

	public double getInfAct() {
		return infAct;
	}

	public void setAc50(double ac50) {
		this.ac50 = ac50;
		if (ac50 != 0.) {
			this.logac50 = Math.log10(ac50);
		}
	}

	public double getAc50() {
		return ac50;
	}

	private double hillfn(double logx) { // x - log10 M
		if (ac50 != 0.) {
			return zeroAct + ((infAct - zeroAct) / (1. + Math.exp(ln10 * hillCoef * (logac50 - logx))));
		}
		return 0.;
	}

	public double[][] getFittedDoseResponseCurve() {
		double[][] curve = new double[0][];

		Double[] dose = getDose();
		if (dose == null || dose.length == 0) {
			// /...
		} else if (ac50 != 0.) {
			double[] x = new double[dose.length];
			// System.out.println();
			for (int i = 0; i < x.length; ++i) {
				x[i] = Math.log10(dose[i]);
				// System.out.println(dose[i] + " " + x[i] + " " + hillfn
				// (x[i]));
			}

			// upsample enough time to get a smooth curve...
			x = upsample(upsample(upsample(upsample(x))));

			curve = new double[2][x.length];
			for (int i = 0; i < x.length; ++i) {
				curve[0][i] = hillfn(x[i]);
				curve[1][i] = x[i];
			}
		}
		return curve;
	}

	private static double[] upsample(double[] x) {
		if (x.length == 0) {
			return new double[] {};
		}

		double[] x2 = new double[2 * x.length - 1];
		for (int i = 0; i < x2.length; i++) {
			if (i % 2 == 0)
				x2[i] = x[i / 2];
			else
				x2[i] = 0.5 * (x[(i - 1) / 2] + x[(i + 1) / 2]);
		}
		return x2;
	}

	/*
	 * override these methods in case we make changes to the class later on, so
	 * that it doesn't break old code
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeDouble(hillCoef);
		out.writeDouble(zeroAct);
		out.writeDouble(infAct);
		out.writeDouble(ac50);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		setHillCoef(in.readDouble());
		setZeroAct(in.readDouble());
		setInfAct(in.readDouble());
		setAc50(in.readDouble());
	}
}
