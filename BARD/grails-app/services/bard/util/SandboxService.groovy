/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package bard.util

import org.apache.commons.lang.exception.ExceptionUtils
import org.apache.commons.lang3.StringUtils

import java.util.regex.Matcher
import java.util.regex.Pattern

//@Secured(["hasRole('ROLE_BARD_ADMINISTRATOR')"])
class SandboxService {

    def grailsApplication

    static class ScriptInfo {
        String description;
        List<String> parameterNames;
    }

    String getPrefix() {
        return grailsApplication.config.bard.services.sandboxService.path
    }

    ScriptInfo getScriptInfo(String name) {
        StringBuilder paramString = new StringBuilder()
        StringBuilder descriptionString = new StringBuilder()

        Pattern descriptionPattern = Pattern.compile("\\s*//\\s*@description \\s*(.*)");
        Pattern paramPattern = Pattern.compile("\\s*//\\s*@param \\s*(.*)");

        assert ! (name.contains("/") || name.contains("\\") )

        new File(getPrefix(), name).eachLine { line ->
            Matcher m = descriptionPattern.matcher(line)
            if(m.matches()) {
                descriptionString.append(m.group(1))
            }

            m = paramPattern.matcher(line)
            if(m.matches()) {
                paramString.append(m.group(1))
            }
        }

        List parameterNames = paramString.toString().split("\\s+") as List

        return new ScriptInfo(description: descriptionString.toString(), parameterNames: parameterNames.findAll { !StringUtils.isBlank(it) })
    }

    List<String> listScriptNames() {
        return new File(getPrefix()).listFiles().findAll() { it.getName().endsWith(".groovy") }.collect { it.name }
    }

    String run(String scriptName, Map<String, String> parameters, OutputStream outputStream) {
        long startTime = System.currentTimeMillis()
        OutputStreamWriter writer = new OutputStreamWriter(outputStream)

        Binding binding = new Binding();
        parameters.each {k, v -> binding.setVariable(k, v) }
        binding.setVariable("ctx", grailsApplication.mainContext)
        binding.setVariable("grailsApplication", grailsApplication)

        PrintStream ps = new PrintStream(outputStream, true)
        binding.setVariable("out", ps)

        GroovyShell shell = new GroovyShell(this.class.getClassLoader(), binding)
        File file = new File(getPrefix(), scriptName);
        try {
            shell.evaluate(file);
        } catch(Throwable t) {
            writer.write(ExceptionUtils.getFullStackTrace(t));
        }

        long endTime = System.currentTimeMillis()
        ps.println("Finished running ${scriptName} (${endTime-startTime} ms)")
        ps.flush()
    }
}
