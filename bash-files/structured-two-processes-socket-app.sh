#!/bin/sh

## progarm input parameters ##
port="2020"
initiatorMessage="Hi-360T"
messageNumber="10"
delayMilliSeconds="1000"

echo "Concurrent-Players in structured two processes are running ..."

##directory where jar file is located
dir=../target

##jar file name
jar_name=structured-two-processes-socket-app.jar
## jar file name in the program is: structured-two-processes-socket-app.jar if you would like
## change your jar file name please pass its new name as a jar_name to running program.
#jar_name=another-name.jar

java -jar $dir/$jar_name $port $initiatorMessage $messageNumber $delayMilliSeconds $jar_name

/bin/bash
