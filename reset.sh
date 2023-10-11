#!/bin/sh


PID=$(ps -e -o pid,cmd  | grep 'java -jar -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000 /home/pi/Desktop/ServidorIOT.jar'  | awk '{print $1 " " $2}' | grep java | awk '{print $1}')
#ps -e -o pid,cmd  | grep ServidorIOT.jar | awk '{print $1}'  | head -n -1 | tail -n 1
echo "Reiniciando ServidorIOT PID " "$PID"
if [ "$PID" ]; then
	echo "Parando ServidorIOT"
	sudo kill -9 "$PID"
fi

echo "Aguardando reiniciar"



while true; do

if [ "$(ps -e -o pid,cmd  | grep 'java -jar -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000 /home/pi/Desktop/ServidorIOT.jar'  | awk '{print $1 " " $2}' | grep java | awk '{print $1}')" ]; then
	break
fi

done



echo "ServidorIOT reiniciado com PID " $(ps -e -o pid,cmd  | grep ServidorIOT.jar | awk '{print $1}'  | head -n -1 | tail -n 1)
