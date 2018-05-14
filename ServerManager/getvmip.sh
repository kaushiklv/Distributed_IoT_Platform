#!/bin/bash
echo "inside the ip script"
#ifconfig eth1 | grep "inet addr" | sed -E 's/^.*addr:(.*) .*$/\1/g' | cut -f1 -d' ' > /vagrant/MyVmIp.txt
#my_ip=$(ip route get 8.8.8.8 | awk 'NR==1 {print $NF}') > /vagrant/MyVmIp.txt
# my_ip=$(ip route get 8.8.8.8 | awk 'NR==1 {print $NF}') 


my_ip=$(ifconfig | grep -o "192".* | cut -d ' ' -f 1)


echo $my_ip"_vagrant_vagrant" > /home/vagrant/myVMIp.txt
echo $my_ip"_vagrant_vagrant" > /vagrant/myVMIp.txt
