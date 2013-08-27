REM directory to write output XML (if this doesn't exist, the results will not be generated!)
OUTPUT_DIR="target/test-reports"
mkdir $OUTPUT_DIR

FIREFOX="C:\Program Files\Mozilla Firefox\firefox.exe"



# run the tests
java -jar test/jasmine/lib/JsTestDriver-1.3.4.b.jar --config test/jasmine/jsTestDriver.conf --port 4224 --browser "$FIREFOX" --tests all --testOutput $OUTPUT_DIR

echo "Done."
