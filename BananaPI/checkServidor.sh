#!/bin/sh

IOT=$(ps -ef | grep java | grep ServidorIOT-1)


if [ "$IOT" ]; then
	echo "ServidorIOT Ok"
else
	echo "ServidorIOT NOk"
	sudo java -jar -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000 /home/pi/Desktop/ServidorIOT-1.jar &
fi