package bard.db.guidance

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/9/13
 * Time: 11:49 AM
 * To change this template use File | Settings | File Templates.
 */

class DefaultGuidanceImpl implements Guidance{
    final String message


    DefaultGuidanceImpl(String message){
        this.message = message
    }

    @Override
    String getMessage() {
        return message  //To change body of implemented methods use File | Settings | File Templates.
    }
}
