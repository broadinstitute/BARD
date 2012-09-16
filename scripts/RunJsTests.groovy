target('default': 'Runs JsTestDriver tests') {
    ant.mkdir(dir: 'target/js-test-reports')
    ant.exec(executable: 'jsTestDriver.sh', dir: 'jsTests', failonerror: true)
}