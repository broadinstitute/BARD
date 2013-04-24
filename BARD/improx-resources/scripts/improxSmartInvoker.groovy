/*
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

//
// Usage:
//     groovy improxSmartInvoker.groovy <TARGET_FILE_PATH>
//     groovyclient improxSmartInvoker.groovy <TARGET_FILE_PATH>
//
// This script try to invoke TARGET_FILE_PATH by the following invokers.
//
// [grails-improx plugin]
//    If the target is a .groovy file under either a test/unit or a test/integration directory
//    of a Grails project, it's executed by grails's test-app with appropriate test type via
//    interactive mode proxy.
//
// [Grails]
//    If the target is a .groovy file under a test/functional directory of a Grails project,
//    it's executed by grails's test-app with appropriate test type on a new Grails process.
//
// [GroovyServ]
//    If GroovyServ is installed, a target *.groovy is executed by groovyclient of GroovyServ.
//
// [Groovy]
//    A target *.groovy is executed by Groovy.
//

//---------------------------------------
// Config
//

// This script expects the commands will find in PATH environment variable.
// If you don't want to use PATH environment variable, change the following lines.
class Config {
    static final GRAILS_BIN = 'grails'
    static final GROOVYCLIENT_BIN = 'groovyclient'
    static final GROOVY_BIN = 'groovy'
}

//---------------------------------------
// Definition
//

class ChainOfInvokers {
    boolean invoke(File file) {
        [
            new GrailsInvoker(),
            new SimpleInvoker(exec: Config.GROOVYCLIENT_BIN),
            new SimpleInvoker(exec: Config.GROOVY_BIN),
            new NotFoundInvoker(),
        ].find { it.invoke(file) }
    }
}

class GrailsInvoker {
    boolean invoke(File file) {
        def params = parseParams(file)
        if (!params) return false

        def invokeMethod = resolveInvokeMethod(params.testType)

        invokeMethod.call(params.testType, params.className, params.projectDir)
        return true
    }

    private parseParams(File file) {
        def matcher = (file.canonicalFile.toURI().path =~ '^(.*)/test/(unit|integration|functional)/(.*)$')
        if (matcher.find()) {
            def projectDir = new File(matcher.group(1))
            def testType = matcher.group(2)
            def relativePathFromPackageRoot = matcher.group(3)
            def className = (relativePathFromPackageRoot - '.groovy').replaceAll('/', '.')
            return [projectDir:projectDir, testType:testType, className:className]
        }
        return null
    }

    private resolveInvokeMethod(testType) {
        switch (testType) {
            case 'unit':
            case 'integration':
                return this.&invokeInteractiveModeProxy
            case 'functional':
                return this.&invokeGrails
            default:
                assert false : 'already passed parseParams()'
        }
    }

    private invokeInteractiveModeProxy(testType, className, projectDir) {
        new InteractiveModeProxyClient().invoke(['test-app', "-echoOut", "-echoErr", "${testType}:", className].join(' '))
    }

    private invokeGrails(testType, className, projectDir) {
        ProcessUtil.execute([Config.GRAILS_BIN, 'test-app', "-echoOut", "-echoErr", "${testType}:" , className], projectDir)
    }
}

class SimpleInvoker {
    String exec

    boolean invoke(File file) {
        ProcessUtil.execute([exec, file.absolutePath])
    }
}

class NotFoundInvoker {
    boolean invoke(File file) {
        System.err.println "ERROR: Cannot invoke: ${file}"
        System.exit 1
    }
}

class ProcessUtil {
    static execute(List command, File currentDir = null) {
        if (currentDir) {
            println "Executing '${command.join(' ')}' at ${currentDir} ..."
            doExecute { command.execute([], currentDir) }
        } else {
            println "Executing '${command.join(' ')}' ..."
            doExecute { command.execute() }
        }
    }

    private static doExecute(closure) {
        try {
            def proc = closure.call()

            // Delegate stdout/stderr to System.out/err
            // it's very important to disable a close method, because waitForProcessOutput
            // will automatically close the streams.
            def out = new FilterOutputStream(System.out) { void close() { /* do nothing */ } }
            def err = new FilterOutputStream(System.err) { void close() { /* do nothing */ } }
            proc.waitForProcessOutput(out, err)

            return true

        } catch (IOException e) {
            if (e.message =~ /^Cannot run program/) return false
            throw e
        }
    }
}

class InteractiveModeProxyClient {

    private static final int DEFAULT_PORT = 8096
    private Socket socket

    def invoke(String command) {
        println "Invoking '${command}' via interactive mode proxy..."
        validate(command)
        connect(port)
        send(command)
        waitForResult()
    }

    private validate(String command) {
        if (command.trim().empty) {
            System.err.println "ERROR: Command not found."
            System.exit 1
        }
    }

    private getPort() {
        (System.getProperty("improx.port") ?: DEFAULT_PORT) as int
    }

    private connect(int port) {
        try {
            socket = new Socket("localhost", port)
        } catch (ConnectException e) {
            System.err.println "ERROR: Failed to connect to server via port $port."
            System.err.println """\
                |  Before connecting, install 'improx' plugin into your application, and
                |  run the 'improx-start' command on interactive mode of the application.
                |""".stripMargin()
            System.exit 1
        }
    }

    private send(String command) {
        socket << command << "\n"
    }

    private waitForResult() {
        socket.withStreams { ins, out ->
            while (true) {
                def (text, eof) = readLine(ins)
                println text
                if (eof) {
                    System.exit 0
                }
            }
        }
    }

    private readLine(InputStream ins) {
        def out = new ByteArrayOutputStream()
        int ch
        while ((ch = ins.read()) != -1) {
            if (ch == '\n') { // LF (fixed)
                return [out.toString(), false] // 2nd arg is EOF flag
            }
            out.write((byte) ch)
        }
        return [out.toString(), true] // 2nd arg is EOF flag
    }
}

def groovyFileOf = { String path ->
    def file = new File(path)
    if (!file.exists()) {
        System.err.println "ERROR: File not found: ${path}"
        System.exit 1
    }
    if (!(file.name =~ /.*\.groovy$/)) {
        System.err.println "ERROR: Not groovy file: ${path}"
        System.exit 1
    }
    return file
}

//---------------------------------------
// Main
//

def path = args ? args[0] : '<NO_ARGUMENT>'
new ChainOfInvokers().invoke(groovyFileOf(path))

