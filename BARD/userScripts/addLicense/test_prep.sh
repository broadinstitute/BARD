#!/bin/bash

dir=$1

for a in $dir/*.*_
do
    len=${#a}
    let len=len-1
    dest=${a:0:len}

    cp $a $dest
done