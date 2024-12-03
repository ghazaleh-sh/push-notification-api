FROM eclipse-temurin:17-jre-focal

VOLUME /tmp

ENV TZ=Asia/Tehran

RUN  mkdir -p /var/log/push-notification-api
RUN  chmod -R 777 /var/log/push-notification-api


COPY src/main/resources/static/*.cer /var/log/
COPY src/main/resources/static/*.crt /var/log/

RUN for cert in /var/log/*.cer /var/log/*.crt; do \
      keytool -keystore $JAVA_HOME/lib/security/cacerts \
              -storepass changeit \
              -noprompt \
              -trustcacerts \
              -importcert \
              -file "$cert" \
              -alias "$(basename "$cert")"; \
    done && \
    chmod 644 $JAVA_HOME/lib/security/cacerts


COPY target/*.jar push-notification-api-1.1.9-SNAPSHOT.jar
ENTRYPOINT ["java","-Xdebug","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:1516","-jar","/push-notification-api-1.1.9-SNAPSHOT.jar"]