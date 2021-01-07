#!/bin/bash

source_file=$1
KOTLINC_LIB_DIR="/opt/kotlinc/lib"
classpath=.:${KOTLINC_LIB_DIR}/kotlin-stdlib-jdk8.jar:${KOTLINC_LIB_DIR}/kotlin-stdlib-jdk7.jar:${KOTLINC_LIB_DIR}/kotlin-stdlib.jar:${KOTLINC_LIB_DIR}/annotations-13.0.jar 
kotlinc -no-stdlib -cp $classpath $1 > log.txt 2>&1
ExitCode=$?
if [ $ExitCode -eq 0 ]
then
    nokt="${source_file%.*}"
    withupper="${nokt^}"
    classname=${withupper}Kt
    java -cp $classpath ${classname} >>log.txt 2>&1
    ExitCode=$?
fi
if [ $ExitCode -ne 0 ]
then
	sed -i '1 i ERROR_FLAG' log.txt
else
	sed -i '1 i SUCCESS_FLAG' log.txt
fi
cat log.txt
if [ $ExitCode -ne 0 ]
then
	exit 2
fi