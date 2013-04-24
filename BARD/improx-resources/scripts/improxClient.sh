#!/bin/bash

#
# Usage: improxClient.sh <COMMAND_LINE>
#
# Examples:
#     improxClient.sh help
#     improxClient.sh test-app unit: sample.SampleUnitTests
#     env IMPROX_PORT=8888 improxClient.sh help
#

#---------------------------------------
# Config
#

IMPROX_PORT=${IMPROX_PORT:=8096}

#---------------------------------------
# Definition
#

error() {
    local message="$*"
    echo "ERROR: $message" 1>&2
}

die() {
    local message="$*"
    error "$message"
    exit 1
}

call_improx() {
    local command="$*"
    echo "Executing '${command}' via interactive mode proxy..."
    (
        exec 5<> /dev/tcp/localhost/$IMPROX_PORT && echo "$command" >&5 && perl -pe 'local $|=1; $line' <&5
    ) 2>/dev/null || die "\
Failed to connect to server via port $IMPROX_PORT
  Before connecting, install 'improx' plugin into your application, and
  run the 'improx-start' command on interactive mode of the application."
}

usage() {
    echo "usage: `basename $0` <COMMAND>" >&2
    echo "examples:"
    echo "  $ `basename $0` help" >&2
    echo "  $ improxClient.sh test-app unit: sample.SampleUnitTests" >&2
    echo "  $ env IMPROX_PORT=8888 `basename $0` help" >&2
}

#---------------------------------------
# Main
#

if [ $# -eq 0 ]; then
    usage
    exit 1
fi

call_improx $*

