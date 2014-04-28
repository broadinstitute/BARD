/*
* Copyright 2009 the original author or authors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package grails.plugin.functional.spock

class SpecTestTypeLoader {
    def binding
    def specType

    SpecTestTypeLoader(binding, specType) {
        this.binding = binding
        this.specType = specType
    }

    void registerFunctionalSpecSupport() {
        if (functionalTestsSupported && shouldRegisterSpecSupport) addFunctionalSpecSupport()
    }

    private boolean isFunctionalTestsSupported() {
        variables.containsKey('functionalTests')
    }

    private getVariables() {
        binding.variables
    }

    boolean isShouldRegisterSpecSupport() {
        return !variables.functionalTests.any {it in specType}
    }

    private addFunctionalSpecSupport() {
        variables.functionalTests << specType.newInstance('spock', 'functional')
    }
}
