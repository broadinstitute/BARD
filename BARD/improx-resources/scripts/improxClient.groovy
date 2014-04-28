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
//     groovy improxClient.groovy <COMMAND_LINE>
//     groovyclient improxClient.groovy <COMMAND_LINE>
//
// Examples:
//     groovy improxClient.groovy help
//     groovy improxClient.groovy test-app unit: sample.SampleUnitTests
//

//---------------------------------------
// Definition
//

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

//---------------------------------------
// Main
//

String command = args.join(' ')
new InteractiveModeProxyClient().invoke(command)

