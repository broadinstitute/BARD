package bard.admin

import bard.util.SandboxService
import grails.plugins.springsecurity.Secured

@Secured(["hasRole('ROLE_BARD_ADMINISTRATOR')"])
class SandboxController {
    def sandboxService

    def index() {
        redirect action: "list"
    }

    def list() {
        [scriptNames: sandboxService.listScriptNames()]
    }

    def show(String scriptName) {
        [scriptName: scriptName, script: sandboxService.getScriptInfo(scriptName)]
    }

    def run(ParameterCommand scriptParameters) {
        response.setContentType("text/plain")
        sandboxService.run(scriptParameters.scriptName, scriptParameters.getParamsAsMap(), response.getOutputStream())
        return null
    }
}

class ParameterCommand {
    String scriptName;
    List<String> keys;
    List<String> values;

    Map<String, String> getParamsAsMap() {
        Map<String,String> p = [:]
        if(keys != null) {
            for(int i=0;i<keys.size();i++) {
                p[keys[i]] = values[i]
            }
        }
        return p
    }
}

