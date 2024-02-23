FROM tomcat:9.0.82-jdk11
MAINTAINER Paulo Sobreira
WORKDIR /usr/local/tomcat/webapps
RUN  rm -rf *
ADD target/topwar.war /usr/local/tomcat/webapps/topwar.war
EXPOSE 8080