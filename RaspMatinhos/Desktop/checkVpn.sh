#!/bin/sh

PPP=$(ps -ef | grep pppd | grep rasp4msmariano)
NOME=$(ps -ef | grep -i rasp | grep NR)
IOT=$(ps -ef | grep java | grep ServidorIOT)
dia=$(date +"%d")


echo $(date)
#echo $PPP
#echo $NOME
#echo $IOT

if [ "$NOME" ]; then
	echo "Nat Iot Ok"
else
	echo "Nat Iot NOk"
#	autossh -fNR 27015:192.168.0.254:27015 pi@rasp4msmariano.dynv6.net
#        autossh -fNR 8080:192.168.0.254:8080 pi@rasp4msmariano.dynv6.net
fi

if [ "$IOT" ]; then
	echo "ServidorIOT Ok"
else
	echo "ServidorIOT NOk"
	sudo java -jar -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000 /home/pi/Desktop/ServidorIOT.jar &
fi

if [ "$PPP" ]; then
	echo "Vpn Matinhos Prado Velho Ok"
else
	echo "Vpn Matinhos Prado Velho NOk"
	#echo $(date) >> /home/pi/teste.txt
	sudo pppd updetach noauth silent nodeflate pty "/usr/bin/ssh root@rasp4msmariano.dynv6.net /usr/sbin/pppd nodetach notty noauth" ipparam vpn 192.168.0.250:192.168.10.250	
	sudo route add -net 192.168.10.0 netmask 255.255.255.0  gw 192.168.0.250
	sudo iptables -t nat -A POSTROUTING -o wlan0 -j MASQUERADE
fi


 
