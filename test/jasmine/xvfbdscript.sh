#!/bin/bash
# Start this before you run the tests
# Author: Brian Connolly (LabKey.org)
#
# chkconfig: 345 98 90
# description: Starts Virtual Framebuffer process to enable the
# LabKey server to use R.
#
#
 
XVFB_OUTPUT=/tmp/Xvfb.out
XVFB=/usr/bin/Xvfb
XVFB_OPTIONS=":99 -ac"
 
start() {
        echo -n "Starting : X Virtual Frame Buffer "
        $XVFB $XVFB_OPTIONS >>$XVFB_OUTPUT 2>&1&
        RETVAL=$?
        echo
        return $RETVAL
}
 
stop() {
        echo -n "Shutting down : X Virtual Frame Buffer"
        echo
        killall Xvfb
        return 0
}
 
case "$1" in
        start)
                start
                ;;
        stop)
                stop
                ;;
        *)
                echo "Usage: xvfbd {start|stop}"
                exit 1
                ;;
 
esac
exit $?