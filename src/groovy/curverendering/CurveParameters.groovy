package curverendering;



/**
* Created with IntelliJ IDEA.
* User: jasiedu
* Date: 9/19/12
* Time: 12:29 PM
* To change this template use File | Settings | File Templates.
*/
public class CurveParameters {

    public final Double AC50;
    public final Double HILL_SLOPE;
    public final Double S0;
    public final Double SINF;
    public final Double lower95CL;
    public final Double upper95CL;
    public final Date resultTime;

    public CurveParameters(Double ac50, Date resultTime, Double hill_slope, Double s0, Double sinf, Double lower95CL, Double upper95CL) {
        AC50 = ac50;
        this.resultTime = resultTime;
        HILL_SLOPE = hill_slope;
        S0 = s0;
        SINF = sinf;
        this.lower95CL = lower95CL;
        this.upper95CL = upper95CL;
    }
}
