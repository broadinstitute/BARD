package bard.validation.extext

import bard.validation.ext.ExternalItem

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 12/20/13
 * Time: 2:29 PM
 * To change this template use File | Settings | File Templates.
 */
class ExternalItemImpl implements ExternalItem{
    String id
    String display
    /**
     * Default no-args constructor
     */
    public ExternalItemImpl(){}
    /**
     * Constructor that sets ID and display
     * @param id
     * @param display
     */
    public ExternalItemImpl(String id, String display){
        this.id = id
        this.display = display
    }
}
