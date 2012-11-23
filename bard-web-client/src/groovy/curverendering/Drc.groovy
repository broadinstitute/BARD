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

}
