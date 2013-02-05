package bard.db.dictionary

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 9/26/12
 * Time: 2:09 PM
 * To change this template use File | Settings | File Templates.
 */
class OntologyBreadcrumb {

//    List<BiologyDescriptor> biologyDescriptors
    def biologyDescriptors
    def instanceDescriptors
    def assayDescriptors

    public OntologyBreadcrumb(Element element) {
        biologyDescriptors = element.biologyDescriptors.sort(new DescriptorLabelComparator())
        instanceDescriptors = element.instanceDescriptors.sort(new DescriptorLabelComparator())
        assayDescriptors = element.assayDescriptors.sort(new DescriptorLabelComparator())
    }


    Descriptor getPreferedDescriptor() {
        Descriptor preferredDescriptor
        if (biologyDescriptors) {
            preferredDescriptor = biologyDescriptors.first()
        }
        else if (instanceDescriptors) {
            preferredDescriptor = instanceDescriptors.first()
        }
        else if (assayDescriptors) {
            preferredDescriptor = assayDescriptors.first()
        }
    }

}