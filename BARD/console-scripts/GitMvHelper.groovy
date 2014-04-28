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

import groovy.io.FileType

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/15/13
 * Time: 4:02 PM
 * To change this template use File | Settings | File Templates.
 */
String srcRoot = 'bard-web-client'
String dstRoot = 'BARD'
def dirs = ['grails-app'] // ['web-app'] // ['src', 'test', 'grails-app']
for(String relativeDir in dirs){
    final String srcDir = "$srcRoot/$relativeDir"
    executeCommand("cp -r ${srcDir} ${dstRoot}")
    new File(srcDir).traverse([type: FileType.FILES, ]) { File file ->
        final String srcPath = file.getPath()
        String relativeDst = "$dstRoot/$relativeDir"
        executeCommand(/git mv -f "${srcPath}" "${srcPath.replace(srcDir, relativeDst)}"/)
    }
}

private executeCommand(String cmd){
    println(cmd)
//    def proc = cmd.execute()
//    proc.waitFor()
//    final exitCode = proc.exitValue()
//    if(exitCode!=0) {
//        println "return code: ${ exitCode}"
//        println "stderr: ${proc.err.text}"
//        println "stdout: ${proc.in.text}"
//    }
}
