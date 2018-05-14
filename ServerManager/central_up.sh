#!bin/bash

#$1--alias $2--ip $3--client_path $4--host_vagrant_folder $5--password

#change hostname in vagrant file
sed -i "s/.*config.vm.hostname.*/\tconfig.vm.hostname = $(date +\"%s%N\")/" $4/Vagrantfile 

sed -i 's#cd .*#cd '$3'#g' $4/run.sh

sed -i 's/echo .* |/echo '$5' |/g' $4/run.sh

sed -i 's/.*config.vm.box.*/config.vm.box = "semifinalbox'$6'"/g' $4/Vagrantfile 

sed -i "s/.*config.vm.hostname.*/\tconfig.vm.hostname = $(date +\"%s%N\")/" $4/Vagrantfile


#sed -i 's/.*config.vm.hostname.*/config.vm.hostname = "semifinalbox'$6'"/g' $4/Vagrantfile 

sed -i 's/add semifinalbox./add semifinalbox'$6'/g' $4/run.sh

#sed -i 's/destroy semifinalbox./destroy semifinalbox'$6'/g' $4/run.sh



#transfer vagrantfile,vagrant,virtualBox
#echo $5 | sudo -S scp -r $4 $1@$2:$3
#sudo scp -r $4 $1@$2:$3

#echo "asa"


#ssh to client
#ssh $1@$2
#sshpass -p $5 ssh $1@$2 'bash -s' < $4/run.sh
#echo "came back"
#sshpass -p $5 ssh $1@$2
#cd $3

#sshpass -p $5 ssh $1@$2

#ssh $1@$2 'bash -s' < run.sh 

#sleep 10s
#install vagrant and virtual box deb
#echo "hello"
#sudo dpkg -i $3/ias/vagrant.deb
#sudo dpkg -i $3/ias/virtualbox.deb

#echo "installed vagrant and virtual box"
#cd to client's host directory
#cd $3/ias

#vagrant up

#vagrant ssh

#echo"in to your vm"

#ifconfig -a



#bash central_up.sh juhi 192.168.43.168 "/home/juhi/vx" "/home/rajat/ias"

#sudo apt-get remove --purge openssh-server
#sudo apt-get install openssh-server
#sudo service ssh start
