FROM openjdk:8
VOLUME /tmp
ADD build/libs/*.jar app.jar
RUN sh -c 'touch /app.jar'
ENV JAVA_OPTS="-Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=8787,suspend=n"
EXPOSE 8080 8787
#ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
CMD ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]