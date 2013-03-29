package bard.db.experiment.results

import bard.db.dictionary.Element

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 3/29/13
 * Time: 10:14 AM
 * To change this template use File | Settings | File Templates.
 */
class Cell {
    String qualifier;
    Float value;
    Float minValue;
    Float maxValue;
    Element element;

    String valueDisplay;
}
