package curverendering;

/**
* Created with IntelliJ IDEA.
* User: jasiedu
* Date: 9/19/12
* Time: 12:29 PM
* To change this template use File | Settings | File Templates.
*/
public class Bounds {

    protected double xMin
    protected double xMax
    protected double yMin
    protected double yMax;

    public Bounds(double xMin, double xMax, double yMin, double yMax) {
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
    }

    public void expandToContain(double xMin, double xMax, double yMin, double yMax) {
        this.xMin = Math.min(xMin, this.xMin);
        this.yMin = Math.min(yMin, this.yMin);
        this.xMax = Math.max(xMax, this.xMax);
        this.yMax = Math.max(yMax, this.yMax);
    }
}
