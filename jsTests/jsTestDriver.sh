#!/bin/bash
# directory to write output XML (if this doesn't exist, the results will not be generated!)
OUTPUT_DIR="test-reports"
mkdir $OUTPUT_DIR


FIREFOX=`which firefox`
if [ "$?" -eq 1 ];
then
    echo "Firefox not found."
    exit 1
fi

echo $FIREFOX

# run the tests
java -jar JsTestDriver-1.3.4.b.jar --port 9876 --config jsTestDriver.conf --browser "$FIREFOX" --raiseOnFailure --tests all --testOutput "$OUTPUT_DIR"
RESULT=$?


echo "Done."

exit $RESULT
