package bard.db.dictionary

class BiologyDescriptorIntegrationTests extends GroovyTestCase {

    void testList() {
        def list = BiologyDescriptor.findAll()
        assert list.size() > 0
    }
}
