#!/bin/bash
# directory to write output XML (if this doesn't exist, the results will not be generated!)
OUTPUT_DIR="../target/test-reports"
mkdir $OUTPUT_DIR


FIREFOX=`which firefox`
if [ "$?" -eq 1 ];
then
    echo "Firefox not found."
    exit 1
fi

#if you executed this script using cygwin, your firefox path would look like
#/cygdrive/c/Program Files (x86)/Mozilla Firefox/firefox
#which would not work in this script
#so we remove the '/cygdrive/c' and repalce it with 'C:'


NEW_FIRE_FOX_PATH=${FIREFOX/\/cygdrive\/c/C:};
echo $FIREFOX
echo $NEW_FIRE_FOX_PATH
# run the tests
java -jar JsTestDriver-1.3.4.b.jar --port 9876 --config jsTestDriver.conf --browser "$NEW_FIRE_FOX_PATH" --raiseOnFailure --tests all --testOutput "$OUTPUT_DIR"
#RESULT=$?


echo "Done."

#exit $RESULT