FROM frolvlad/alpine-oraclejdk8:slim
VOLUME /tmp
ADD DoubleData.jar app.jar
RUN sh -c 'touch /app.jar'
ENV JAVA_OPTS=""
EXPOSE 8080
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /app.jar" ]