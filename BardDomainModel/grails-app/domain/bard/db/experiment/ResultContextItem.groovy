package bard.db.experiment

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/21/12
 * Time: 3:59 PM
 * To change this template use File | Settings | File Templates.
 */
class ResultContextItem extends RunContextItem{

    Result result

    static belongsTo = [result:Result]

    static mapping = {
        discriminator(value:"Result")
    }
}
