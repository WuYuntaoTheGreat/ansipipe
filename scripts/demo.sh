#!/bin/sh
PROJECT=ansipipe
DEMO=cn.wuyatang.$PROJECT.demo.Main
ANSISH=./scripts/ansipipe.sh

if [ "$(uname | grep -io '\(cygwin\|mingw\)'" != "" ]; then
    echo "Windows NOT supported!"
    exit 1
elif [ "$(uname | grep -io darwin)" != "" ]; then
    SHNAME=zsh
    EXEC=/bin/zsh
else
    SHNAME=bash
    EXEC=/bin/bash
fi

./gradlew jar

$EXEC -c $ANSISH $SHNAME java -jar build/libs/${PROJECT}*.jar $DEMO


