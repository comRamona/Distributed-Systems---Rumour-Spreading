#!/bin/bash
if [ "$1" == "compile" ]; then
    echo "compiling"
    mvn package -Ddir=build
elif [ "$1" == "q1" ]; then
    echo "q1"
    if [ "$4" != "" ]; then
        java -jar build/coursework-2.4.jar q1 "$2" "$3" "$4" "$5"
    else
        java -jar build/coursework-2.4.jar q1 "$2" "$3"
    fi
elif [ "$1" == "q2" ]; then
    echo "q2"
    java -jar build/coursework-2.4.jar q2 "$2" "$3" "$4" "$5"
elif [ "$1" == "q3" ]; then
    echo "q3"
    java -jar build/coursework-2.4.jar q3 "$2" "$3" "$4" "$5"
fi
