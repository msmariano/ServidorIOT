#!/bin/sh

PPP=$(ps -ef | grep pppd | grep "notty noauth")
teste=$(cat /home/pi/controlevpn.txt | grep 0)
forward=$(cat /proc/sys/net/ipv4/ip_forward | grep 0)

echo $(date)

if [ "$PPP" ]; then
	echo "Vpn Ok"
	echo 1 > controlevpn.txt
	if [ "$teste" ]; then
		echo "Acrestando regras de roteamento"
		sudo service init_forward start
		sudo route add -net 192.168.0.0 netmask 255.255.255.0  gw 192.168.10.250
		sudo iptables -t nat -A POSTROUTING -o wlan0 -j MASQUERADE
		sudo iptables -t nat -A POSTROUTING -o eth0 -j MASQUERADE
	fi
else
	echo "Vpn NOk"
	echo 0 > controlevpn.txt
fi

if [ "$forward" ]; then
	echo "Ativando forward ipv4/roteamento vpn"
	sudo service init_forward start
	sudo route add -net 192.168.0.0 netmask 255.255.255.0  gw 192.168.10.250
        sudo iptables -t nat -A POSTROUTING -o wlan0 -j MASQUERADE
        sudo iptables -t nat -A POSTROUTING -o eth0 -j MASQUERADE
fi
