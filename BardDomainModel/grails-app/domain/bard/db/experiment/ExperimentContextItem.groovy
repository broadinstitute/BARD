package bard.db.experiment

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/21/12
 * Time: 3:59 PM
 * To change this template use File | Settings | File Templates.
 */
class ExperimentContextItem extends RunContextItem{

    Experiment experiment

    static belongsTo = [experiment:Experiment]

    static mapping = {
        discriminator( value:"Experiment")
    }
}
