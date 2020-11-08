#!/bin/sh
## progarm input parameters ##
threadNumber="2"
messageNumber="10"
delayMilliSeconds="1000"
initiatorMessage="Hi-360T"

echo "Concurrent-Players in simple single process are running ..."

##directory where jar file is located
dir="../target"

##jar file name
jar_name=simple-single-process-app.jar
## jar file name in the program is: structured-two-processes-socket-app.jar if you would like
## change your jar file name please pass its new name as a jar_name to running program.
#jar_name=another-name.jar

java -jar $dir/$jar_name $threadNumber $messageNumber $delayMilliSeconds $initiatorMessage $jar_name
##/bin/bash
