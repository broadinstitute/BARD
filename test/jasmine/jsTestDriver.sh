#!/bin/bash
# directory to write output XML (if this doesn't exist, the results will not be generated!)
OUTPUT_DIR="target/test-reports"
mkdir $OUTPUT_DIR

XVFB=`which Xvfb`
if [ "$?" -eq 1 ];
then
    echo "Xvfb not found."
    exit 1
fi

FIREFOX=`which firefox`
if [ "$?" -eq 1 ];
then
    echo "Firefox not found."
    exit 1
fi

#if you executed this script using cygwin, your firefox path would look like
#/cygdrive/c/Program Files (x86)/Mozilla Firefox/firefox
#which would not work in this script
#so we remove the '/cygdrive/c' and replace it with 'C:'

 echo `pwd`
NEW_FIRE_FOX_PATH=${FIREFOX/\/cygdrive\/c/C:};
#echo $FIREFOX
#echo $NEW_FIRE_FOX_PATH

$XVFB :99 -ac &    # launch virtual framebuffer into the background
PID_XVFB="$!"      # take the process ID
export DISPLAY=:99 # set display to use that of the xvfb

# run the tests
java -jar test/jasmine/lib/JsTestDriver-1.3.4.b.jar --config test/jasmine/jsTestDriver.conf --port 4224 --browser "$NEW_FIRE_FOX_PATH" --tests all --testOutput $OUTPUT_DIR

kill $PID_XVFB     # shut down xvfb (firefox will shut down cleanly by JsTestDriver)
echo "Done."
