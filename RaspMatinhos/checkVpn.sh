#!/bin/sh

NOME=$(ps -ef | grep -i rasp | grep fNR)
IOT=$(ps -ef | grep java | grep ServidorIOT)
dia=$(date +"%d")


echo $(date)

if [ "$NOME" ]; then
	echo "Vpn Ok"
else
	echo "Vpn NOk"
	ssh -fNR 27015:192.168.0.254:27015 pi@rasp4msmariano.dynv6.net
fi

if [ "$IOT" ]; then
	echo "ServidorIOT Ok"
else
	echo "ServidorIOT NOk"
	#sudo java -jar -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000 /home/pi/Desktop/ServidorIOT.jar &
fi


 