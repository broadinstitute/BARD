package bard.db.registration

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestFor

@TestFor(AssayController)
@Build(Assay)
class AssayControllerTests {


    def populateValidParams(params) {
        assert params != null

        Assay assay = Assay.buildWithoutSave()
        params['assayTitle'] = assay.assayTitle
        params['assayName'] = assay.assayName
        params['assayVersion'] = '1.1'
        params['dateCreated'] = assay.dateCreated
    }

    void testIndex() {
        controller.index()
        assert "/assay/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.assayInstanceList.size() == 0
        assert model.assayInstanceTotal == 0
    }

    void testCreate() {
        def model = controller.create()

        assert model.assayInstance != null
    }

    void testSave() {
        controller.save()

        assert model.assayInstance != null
        assert view == '/assay/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/assay/show/1'
        assert controller.flash.message != null
        assert Assay.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/assay/list'


        populateValidParams(params)
        def assay = new Assay(params)

        assert assay.save() != null

        params.id = assay.id

        def model = controller.show()

        assert model.assayInstance == assay
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/assay/list'


        populateValidParams(params)
        def assay = new Assay(params)

        assert assay.save() != null

        params.id = assay.id

        def model = controller.edit()

        assert model.assayInstance == assay
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/assay/list'

        response.reset()


        populateValidParams(params)
        def assay = new Assay(params)

        assert assay.save() != null

        // test invalid parameters in update
        params.id = assay.id
        params.'assayTitle' = null
        controller.update()

        assert view == "/assay/edit"
        assert model.assayInstance != null

        assay.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/assay/show/$assay.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        assay.clearErrors()

        populateValidParams(params)
        params.id = assay.id
        params.version = -1
        controller.update()

        assert view == "/assay/edit"
        assert model.assayInstance != null
        assert model.assayInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/assay/list'

        response.reset()

        populateValidParams(params)
        def assay = new Assay(params)

        assert assay.save() != null
        assert Assay.count() == 1

        params.id = assay.id

        controller.delete()

        assert Assay.count() == 0
        assert Assay.get(assay.id) == null
        assert response.redirectedUrl == '/assay/list'
    }
}
