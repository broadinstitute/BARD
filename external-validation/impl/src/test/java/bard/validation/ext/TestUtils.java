package bard.validation.ext;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 12/11/13
 * Time: 5:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestUtils {
    public static void printItemsInfo(List<ExternalItem> items) {
        for (ExternalItem item : items)
            System.out.println(String.format("%s\t%s", item.getId(), item.getDisplay()));
    }
}
