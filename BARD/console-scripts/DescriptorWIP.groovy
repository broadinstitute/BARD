import bard.db.registration.Assay
import bard.db.registration.AssayContext
import bard.db.registration.AssayContextItem
import bard.db.dictionary.DescriptorLabelComparator

Assay assay = Assay.findById(726)

//def assayContexts = assay.assayContexts.sort {a, b -> a.id <=> b.id}
def assayContexts = assay.assayContexts.sort {a, b ->
    new DescriptorLabelComparator().compare(a.preferredDescriptor, b.preferredDescriptor)
}

for (AssayContext ac in assayContexts) {
    println("start ${ac.contextName}******************************************")
    def preferredDescriptor = ac.preferredDescriptor
    println( "ac.preferredDescriptor.label : ${ac.preferredDescriptor.label}" )
    println( "preferredDescriptor path : ${preferredDescriptor.generateOntologyBreadCrumb()}")

    printBreadCrumbs(ac.assayContextItems, "  ")
    println("end   ${ac.contextName}******************************************")
    println()
}

void printBreadCrumbs(List<AssayContextItem> assayContextItems, indent) {

    for (AssayContextItem aci in assayContextItems) {
        println("${indent}[${aci.attributeElement.label} : ${aci.valueDisplay}]")
//        println(aci.attributeElement.ontologyBreadcrumb.preferedDescriptor.generateOntologyBreadCrumb())

        //println(aci.attributeElement.ontologyBreadcrumb.toOntologyBreadcrumbString())
//        printDescriptorBreadCrumbs(aci.attributeElement.ontologyBreadcrumb.assayDescriptors, 'assay attr'.padRight(15))
        printDescriptorBreadCrumbs(aci.attributeElement.assayDescriptors, 'assay attr'.padRight(15))
//        printDescriptorBreadCrumbs(aci.valueElement?.assayDescriptors, 'assay el'.padRight(15))
        printDescriptorBreadCrumbs(aci.attributeElement.biologyDescriptors, 'bio attr'.padRight(15))
//        printDescriptorBreadCrumbs(aci.valueElement?.biologyDescriptors, 'bio el'.padRight(15))
        printDescriptorBreadCrumbs(aci.attributeElement.instanceDescriptors, 'inst attr'.padRight(15))
//        printDescriptorBreadCrumbs(aci.valueElement?.instanceDescriptors, 'inst el'.padRight(15))

    }
}

void printDescriptorBreadCrumbs(Collection descriptors, String indent) {
    for (def ad in descriptors) {
        println(indent + ad.generateOntologyBreadCrumb())
    }
}
/*
def ads = Element.findById(882).assayDescriptors
  for( ad in ads ){
    println(ad.generateOntologyBreadCrumb())
  }
*/
