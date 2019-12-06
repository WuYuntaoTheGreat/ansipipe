#!/usr/local/bin/bash
PROJECT=ansipipe
DEMO=cn.wuyatang.$PROJECT.demo.Main
ANSISH=${0%/*}/ansipipe.sh

./gradlew jar

SHNAME=bash
COMMAND="java -jar build/libs/${PROJECT}*.jar $DEMO"

. $ANSISH 


