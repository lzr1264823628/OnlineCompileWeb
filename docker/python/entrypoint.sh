#!/bin/bash
source_file=$1
python $1 -u >log.txt 2>&1
ExitCode=$?
if [ $ExitCode -ne 0 ]
then
	sed -i '1 i ERROR_FLAG' log.txt && cat log.txt
else
	sed -i '1 i SUCCESS_FLAG' log.txt && cat log.txt
fi
if [ $ExitCode -ne 0 ]
then
	exit 2
fi