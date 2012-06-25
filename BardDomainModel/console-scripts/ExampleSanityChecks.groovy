
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
