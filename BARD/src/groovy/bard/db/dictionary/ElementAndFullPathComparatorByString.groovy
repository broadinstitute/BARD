package bard.db.dictionary

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 4/23/13
 * Time: 11:56 PM
 * To change this template use File | Settings | File Templates.
 */
class ElementAndFullPathComparatorByString implements Comparator<ElementAndFullPath> {

    int compare(ElementAndFullPath o1, ElementAndFullPath o2) {
        if (o1 != null && o2 != null) {
            return o1.toString().compareTo(o2.toString())
        } else if (null == o1) {
            return 1
        } else if (null == o2) {
            return -1
        } else {
            return 0
        }
    }
}
