package curverendering;



/**
* Created with IntelliJ IDEA.
* User: jasiedu
* Date: 9/19/12
* Time: 12:29 PM
* To change this template use File | Settings | File Templates.
*/
public class CurveParameters {

    final Double slope;
    final Double HILL_SLOPE;
    final Double S0;
    final Double SINF;
    final Double lower95CL;
    final Double upper95CL;
    final Date resultTime;

    public CurveParameters(final Double slope,
                           final Date resultTime,
                           final Double hill_slope,
                           final Double s0,
                           final Double sinf,
                           final Double lower95CL,
                           final Double upper95CL) {
        this.slope = slope;
        this.resultTime = resultTime;
        HILL_SLOPE = hill_slope;
        S0 = s0;
        SINF = sinf;
        this.lower95CL = lower95CL;
        this.upper95CL = upper95CL;
    }


//    public CurveParameters( CurveParameters curveParameters ) {
//        this.slope = curveParameters.slope;
//        this.resultTime = curveParameters.resultTime;
//        this.HILL_SLOPE = curveParameters.HILL_SLOPE;
//        this.S0 = curveParameters.S0;
//        this.SINF = curveParameters.SINF;
//        this.lower95CL = lower95CL;
//        this.upper95CL = upper95CL;
//    }


}
