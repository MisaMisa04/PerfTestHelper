FROM liferay/jdk21:latest
FROM tomcat:10-jdk21-temurin
#ARG SETENV=src/main/resources/setenv.sh
#COPY ${SETENV} /usr/local/tomcat/bin/setenv.sh
ARG WAR_FILE=target/PerfTestHelper-0.0.1-SNAPSHOT.war
COPY ${WAR_FILE} /usr/local/tomcat/webapps/ROOT.war
