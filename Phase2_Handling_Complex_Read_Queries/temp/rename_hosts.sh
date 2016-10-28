dns1=data-node1
dns2=data-node2
dns3=data-node3
dns4=data-node4
dns5=data-node5


for i in {1..5}
do
  host=dns$i
  ssh -i /Users/Dawn/Desktop/aws\ keys/team.pem ubuntu@${!host} "sudo mv /home/ubuntu/hosts /etc/hosts && cat /etc/hosts"
  ssh -i /Users/Dawn/Desktop/aws\ keys/team.pem ubuntu@${!host} "echo data-node$i | sudo tee /etc/hostname && sudo service hostname restart && hostname"
  ssh -i /Users/Dawn/Desktop/aws\ keys/team.pem ubuntu@${!host} "echo vm.swappiness = 0 | sudo tee /etc/sysctl.conf > /dev/null && sudo sysctl -p /etc/sysctl.conf"
done
