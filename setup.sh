#!/bin/sh
sudo apt update -y

sudo apt upgrade -y

sudo apt install openjdk-11-jdk-headless -y

sudo apt install maven -y

sudo apt-get install apt-transport-https ca-certificates -y curl software-properties-common

curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add

sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu  $(lsb_release -cs)  stable"

sudo apt-get update -y

sudo apt-get install docker-ce -y

sudo curl -L https://github.com/docker/compose/releases/download/1.23.2/docker-compose-`uname -s`-`uname -m` -o /usr/local/bin/docker-compose

sudo chmod +x /usr/local/bin/docker-compose

docker-compose --version

sudo docker network create localnet
