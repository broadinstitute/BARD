package bard.util

import clover.org.apache.commons.lang.exception.ExceptionUtils
import grails.plugins.springsecurity.Secured
import org.apache.commons.lang3.StringUtils
import org.codehaus.groovy.control.CompilerConfiguration

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
