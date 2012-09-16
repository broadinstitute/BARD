#!/bin/bash
FIREFOX=`which firefox`
if [ "$?" -eq 1 ];
then
  echo "Firefox not found."
  exit 1
fi
echo $FIREFOX 

# run the tests
java -jar JsTestDriver-1.3.4.b.jar --port 9876 --config jsTestDriver.conf --browser "$FIREFOX" --tests all --testOutput "../target"
RESULT=$?
 
exit $RESULT