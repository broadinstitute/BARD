package bard.db.experiment


import org.junit.*
import grails.test.mixin.*

@TestFor(PanelExperimentController)
@Mock(PanelExperiment)
class PanelExperimentControllerUnitSpec {

    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/panelExperiment/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.panelExperimentInstanceList.size() == 0
        assert model.panelExperimentInstanceTotal == 0
    }

    void testCreate() {
        def model = controller.create()

        assert model.panelExperimentInstance != null
    }

    void testSave() {
        controller.save()

        assert model.panelExperimentInstance != null
        assert view == '/panelExperiment/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/panelExperiment/show/1'
        assert controller.flash.message != null
        assert PanelExperiment.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/panelExperiment/list'

        populateValidParams(params)
        def panelExperiment = new PanelExperiment(params)

        assert panelExperiment.save() != null

        params.id = panelExperiment.id

        def model = controller.show()

        assert model.panelExperimentInstance == panelExperiment
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/panelExperiment/list'

        populateValidParams(params)
        def panelExperiment = new PanelExperiment(params)

        assert panelExperiment.save() != null

        params.id = panelExperiment.id

        def model = controller.edit()

        assert model.panelExperimentInstance == panelExperiment
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/panelExperiment/list'

        response.reset()

        populateValidParams(params)
        def panelExperiment = new PanelExperiment(params)

        assert panelExperiment.save() != null

        // test invalid parameters in update
        params.id = panelExperiment.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/panelExperiment/edit"
        assert model.panelExperimentInstance != null

        panelExperiment.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/panelExperiment/show/$panelExperiment.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        panelExperiment.clearErrors()

        populateValidParams(params)
        params.id = panelExperiment.id
        params.version = -1
        controller.update()

        assert view == "/panelExperiment/edit"
        assert model.panelExperimentInstance != null
        assert model.panelExperimentInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/panelExperiment/list'

        response.reset()

        populateValidParams(params)
        def panelExperiment = new PanelExperiment(params)

        assert panelExperiment.save() != null
        assert PanelExperiment.count() == 1

        params.id = panelExperiment.id

        controller.delete()

        assert PanelExperiment.count() == 0
        assert PanelExperiment.get(panelExperiment.id) == null
        assert response.redirectedUrl == '/panelExperiment/list'
    }
}
