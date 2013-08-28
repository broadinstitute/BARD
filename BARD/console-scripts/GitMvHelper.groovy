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
def dirs = ['src', 'test']
for(String relativeDir in dirs){
    final String srcDir = "$srcRoot/$relativeDir"
    executeCommand("cp -r ${srcDir} ${dstRoot}")
    new File(srcDir).traverse([type: FileType.FILES, ]) { File file ->
        final String srcPath = file.getPath()
        executeCommand("git mv -f ${srcPath} ${srcPath.replace(srcDir, "$dstRoot/$relativeDir")}")
    }
}

private executeCommand(String cmd){
    println(cmd)
    def proc = cmd.execute()
    proc.waitFor()
    final exitCode = proc.exitValue()
    if(exitCode!=0) {
        println "return code: ${ exitCode}"
        println "stderr: ${proc.err.text}"
        println "stdout: ${proc.in.text}"
    }
}
