FROM openjdk:8-jre-alpine

WORKDIR /data

ADD target/encryption-agent.jar .
ADD entry.sh .

VOLUME /opt/data/password
VOLUME /opt/data/salt

ENTRYPOINT ["./entry.sh"]
