package bard.db.experiment

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/23/12
 * Time: 12:02 AM
 * To change this template use File | Settings | File Templates.
 */
class ProjectContextItem extends AbstractProjectContextItem {
    Project project

    static belongsTo = [project: Project]

    static mapping = {
        discriminator(value: "Project")
    }
}
