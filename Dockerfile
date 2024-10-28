FROM adoptopenjdk/openjdk11

VOLUME /tmp

ENV TZ=Asia/Tehran

RUN  mkdir -p /var/log/push-notification-api
RUN  chmod -R 777 /var/log/push-notification-api

COPY target/*.jar push-notification-api-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-Xdebug","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:1516","-jar","/push-notification-api-0.0.1-SNAPSHOT.jar"]