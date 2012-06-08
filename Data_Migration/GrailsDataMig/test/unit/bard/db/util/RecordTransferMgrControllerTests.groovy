package bard.db.util

import grails.test.mixin.*

@TestFor(RecordTransferMgrController)
@Mock(RecordTransferMgr)
class RecordTransferMgrControllerTests {


    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/recordTransferMgr/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.recordTransferMgrInstanceList.size() == 0
        assert model.recordTransferMgrInstanceTotal == 0
    }

    void testCreate() {
        def model = controller.create()

        assert model.recordTransferMgrInstance != null
    }

    void testSave() {
        controller.save()

        assert model.recordTransferMgrInstance != null
        assert view == '/recordTransferMgr/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/recordTransferMgr/show/1'
        assert controller.flash.message != null
        assert RecordTransferMgr.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/recordTransferMgr/list'


        populateValidParams(params)
        def recordTransferMgr = new RecordTransferMgr(params)

        assert recordTransferMgr.save() != null

        params.id = recordTransferMgr.id

        def model = controller.show()

        assert model.recordTransferMgrInstance == recordTransferMgr
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/recordTransferMgr/list'


        populateValidParams(params)
        def recordTransferMgr = new RecordTransferMgr(params)

        assert recordTransferMgr.save() != null

        params.id = recordTransferMgr.id

        def model = controller.edit()

        assert model.recordTransferMgrInstance == recordTransferMgr
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/recordTransferMgr/list'

        response.reset()


        populateValidParams(params)
        def recordTransferMgr = new RecordTransferMgr(params)

        assert recordTransferMgr.save() != null

        // test invalid parameters in update
        params.id = recordTransferMgr.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/recordTransferMgr/edit"
        assert model.recordTransferMgrInstance != null

        recordTransferMgr.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/recordTransferMgr/show/$recordTransferMgr.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        recordTransferMgr.clearErrors()

        populateValidParams(params)
        params.id = recordTransferMgr.id
        params.version = -1
        controller.update()

        assert view == "/recordTransferMgr/edit"
        assert model.recordTransferMgrInstance != null
        assert model.recordTransferMgrInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/recordTransferMgr/list'

        response.reset()

        populateValidParams(params)
        def recordTransferMgr = new RecordTransferMgr(params)

        assert recordTransferMgr.save() != null
        assert RecordTransferMgr.count() == 1

        params.id = recordTransferMgr.id

        controller.delete()

        assert RecordTransferMgr.count() == 0
        assert RecordTransferMgr.get(recordTransferMgr.id) == null
        assert response.redirectedUrl == '/recordTransferMgr/list'
    }
}
