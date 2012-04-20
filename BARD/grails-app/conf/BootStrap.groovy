import bard.db.registration.Assay
import bard.db.registration.AssayStatus
import bard.db.registration.Measure
import bard.db.util.Unit
import bard.db.dictionary.ResultType
import bard.db.registration.MeasureContext

class BootStrap {

    def init = { servletContext ->
        environments {
            development {

                new AssayStatus(status:"Pending").save();
                new AssayStatus(status:"Active").save();
                new AssayStatus(status:"Superceded").save();
                new AssayStatus(status:"Retired").save();

                def pi = new ResultType(resultTypeName: "Percent Inhibition").save()
                new Unit(unit:"uM", description: "micromolar").save()
                new Unit(unit:"mL", description: "milliLiters").save()

            }
        }
    }
    def destroy = {
    }
}
