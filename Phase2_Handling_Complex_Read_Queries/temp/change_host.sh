#!/usr/bin/bash

dns1=data-node1
dns2=data-node2
dns3=data-node3
dns4=data-node4
dns5=data-node5

for i in {1..5}
do
  host=dns$i
  scp -i /Users/Dawn/Desktop/aws\ keys/team.pem  ./hosts ubuntu@${!host}:/home/ubuntu
done

# for i in {1..5}
# do
#   host=dns$i
#   # ssh -i $KEY/team.pem ubuntu@${!host} "sudo mv /home/ubuntu/hosts /etc/hosts && cat /etc/hosts"
#   # ssh -i $KEY/team.pem ubuntu@${!host} "echo data-node$i | sudo tee /etc/hostname && sudo service hostname restart && hostname"
#   ssh -i $KEY/team.pem ubuntu@${!host} "echo vm.swappiness = 0 | sudo tee /etc/sysctl.conf > /dev/null && sudo sysctl -p /etc/sysctl.conf"
# done
