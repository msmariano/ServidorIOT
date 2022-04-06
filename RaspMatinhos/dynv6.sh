#!/bin/sh -e
hostname="raspberrypimsmariano.dynv6.net"
device=$2
token="yL3HuMLSX7yazHawJnki8zcWzfyQjZ"
dia=$(date +"%d")
MES=$(date +"%m")
ANO=$(date +"%Y")
DSEMANA=$(date +"%w")

dia=$(date)

file=$HOME/.dynv6.addr6
[ -e $file ] && old=`cat $file`


if [ -z "$hostname" -o -z "$token" ]; then
  echo "Usage: token=<your-authentication-token> [netmask=64] $0 your-name.dynv6.net [device]"
  exit 1
fi

if [ -z "$netmask" ]; then
  netmask=128
fi

if [ -n "$device" ]; then
  device="dev $device"
fi
address=$(ip -6 addr list scope global $device | grep -v " fd" | sed -n 's/.*inet6 \([0-9a-f:]\+\).*/\1/p' | head -n 1)

if [ -e /usr/bin/curl ]; then
  bin="curl -fsS"
elif [ -e /usr/bin/wget ]; then
  bin="wget -O-"
else
  echo "neither curl nor wget found"
  exit 1
fi

if [ -z "$address" ]; then
  echo date "no IPv6 address found"
  exit 1
fi

# address with netmask
current=$address/$netmask

if [ "$old" = "$current" ]; then
  echo $dia "IPv6 address unchanged" $current
  exit
fi

# send addresses to dynv6
$bin "http://dynv6.com/api/update?hostname=$hostname&ipv6=$current&token=$token"
#$bin "http://ipv4.dynv6.com/api/update?hostname=$hostname&ipv4=auto&token=$token"

# save current address
echo $current > $file
echo $dia $current