package barddataqa

class QaStatusController {

    def index() {
        return [qaStatusList: QaStatus.findAll()]
    }
}
