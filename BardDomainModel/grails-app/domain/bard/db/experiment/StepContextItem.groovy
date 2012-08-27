package bard.db.experiment

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/23/12
 * Time: 12:03 AM
 * To change this template use File | Settings | File Templates.
 */
class StepContextItem extends AbstractProjectContextItem {

    ProjectStep projectStep

    static belongsTo = [projectStep: ProjectStep]

    static mapping = {
        discriminator(value: "Step")
    }
}
