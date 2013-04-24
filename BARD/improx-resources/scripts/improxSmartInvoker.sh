#!/bin/bash

#
# Usage: improxSmartInvoker.sh <TARGET_FILE_PATH>
#
# This script try to invoke TARGET_FILE_PATH by the following invokers.
#
# [grails-improx plugin]
#    If the target is a .groovy file under either a test/unit or a test/integration directory
#    of a Grails project, it's executed by grails's test-app with appropriate test type via
#    interactive mode proxy.
#
# [Grails]
#    If the target is a .groovy file under a test/functional directory of a Grails project,
#    it's executed by grails's test-app with appropriate test type on a new Grails process.
#
# [GroovyServ]
#    If GroovyServ is installed, a target *.groovy is executed by groovyclient of GroovyServ.
#
# [Groovy]
#    A target *.groovy is executed by Groovy.
#

#---------------------------------------
# Config
#

IMPROX_PORT=${IMPROX_PORT:=8096}

# This script expects the commands will find in PATH environment variable.
# If you don't want to use PATH environment variable, change the following lines.
GRAILS_BIN='grails'
GROOVYCLIENT_BIN='groovyclient'
GROOVY_BIN='groovy'

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

check_file() {
    local path="$1"
    [ -f $path ] || die "File not found: $path"
    local extension=`basename $path | sed -e 's/^.*\.//'`
    [ "$extension" = "groovy" ] || die "Not groovy file: ${path}"
}

resolve_class_name() {
    local path="$1"
    echo $path \
        | sed -E 's/.*test\/(unit|integration|functional)\///' \
        | sed -E 's/.groovy$//' \
        | sed -e 's|/|.|g'
}

grails_base_dir() {
    local path="$1"
    echo $path \
        | sed -E 's!/test/(unit|integration|functional)/.*$!!'
}

test_type() {
    local path="$1"
    echo $path \
        | sed -E 's!^.*test/(unit|integration|functional)/.*$!\1!'
}

is_grails_test() {
    local path="$1"
    [ "$path" != "`test_type $path`" ]
    return $?
}

exists_in_path() {
    local bin="$1"
    which "$bin" >/dev/null 2>&1
    return $?
}

exec_command() {
    local command="$*"
    echo "Executing '${command}'..."
    $command
}

usage() {
    echo "usage: `basename $0` <FILE_PATH>" >&2
    echo "examples:"
    echo "  $ `basename $0` /path/to/yourGrailsApp/test/unit/sample/SampleUnitTests.groovy" >&2
    echo "  $ env IMPROX_PORT=8888 `basename $0` /path/to/yourGrailsApp/test/unit/sample/SampleUnitTests.groovy" >&2
}

#---------------------------------------
# Main
#

if [ ! $# -eq 1 ]; then
    usage
    exit 1
fi

path=$1
check_file $path

# Try to invoke as Grails test
if is_grails_test $path; then
    test_type=`test_type $path`
    class_name=`resolve_class_name $path`

    if [ "$test_type" = "functional" ]; then
        work_dir=`grails_base_dir $path`
        (cd $work_dir; exec_command $GRAILS_BIN test-app -echoOut -echoErr ${test_type}: $class_name)
    else
        call_improx test-app -echoOut -echoErr ${test_type}: $class_name
    fi

# Try to invoke as Groovy script
else
    if exists_in_path $GROOVYCLIENT_BIN; then
        exec_command $GROOVYCLIENT_BIN $path
    elif exists_in_path $GROOVY_BIN; then
        exec_command $GROOVY_BIN $path
    else
        die "Cannot invoke: ${path}"
    fi
fi

