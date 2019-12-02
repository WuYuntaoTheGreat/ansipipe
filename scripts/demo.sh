#!/usr/local/bin/bash
PROJECT=ansipipe
DEMO=cn.wuyatang.$PROJECT.demo.Main

./gradlew jar
./scripts/ansipipe.bash java -jar build/libs/${PROJECT}*.jar $DEMO


