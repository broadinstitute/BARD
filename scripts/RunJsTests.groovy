#!/usr/bin/env groovy

def ant = new AntBuilder()
ant.mkdir(dir: 'target/test-reports')

//Update the codebase from GitHub
//ant.exec(executable:"test/jasmine/xvfbdscript.sh") {
//    arg(value:'start')
//}
ant.exec(executable: '/bin/sh', dir: 'test/jasmine', failonerror: true){
    arg(line:'-c jsTestDriver.sh')
}

//ant.exec(executable:'xvfbdscript.sh', dir: "test/jasmine") {
//    arg(value:'start')
//}