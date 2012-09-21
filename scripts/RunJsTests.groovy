#!/usr/bin/env groovy

def ant = new AntBuilder()
ant.mkdir(dir: 'target/test-reports')

//Update the codebase from GitHub
ant.exec(executable:'./xvfbdscript.sh', dir: "test/jasmine") {
    arg(value:'start')
}
ant.exec(executable: './jsTestDriver.sh', dir: 'test/jasmine', failonerror: true)

ant.exec(executable:'./xvfbdscript.sh', dir: "test/jasmine") {
    arg(value:'stop')
}