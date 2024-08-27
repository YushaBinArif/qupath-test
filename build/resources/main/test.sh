#!/bin/sh
echo start
expect -c "
    set timeout 1000
    spawn ssh arif.yousha@gpu-a02
    expect \"password:\"
    send \"55nakano\n\"
    expect \"$\ \"
    send \"cd program/ABMILqupath\n\"
    expect \"$\ \"
    send \"conda activate openslide\n\"
    expect \"$\ \"
    send \"sh test.sh $1 1\n\"
    expect \"$\ \"
    send \"sh vis.sh $1\n\"
    expect \"$\ \"
    send \"exit\n\"
    "
expect -c "
    set timeout 1000
    spawn scp -r arif.yousha@gpu-a02:program/ABMILqupath/test_qupath ./../
    expect \"password:\"
    send \"55nakano\n\"
    expect \"Qupath-PT % \"
    "
echo end