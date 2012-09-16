# run the tests
java -jar JsTestDriver-1.3.4.b.jar --port 9876 --config jsTestDriver.conf --browser "C:/Program Files (x86)/Mozilla Firefox/firefox" --tests all --testOutput "../target"
RESULT=$?
 
exit $RESULT