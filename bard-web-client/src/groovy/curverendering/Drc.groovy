package curverendering;

import java.awt.Color

/**
* Created with IntelliJ IDEA.
* User: jasiedu
* Date: 9/19/12
* Time: 12:28 PM
* To change this template use File | Settings | File Templates.
*/
public class Drc {
    List<Double> concentrations=[];
    List<Double> activities = [];
    List<Boolean> isValid=[];
    CurveParameters curveParameters;
    Color color;

    public Drc(List<Double> concentrations, List<Double> activities, List<Boolean> isValid, CurveParameters curveParameters, Color color) {
        this.concentrations = concentrations;
        this.activities = activities;
        this.curveParameters = curveParameters;
        this.isValid = isValid;
        this.color = color;
    }

    CurveParameters getCurveParameters(){
        if ((curveParameters?.S0==0D) &&
            (curveParameters?.SINF==0D)  &&
            (curveParameters?.HILL_SLOPE==0D))
                return new CurveParameters( curveParameters?.slope,
                                            curveParameters?.resultTime,
                                            null,
                                            null,
                                            null,
                                            curveParameters?.lower95CL,
                                            curveParameters?.upper95CL ) ;
        else
            return  curveParameters;
    }

}
