#!/bin/zsh
PROJECT=ansipipe
DEMO=cn.wuyatang.$PROJECT.demo.Main
ANSISH=${0%/*}/ansipipe.sh

./gradlew jar

SHNAME=zsh
COMMAND="java -jar build/libs/${PROJECT}*.jar $DEMO"

. $ANSISH 


