package bard.validation.ext;

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 12/18/13
 * Time: 3:58 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ExternalItem {
    String getDisplay();

    String getId();

    void setDisplay(String display);

    void setId(String id);
}
