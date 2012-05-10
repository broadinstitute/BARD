package bard.db.dictionary

/**
 */
class StageIntegrationTests extends GroovyTestCase {

    void testList() {
        def list = Stage.findAll()
        assert list.size() > 0
    }

}
