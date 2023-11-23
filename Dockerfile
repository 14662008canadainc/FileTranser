FROM maven:3.6.3-openjdk-11
ENV TZ=Asia/Shanghai
RUN ln -sf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
RUN apt-get update && apt -y upgrade
RUN apt-get install -y vim
WORKDIR /psi
USER root
ADD  /target/filetransfer-1.0.0.jar /filetransfer.jar
ADD nvxclouds.net.jks /psi/nvxclouds.net.jks
ADD run.sh /psi/run.sh
ADD .env /psi/.env
RUN mkdir /psi/files
RUN mkdir /psi/downloads
RUN mkdir /psi/uploads
RUN chmod +x /psi/files
RUN chmod +x /psi/downloads
RUN chmod +x /psi/uploads
RUN chmod +x /psi/run.sh
EXPOSE 12443
CMD ["/psi/run.sh"]
