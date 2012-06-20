import bard.db.dictionary.AssayDescriptor
import bard.db.dictionary.BiologyDescriptor
import bard.db.dictionary.Element
import bard.db.dictionary.ElementHierarchy
import bard.db.dictionary.InstanceDescriptor
import bard.db.dictionary.Laboratory
import bard.db.dictionary.Ontology
import bard.db.dictionary.OntologyItem
import bard.db.dictionary.ResultType
import bard.db.dictionary.Stage
import bard.db.dictionary.TreeRoot
import bard.db.dictionary.Unit
import bard.db.dictionary.UnitConversion
import bard.db.experiment.Experiment
import bard.db.experiment.Project
import bard.db.experiment.Result
import bard.db.experiment.ResultContextItem
import bard.db.experiment.ResultHierarchy
import bard.db.experiment.Substance
import bard.db.registration.Assay
import bard.db.registration.ExternalReference
import bard.db.registration.ExternalSystem
import bard.db.registration.Measure
import bard.db.registration.MeasureContext
import bard.db.registration.MeasureContextItem
import bard.db.registration.Protocol
import bard.db.util.Qualifier
/*
for (cls in grailsApplication.DomainClasses) {
   println( "import ${cls.fullName}" )
}
*/

for (cls in grailsApplication.DomainClasses) {
   //def domainClass = grailsApplication.getDomainClass(cls.fullName)
   def domainClass =null
   println( "${cls.fullName} ${cls.class} count: ${domainClass?.count()}" )
}
