@servidor= pi@192.168.0.125
del ServidorIOT.jar
ren ServidorIOT-1.jar ServidorIOT.jar
scp  .\ServidorIOT.jar pi@192.168.0.125:/home/pi/Desktop/
ssh pi@192.168.0.125  Desktop/reset.sh