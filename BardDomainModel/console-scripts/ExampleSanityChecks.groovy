import bard.db.dictionary.*
import bard.db.registration.*
import bard.db.experiment.*
/*
for (cls in grailsApplication.DomainClasses) {
   println( "import ${cls.fullName}" )
}
*/

for (cls in grailsApplication.DomainClasses) {
   //def domainClass = grailsApplication.getDomainClass(cls.fullName)
   def domainClass = cls.shortName
   println("domainClass: ${domainClass}")
   def dcls = cls.newInstance()
   assert dcls
   println("dcls: ${dcls.count()}")
}